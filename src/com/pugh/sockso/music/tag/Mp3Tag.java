/*
 * Mp3Tag.java
 * 
 * Created on Jun 9, 2007, 1:34:34 AM
 * 
 * code originally taken from limewire: http://limewire.com
 * 
 */

package com.pugh.sockso.music.tag;

import java.io.IOException;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.io.RandomAccessFile;

import java.util.Vector;
import java.util.Iterator;

import de.vdheide.mp3.ID3v2;
import de.vdheide.mp3.ID3v2Exception;
import de.vdheide.mp3.ID3v2Frame;
import de.vdheide.mp3.NoID3v2TagException;

public class Mp3Tag extends AudioTag {
    
    public static final String ISO_LATIN_1 = "8859_1";
    public static final String UNICODE = "Unicode";

    static final String TITLE_ID = "TIT2";
    static final String ARTIST_ID = "TPE1";
    static final String ALBUM_ID = "TALB";
    static final String TRACK_ID = "TRCK";
	
	/**
     *  Returns ID3Data for the file.
     *
     *  We would prefer to use ID3V2 tags, so we try to parse the ID3V2
     *  tags first, and then v1 to get any missing tags.
     * 
     */
    
    public void parse( final File file ) throws IOException {
        
        parseID3v2Data( file );
        parseID3v1Data( file );
        
    }

    /**
     *  Parses the file's id3 data.
     * 
     */
    
    private void parseID3v1Data( final File file ) {
        
        // not long enough for id3v1 tag?
        if( file.length() < 128 )
            return;
        
        RandomAccessFile randomAccessFile = null;        
        
        try {
            
            randomAccessFile = new RandomAccessFile(file, "r");
            final long length = randomAccessFile.length();
            randomAccessFile.seek(length - 128);
            final byte[] buffer = new byte[30];
            
            // If tag is wrong, no id3v1 data.
            randomAccessFile.readFully(buffer, 0, 3);
            String tag = new String(buffer, 0, 3);
            if ( !tag.equals("TAG") )
                return;
            
            // We have an ID3 AudioTag, now get the parts
            randomAccessFile.readFully(buffer, 0, 30);
            if ( trackTitle.equals("") )
                trackTitle = getString(buffer, 30);
            
            randomAccessFile.readFully(buffer, 0, 30);
            if ( artistTitle.equals("") )
                artistTitle = getString( buffer, 30 );

            randomAccessFile.readFully(buffer, 0, 30);
            if ( albumTitle.equals("") )
                albumTitle = getString(buffer, 30);
                        
            randomAccessFile.readFully(buffer, 0, 30);
            int commentLength;
            	if( buffer[28] == 0 ) {
                    if ( trackNumber == 0 )
                        trackNumber = ByteOrder.ubyte2int( buffer[29] );
                    commentLength = 28;
            	}
            
        }
        catch ( final IOException ignored ) {}
        finally {
            if( randomAccessFile != null )
                try { randomAccessFile.close(); }
                catch ( IOException ignored ) {}
        }
        
    }
    
    /**
	 *  Walks back through the byte array to trim off null characters and
	 *  spaces.
     * 
	 *  @return the number of bytes with nulls and spaces trimmed.
     * 
	 */
    
	private int getTrimmedLength( final byte[] bytes, final int includedLength ) {
	    int i;
	    for ( i=includedLength - 1;
	         (i >= 0) && ((bytes[i] == 0) || (bytes[i] == 32));
	          i-- );
	    //replace the nulls with spaces in the array upto i
	    for ( int j=0; j<=i; j++ ) 
	        if ( bytes[j] == 0 )
	            bytes[j] = (byte) 32;
	    return i + 1;
	}
	
    /**
     *  Helper method to generate a string from an id3v1 filled buffer.
     * 
     */
    
    private String getString( final byte[] buffer, final int length ) {
        try {
            return new String(buffer, 0, getTrimmedLength(buffer, length), ISO_LATIN_1);
        }
        catch ( final UnsupportedEncodingException err ) { /* should never happen */
            return null;
        }
    }

    /**
     *  Generates ID3Data from id3v2 data in the file.
     * 
     */
    
    private void parseID3v2Data( final File file ) {
        
        ID3v2 id3v2Parser = null;

        try { id3v2Parser = new ID3v2(file); }
        catch ( final ID3v2Exception idvx ) { return; }
        catch ( final IOException iox ) { return; }
        catch ( final ArrayIndexOutOfBoundsException ignored ) { return; }

        Vector frames = null;
        try { frames = id3v2Parser.getFrames(); }
        catch ( final NoID3v2TagException ntx ) { return; }
        
        //rather than getting each frame indvidually, we can get all the frames
        //and iterate, leaving the ones we are not concerned with
        for ( Iterator iter=frames.iterator(); iter.hasNext(); ) {
            
            final ID3v2Frame frame = (ID3v2Frame)iter.next();
            final String frameID = frame.getID();
            
            final byte[] contentBytes = frame.getContent();
            String frameContent = null;

            if ( contentBytes.length > 0 )
                try {
                    final String enc = (frame.isISOLatin1()) ? ISO_LATIN_1 : UNICODE;
                    frameContent = new String(contentBytes, enc).trim();
                }
                catch ( final UnsupportedEncodingException err ) { /* "should" never happen */ }

            if( frameContent == null || frameContent.trim().equals("") )
                continue;

            //check which tag we are looking at
            if ( TITLE_ID.equals(frameID) ) 
                trackTitle = frameContent;
            else if ( ARTIST_ID.equals(frameID) ) 
                artistTitle = frameContent;
            else if ( ALBUM_ID.equals(frameID) ) 
                albumTitle = frameContent;
            else if ( TRACK_ID.equals(frameID) )
                try { setTrackNumber(frameContent); }
                catch ( final NumberFormatException ignored ) {} 

        }
        
    }




}
