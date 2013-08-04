
package com.pugh.sockso.music.tag;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.kc7bfi.jflac.FLACDecoder;
import org.kc7bfi.jflac.metadata.Metadata;
import org.kc7bfi.jflac.metadata.VorbisComment;

import org.apache.log4j.Logger;

import com.pugh.sockso.Utils;

/**
 *  class to read tag information from Flac files
 *
 */

public class FlacTag extends AudioTag {
    
    private static final Logger log = Logger.getLogger( FlacTag.class );
    
    /**
     *  reads the audio comments from a Flac file
     * 
     *  @param file
     * 
     */
    
    public void parse( final File file ) throws IOException {
        
        FileInputStream in = null;
        try {
            in = new FileInputStream( file );
            final FLACDecoder dec = new FLACDecoder( in );

            Metadata[] metadata = dec.readMetadata( dec.readStreamInfo() );

            // look for the vorbis comment
            for ( final Metadata item : metadata ) {

                if ( item.getClass().equals(VorbisComment.class) ) {

                    final VorbisComment comment = (VorbisComment) item;

                    this.artistTitle = getComment( comment, "ARTIST" );
                    this.albumTitle = getComment( comment, "ALBUM" );
                    this.albumArtist = getComment( comment, "ALBUMARTIST" );
                    this.trackTitle = getComment( comment, "TITLE" );
                    this.albumYear = getComment( comment, "DATE" );
                    setTrackNumber( getComment( comment,"TRACKNUMBER") );
                    this.genre = getComment( comment, "GENRE");

                }
            }
        } finally {
            Utils.close(in);
        }
        
    }

    /**
     *  tries to extract a named comment from a VorbisComment.  if the comment
     *  doesn't exist then the empty string is returned
     * 
     *  @param comment
     *  @param name
     * 
     *  @return
     */
    
    protected String getComment( final VorbisComment comment, final String name ) {

        try {

            final String[] comments = comment.getCommentByName( name );

            return ( comments.length > 0 )
                ? comments[ 0 ] : "";

        }

        catch ( final Exception e ){
            return name;
        }

    }

}
