/*
 * OggTag.java
 * 
 * Created on Jun 9, 2007, 1:28:56 AM
 * 
 *  NB: This code was originally taken from the example code provided
 *  with the jorbis library: http://www.jcraft.com/jorbis/
 * 
 */

package com.pugh.sockso.music.tag;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;

import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.Packet;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.Info;

import org.apache.log4j.Logger;

public class OggTag extends AudioTag {

    private static final Logger log = Logger.getLogger( OggTag.class );
    
    private State state = null;
    private static final int CHUNKSIZE = 4096;

    public void parse( final File file ) throws IOException {

        InputStream in = null;
        
        try {
            
            in = new FileInputStream( file ); 
            state = new State();

            read( in );

            for ( int i=0; i<state.vc.comments; i++ ) {
                
                final String c = (String) state.vc.getComment( i );
                final String[] parts = c.split( "=" );
                final String name = parts.length > 0 ? parts[0].toLowerCase() : "";
                final String value = parts.length > 1 ? parts[1] : "";
                
                if ( name.equals("tracknumber") )
                    setTrackNumber( value );
                else if ( name.equals("artist") )
                    artistTitle = value;
                else if ( name.equals("album") )
                    albumTitle = value;
                else if ( name.equals("title") )
                    trackTitle = value;
                else if ( name.equalsIgnoreCase( "date" ) )
                    albumYear = value;
                else if ( name.equalsIgnoreCase("genre") )
                    this.genre = value;
            }

        }

        finally {
            try{in.close();} catch( final Exception e ){}
        }
        
    }
  
    private void read( final InputStream in ) {

        state.in = in;

        final Page og = new Page();

        int index;
        byte[] buffer;
        int bytes = 0;

        state.oy = new SyncState();
        state.oy.init();

        index = state.oy.buffer( CHUNKSIZE );
        buffer = state.oy.data;
        try {
            bytes = state.in.read( buffer, index, CHUNKSIZE );
        }
        catch ( final Exception e ) {
            log.error( e );
            return;
        }
        state.oy.wrote( bytes );

        if ( state.oy.pageout(og) != 1 ) {
            if( bytes < CHUNKSIZE )
                log.error( "Input truncated or empty." );
            else
                log.error( "Input is not an Ogg bitstream." );
            return;
        }
        state.serial = og.serialno();
        state.os = new StreamState();
        state.os.init( state.serial );

        state.vi = new Info();
        state.vi.init();

        state.vc = new Comment();
        state.vc.init();

        if ( state.os.pagein(og) < 0 ) { 
            log.error( "Error reading first page of Ogg bitstream data." );
            return;
        }

        final Packet header_main = new Packet();

        if ( state.os.packetout(header_main) != 1) { 
            log.error( "Error reading initial header packet." );
            return;
        }

        if ( state.vi.synthesis_headerin(state.vc, header_main) < 0 ) { 
            log.error( "This Ogg bitstream does not contain Vorbis data." );
            return;
        }

        state.mainlen = header_main.bytes;
        state.mainbuf = new byte[ state.mainlen ];
        System.arraycopy(
            header_main.packet_base,
            header_main.packet, 
            state.mainbuf,
            0,
            state.mainlen
        );

        int i = 0;
        Packet header;
        final Packet header_comments = new Packet();
        final Packet header_codebooks = new Packet();

        header = header_comments;
        while ( i < 2 ) {
            while( i < 2 ) {
                int result = state.oy.pageout( og );
                if ( result == 0 ) break; /* Too little data so far */
                else if( result == 1 ) {
                    state.os.pagein(og);
                    while( i < 2 ) {
                        result = state.os.packetout(header);
                        if ( result == 0 ) break;
                        if ( result == -1 ) {
                            log.debug( "Corrupt secondary header." );
                            return;
                        }
                        state.vi.synthesis_headerin( state.vc, header );
                        if ( i == 1 ) {
                            state.booklen = header.bytes;
                            state.bookbuf = new byte[ state.booklen ];
                            System.arraycopy(
                                header.packet_base,
                                header.packet,
                                state.bookbuf,
                                0,
                                header.bytes
                            );
                        }
                        i++;
                        header = header_codebooks;
                    }
                }
            }

            index = state.oy.buffer( CHUNKSIZE );
            buffer = state.oy.data; 
            
            try {
                bytes = state.in.read( buffer, index, CHUNKSIZE );
            }
            catch ( final Exception e ) {
                log.error( e );
                return;
            }

            if ( bytes == 0 && i < 2 ) {
                log.debug("EOF before end of vorbis headers.");
                return;
            }
            
            state.oy.wrote(bytes);
            
        }

        log.debug(state.vi);
        
    }

}


class State {

    private static final Logger log = Logger.getLogger( State.class );
    
    private static final int CHUNKSIZE = 4096;

    SyncState oy;
    StreamState os;
    Comment vc;
    Info vi;
    InputStream in;
    int serial, mainlen, booklen, prevW;
    byte[] mainbuf, bookbuf;
    String lasterror;
    
    final Page og = new Page();

    public int blocksize( Packet p ) {

        int _this = vi.blocksize( p );
        int ret = ( _this + prevW ) / 4;

        if ( prevW == 0 ) {
            prevW = _this;
            return 0;
        }

        prevW = _this;
        return ret;

    }


    public int fetch_next_packet( Packet p ) {

        final int result;
        int index, bytes;
        byte[] buffer;

        result = os.packetout( p );

        if ( result > 0 ) {
            return 1;
        }

        while ( oy.pageout(og) <= 0 ) {
            
            index = oy.buffer( CHUNKSIZE );
            buffer = oy.data; 
            
            try {
                bytes = in.read( buffer, index, CHUNKSIZE );
            }
            catch ( final IOException e ) {
                log.error( e );
                return 0;
            }
            
            if ( bytes > 0 ) {
                oy.wrote( bytes );
                if ( bytes == 0 || bytes == -1 ) {
                    return 0;
                }
            }
            
            os.pagein( og );
        }
        
        return fetch_next_packet(p);
        
    }
    
}
