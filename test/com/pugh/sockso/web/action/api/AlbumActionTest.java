
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestResponse;
import com.pugh.sockso.web.BadRequestException;

public class AlbumActionTest extends SocksoTestCase {
    
    private AlbumAction action;
    
    private TestResponse res;
    
    @Override
    protected void setUp() throws Exception {
        TestDatabase db = new TestDatabase();
        db.fixture( "albums" );
        res = new TestResponse();
        action = new AlbumAction();
        action.setDatabase( db );
        action.setResponse( res );
        
    }
    
    public void testActionHandlesAlbumUrls() {
        assertTrue( action.canHandle(getRequest("/api/albums/123")) );
        assertTrue( action.canHandle(getRequest("/api/albums/123?foo=bar")) );
    }
    
    public void testActionDoesntHandleNonAlbumUrls() {
        assertFalse( action.canHandle(getRequest("/api/albums/1s23")) );
        assertFalse( action.canHandle(getRequest("/api/albums")) );
        assertFalse( action.canHandle(getRequest("/api/artists/456")) );
        assertFalse( action.canHandle(getRequest("/api/albums/456/foobar")) );
    }
    
    public void testAlbumDetailsListedWhenRequested() throws Exception {
        action.setRequest(getRequest( "/api/albums/1" ));
        action.handleRequest();
        assertContains( res.getOutput(), "1" );
        assertContains( res.getOutput(), "Zan Album" );
    }
    
    public void testAlbumDetailsIncludesArtistDetails() throws Exception {
        action.setRequest(getRequest( "/api/albums/1" ));
        action.handleRequest();
        assertContains( res.getOutput(), "A Artist" );
    }
    
    public void testExceptionThrownWhenInvalidArtistIdSpecified() throws Exception {
        boolean gotException = false;
        action.setRequest(getRequest( "/api/albums/999999"));
        try { action.handleRequest(); }
        catch ( BadRequestException e ) { gotException=true; }
        if ( !gotException ) {
            fail( "Expected exception on invalid album id" );
        }
    }
    
}
