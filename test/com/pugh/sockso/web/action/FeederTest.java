/*
 * FeederTest.java
 * 
 * Created on Jul 23, 2007, 9:03:26 PM
 * 
 * Tests the feeder class
 * 
 */

package com.pugh.sockso.web.action;

import com.pugh.sockso.StringProperties;
import com.pugh.sockso.music.Album;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestResponse;
import com.pugh.sockso.web.Server;

import java.util.Date;
import java.util.Vector;

import static org.easymock.EasyMock.*;

public class FeederTest extends SocksoTestCase {

    public void testShowLatest() throws Exception {
        
        final Artist artist = new Artist( 1, "artistFoo" );
        final Album album = new Album( artist, 2, "albumBar", "year");
        final Track track = new Track( artist, album, 3, "track name", "/path", 1, new Date() );
        final Vector<Track> tracks = new Vector<Track>();
        final Server sv = createNiceMock( Server.class );
        final TestResponse res = new TestResponse();

        tracks.add( track );

        final Feeder f = new Feeder();
        f.init( "domain.com" );
        f.setProperties( new StringProperties() );
        f.setResponse( res );
        f.latest( tracks );

        final String data = res.getOutput();
        
        assertTrue( data.contains("artistFoo") );
        assertTrue( data.contains("track name") );

    }
    
}
