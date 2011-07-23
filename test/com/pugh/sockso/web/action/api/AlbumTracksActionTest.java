
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestResponse;

public class AlbumTracksActionTest extends SocksoTestCase {

    private AlbumTracksAction action;
    
    private TestResponse res;
    
    @Override
    protected void setUp() throws Exception {
        TestDatabase db = new TestDatabase();
        db.fixture( "albumTracks" );
        res = new TestResponse();
        action = new AlbumTracksAction();
        action.setDatabase( db );
        action.setResponse( res );
    }
    
    public void testCanhandleReturnsForValidUrls() {
        assertTrue( action.canHandle(getRequest("/api/albums/123/tracks")) );
        assertTrue( action.canHandle(getRequest("/api/albums/123/tracks?foo=bar")) );
    }
    
    public void testCanhandleReturnsFalseForInvalidUrls() {
        assertFalse( action.canHandle(getRequest("/api/albums/asd/tracks?foo=bar")) );
        assertFalse( action.canHandle(getRequest("/api/albums/asd/tracks")) );
        assertFalse( action.canHandle(getRequest("/api/artists/123/tracks")) );
    }
    
    public void testExceptionThrownWhenInvalidAlbumSpecified() {
        boolean gotException = false;
        action.setRequest(getRequest("/api/albums/99999/tracks"));
        try { action.handleRequest(); }
        catch ( Exception e ) {
            gotException = true;
        }
        if ( !gotException ) {
            fail( "Expected exception when invalid artist ID specified" );
        }
    }
    
    public void testAlbumTracksListedWhenValidAlbumSpecified() throws Exception {
        action.setRequest(getRequest( "/api/albums/1/tracks" ));
        action.handleRequest();
        assertContains( res.getOutput(), "My Track 1" );
        assertContains( res.getOutput(), "My Track 2" );
    }
    
    public void testTracksFromOtherAlbumsNotIncluded() throws Exception {
        action.setRequest(getRequest( "/api/albums/1/tracks" ));
        action.handleRequest();
        assertNotContains( res.getOutput(), "My Track 3" );
    }
    
}
