
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestResponse;

public class ArtistTracksActionTest extends SocksoTestCase {

    private ArtistTracksAction action;
    
    @Override
    protected void setUp() {
        action = new ArtistTracksAction();
    }
    
    public void testActionHandlesArtistTracksUrls() {
        assertTrue( action.canHandle(getRequest( "/api/artists/123/tracks" )) );
        assertTrue( action.canHandle(getRequest( "/api/artists/456/tracks/" )) );
        assertTrue( action.canHandle(getRequest( "/api/artists/456/tracks?index=foo" )) );
    }
    
    public void testActionDoesntHandleNonArtistTracksUrls() {
        assertFalse( action.canHandle(getRequest( "/api/artists/123" )) );
        assertFalse( action.canHandle(getRequest( "/api/albums/123/tracks" )) );
    }
    
    public void testTracksListedWhenArtistIdFound() throws Exception {
        TestDatabase db = new TestDatabase();
        TestResponse res = new TestResponse();
        db.fixture( "tracksForPath" );
        action.setDatabase( db );
        action.setResponse( res );
        action.setRequest(getRequest( "/api/artists/1/tracks" ));
        action.handleRequest();
        assertContains( res.getOutput(), "My Track 1" );
        assertContains( res.getOutput(), "My Track 2" );
        assertContains( res.getOutput(), "My Track 3" );
    }
    
}
