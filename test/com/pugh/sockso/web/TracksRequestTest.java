
package com.pugh.sockso.web;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestRequest;

import java.util.Arrays;

public class TracksRequestTest extends SocksoTestCase {

    private Properties p;
    private TestDatabase db;
    private TestRequest req;

    @Override
    protected void setUp() {
        p = new StringProperties();
        p.set( Constants.WWW_BROWSE_FOLDERS_ENABLED, Properties.YES );
        db = new TestDatabase();
    }
    
    protected TracksRequest getInstance( final String resource ) throws Exception {
        req = new TestRequest( "GET " +resource+ " HTTP/1.1" );
        db.fixture( "artistsAlbumsAndTracks" );
        return new TracksRequest( req, db, p );
    }

    public void testTracksAreFetchedFromUrlParameters() throws Exception {
        TracksRequest tr = getInstance( "/foo/tr1/tr2" );
        Track[] tracks = tr.getRequestedTracks();
        assertEquals( 2, tracks.length );
    }

    public void testTracksAreFetchedFromThePathArgument() throws Exception {
        TracksRequest tr = getInstance( "/foo?path=%2Fmusic%2Ffolder" );
        Track[] tracks = tr.getRequestedTracks();
        assertEquals( 2, tracks.length );
    }

    public void testPathArgumentIgnoredWhenFolderBrowsingIsNotEnabled() throws Exception {
        p.set( Constants.WWW_BROWSE_FOLDERS_ENABLED, Properties.NO );
        TracksRequest tr = getInstance( "/foo?path=%2Fmusic%2Ffolder" );
        Track[] tracks = tr.getRequestedTracks();
        assertEquals( 0, tracks.length );
     }

    public void testTracksCanBeRandomisedUsingOrderbyParameter() throws Exception {
        assertTrue( doRandomTest("random") );
    }

    public void testTracksNotRandomisedByDefault() throws Exception {
        assertFalse( doRandomTest("") );
    }

    // this method tests randomizing of requested tracks. it tries a maximum of
    // 100 times fetching 2 samples and checking if they're as expected.  this
    // could *possibly* fail, and 100 might need to be increased to provide
    // sufficiently low probability of this (or develop a better way of testing
    // the randomness.
    protected boolean doRandomTest( final String orderBy ) throws Exception {
        boolean wereDifferent = false;
        for ( int i=0; i<100; i++ ) {
            db = new TestDatabase();
            TracksRequest tr = getInstance( "/tr1/tr2/tr3" );
            req.setArgument( "orderBy", orderBy );
            Track[] first = tr.getRequestedTracks();
            Track[] second = tr.getRequestedTracks();
            if ( !Arrays.equals(first,second) ) {
                wereDifferent = true;
            }
        }
        return wereDifferent;
    }

    public void testRandomTracksReturnedUseRandomLimitProperty() throws Exception {
        p.set( Constants.WWW_RANDOM_TRACK_LIMIT, 2 );
        TracksRequest tr = getInstance( "/" );
        Track[] tracks = tr.getRandomTracks();
        assertEquals( 2, tracks.length );
     }

    public void testRandomTracksReturnedUseDefaultLimitWhenNotSpecifiedByAProperty() throws Exception {
        TracksRequest tr = getInstance( "/" );
        Track[] tracks = tr.getRandomTracks();
        assertEquals( 3, tracks.length );
    }

    public void testRandomTracksReturnedCanBeFilteredByType() throws Exception {
        TracksRequest tr = getInstance( "" );
        db.update( " update tracks set path = '/music/track.ogg' where id = 2 " );
        req.setArgument( "trackType", "mp3" );
        Track[] tracks = tr.getRandomTracks();
        assertEquals( 2, tracks.length );
    }
    
}
