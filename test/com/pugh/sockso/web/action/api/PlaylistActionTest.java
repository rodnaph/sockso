
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestResponse;
import com.pugh.sockso.web.BadRequestException;

public class PlaylistActionTest extends SocksoTestCase {

    private PlaylistAction action;
    
    private TestResponse res;
    
    @Override
    protected void setUp() throws Exception {
        TestDatabase db = new TestDatabase();
        db.fixture( "playlists" );
        res = new TestResponse();
        action = new PlaylistAction();
        action.setDatabase( db );
        action.setResponse( res );
    }
    
    public void testActionHandlesPlaylistUrls() {
        assertTrue( action.canHandle(getRequest("/api/playlists/123")) );
        assertTrue( action.canHandle(getRequest("/api/playlists/123?foo=bar")) );
    }
    
    public void testActionDoesNotHandleNonPlaylistUrls() {
        assertFalse( action.canHandle(getRequest("/api/albums/123")) );
        assertFalse( action.canHandle(getRequest("/api/playlists/user")) );
        assertFalse( action.canHandle(getRequest("/api/playlists")) );
    }
    
    public void testPlaylistDetailsReturnedWhenRequested() throws Exception {
        action.setRequest( getRequest("/api/playlists/1") );
        action.handleRequest();
        assertContains( res.getOutput(), "1" );
        assertContains( res.getOutput(), "Foo Foo" );
    }
    
    public void testPlaylistDetailsIncludeUserDetailsWhenItsAUserPlaylist() throws Exception {
        action.setRequest( getRequest("/api/playlists/2") );
        action.handleRequest();
        assertContains( res.getOutput(), "MyUser" );
    }
    
    public void testBadrequestexceptionThrownWhenInvalidPlaylistRequested() throws Exception {
        boolean gotException = false;
        action.setRequest( getRequest("/api/playlists/9999") );
        try { action.handleRequest(); }
        catch ( BadRequestException e ) { gotException=true; }
        if ( !gotException ) {
            fail( "Expected exception on invalid playlist id" );
        }
    }
    
    public void testTracksForPlaylistReturnedWithIt() throws Exception {
        action.setRequest( getRequest("/api/playlists/2") );
        action.handleRequest();
        assertContains( res.getOutput(), "My Track" );
        assertContains( res.getOutput(), "Second Track" );
        assertContains( res.getOutput(), "Third Track" );
    }
    
    public void testTracksForPlaylistHaveArtistsAndAlbumsListed() throws Exception {
        action.setRequest( getRequest("/api/playlists/2") );
        action.handleRequest();
        assertContains( res.getOutput(), "My Album" );
        assertContains( res.getOutput(), "My Artist" );
    }
    
}
