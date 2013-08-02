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
import com.pugh.sockso.music.Track;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestResponse;
import com.pugh.sockso.tests.TestUtils;

import java.util.ArrayList;
import java.util.List;


public class FeederTest extends SocksoTestCase {

    public void testShowLatest() throws Exception {
        
        final Track track = TestUtils.getTrack();
        
        final List<Track> tracks = new ArrayList<Track>();
        final TestResponse res = new TestResponse();

        tracks.add( track );

        final Feeder f = new Feeder();
        f.init( "domain.com" );
        f.setProperties( new StringProperties() );
        f.setResponse( res );
        f.latest( tracks );

        final String data = res.getOutput();
        
        assertTrue( data.contains(track.getArtist().getName()) );
        assertTrue( data.contains(track.getName()) );

    }
    
}
