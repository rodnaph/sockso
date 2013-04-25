/*
 * TestUtils.java
 * 
 * Created on Jul 24, 2007, 12:40:05 AM
 * 
 * Utility functions for unit tests
 * 
 */

package com.pugh.sockso.tests;

import com.pugh.sockso.music.Album;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.music.Genre;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.resources.Locale;

import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.InputStream;
import java.io.EOFException;
import java.io.OutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Date;

import static org.easymock.EasyMock.*;

public class TestUtils {

    /**
     *  returns an input stream that will give you
     *  back the specified string
     * 
     *  @param data the stream data
     *  @return the input stream
     * 
     */
    
    public static InputStream getInputStream( String data ) {
        
        final String theData = data;
        
        return new InputStream() {
            final String data = theData;
            int position = 0;
            public int read() throws EOFException {
                if ( position < data.length() )
                    return data.charAt(position++);
                return -1;
            }
            @Override
            public int available() {
                return data.length() - position; // include terminating -1
            }
        };
        
    }
    
    /**
     *  returns an output stream that will swallow anything
     *  it's given.
     * 
     *  @return the output stream
     * 
     */

    public static OutputStream getOutputStream() {
        
        return new OutputStream() {
            public void write( int i ) {}
        };

    }

    /**
     *  returns a 1x1 blank image
     * 
     *  @return
     * 
     */
    
    public static Image getBlankImage() {
        
        final BufferedImage img = new BufferedImage( 1, 1, BufferedImage.OPAQUE );
        
        return img.getScaledInstance( 1, 1, 0 );
        
    }

    /**
     *  returns a mock Locale object
     * 
     *  @return
     * 
     */
    
    public static Locale getLocale() {
        
        final Locale locale = createNiceMock( Locale.class );
        expect( locale.getString((String)anyObject()) ).andReturn( "" ).anyTimes();
        replay( locale );
        
        return locale;
        
    }

    /**
     *  compares 2 files to check their contents match
     * 
     *  @param file1
     *  @param file2
     * 
     *  @throws IOException
     * 
     */
    
    public static boolean compareFiles( final File file1, final File file2 ) throws IOException {

        final FileInputStream in1 = new FileInputStream( file1 );
        final FileInputStream in2 = new FileInputStream( file2 );
        
        int onChar = 1;

        while ( true ) {

            final int c1 = in1.read();
            final int c2 = in2.read();

            if ( c1 == -1 && c2 == -1 ) break; // finished ok!
            
            if ( c1 == -1 || c2 == -1 ) return false;
            
            if ( c1 != c2 ) {
                return false;
            }

            onChar++;

        }
        
        return true;
        
    }

    /**
     *  returns an Artist
     * 
     *  @return
     * 
     */
    
    public static Artist getArtist() {
        
        return new Artist( 123, "foo" );

    }
    
    /**
     *  returns an Album
     * 
     *  @return
     * 
     */
    
    public static Album getAlbum() {
        
        return new Album( getArtist(), 123, "foo", "bar" );
        
    }
    

    /**
     *  returns a Genre
     *
     *  @return
     *
     */

    public static Genre getGenre() {

        return new Genre( 123, "rock" );

    }

    /**
     *  returns a Track
     * 
     *  @return
     * 
     */
    
    public static Track getTrack() {

        Track.Builder builder = new Track.Builder();
        builder.artist( getArtist() )
                .album( getAlbum() )
                .genre( getGenre() )
                .id(123)
                .name("foo")
                .number(1)
                .path("/my/path")
                .dateAdded(new Date());
        
        return builder.build();
    }
    
}
