
package com.pugh.sockso.web.action;

import com.pugh.sockso.tests.TestResponse;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.tests.TestUtils;
import com.pugh.sockso.web.*;

import java.io.File;
import java.io.IOException;

import java.util.Vector;
import java.util.ArrayList;

public class JsonerTest extends SocksoTestCase {
    
    public void testConvertPath() {
        
        // check forward slashes are preserved
        System.setProperty( "file.separator", "/" );
        String path = "/Users/test/some/path";
        
        assertEquals( path, Jsoner.convertPath(path) );
        
        // check backslashes are converted back
        System.setProperty( "file.separator", "\\" );
        String path2 = "c:/users/test/";
        
        assertEquals( "c:\\users\\test\\", Jsoner.convertPath(path2) );
        
    }
    
    public void testShowTracks() throws IOException {
        
        final Vector<Track> tracks = new Vector<Track>();
        final Jsoner j = new Jsoner( null );
        final Response res = new TestResponse();
        final Track track = TestUtils.getTrack();
        
        tracks.add( track );
        
        j.setResponse( res );
        j.showTracks( tracks );

    }
 
    public void testShowSimilarArtists() throws IOException {
        
        final ArrayList<Artist> artists = new ArrayList<Artist>();
        final Jsoner j = new Jsoner( null );
        final Response res = new TestResponse();
        final Artist artist = TestUtils.getArtist();
        
        artists.add( artist );
        
        j.setResponse( res );
        j.showSimilarArtists( artists );
        
    }
 
    public void testGetOrderedFiles() throws Exception {
        
        final Jsoner j = new Jsoner( null );
        final File[] unordered = new File[] {
            new File( "first.mp3" ),
            new File( "second.mp3" ),
            new File( "abba.mp3" ),
            new File( "1 - a.mp3" ),
            new File( "10 - a.mp3" )
        };
        final File[] ordered = j.getOrderedFiles( unordered );
        
        assertEquals( ordered[0], unordered[3] );
        assertEquals( ordered[1], unordered[4] );
        assertEquals( ordered[2], unordered[2] );
        assertEquals( ordered[3], unordered[0] );
        assertEquals( ordered[4], unordered[1] );

    }

}
