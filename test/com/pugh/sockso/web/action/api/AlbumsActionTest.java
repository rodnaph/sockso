
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestResponse;

public class AlbumsActionTest extends SocksoTestCase {
    
    private AlbumsAction action;
    
    private TestResponse res;
    
    @Override
    protected void setUp() throws Exception {
        TestDatabase db = new TestDatabase();
        db.fixture( "albums" );
        res = new TestResponse();
        action = new AlbumsAction();
        action.setDatabase( db );
        action.setResponse( res );
    }
    
    public void testActionCanHandleAlbumsUrls() {
        assertTrue( action.canHandle(getRequest("/api/albums")) );
        assertTrue( action.canHandle(getRequest("/api/albums?offset=123")) );
    }
    
    public void testActionDoesnthandleNonAlbumsUrls() {
        assertFalse( action.canHandle(getRequest("/api/artists")) );
        assertFalse( action.canHandle(getRequest("/api/albums/123")) );
        assertFalse( action.canHandle(getRequest("/api/albums/foo")) );
    }
    
    public void testAllAlbumsListedWhenRequested() throws Exception {
        action.setRequest(getRequest( "/api/albums" ));
        action.handleRequest();
        assertContains( res.getOutput(), "Another Album" );
        assertContains( res.getOutput(), "Beta Third" );
        assertContains( res.getOutput(), "Zan Album" );
    }
    
    public void testAlbumsCanBeLimited() throws Exception {
        action.setRequest(getRequest( "/api/albums?limit=2" ));
        action.handleRequest();
        assertContains( res.getOutput(), "Another Album" );
        assertContains( res.getOutput(), "Beta Third" );
        assertNotContains( res.getOutput(), "Zan Album" );
    }
    
    public void testAlbumsCanBeOffset() throws Exception {
        action.setRequest(getRequest( "/api/albums?limit=3&offset=1" ));
        action.handleRequest();
        assertNotContains( res.getOutput(), "Another Album" );
        assertContains( res.getOutput(), "Beta Third" );
        assertContains( res.getOutput(), "Zan Album" );
    }
    
    public void testDefaultLimitUsedWhenNoLimitSpecified() {
        
    }
    
    public void testLimitOfMinusOneMeansNoLimit() throws Exception {
        action.setRequest(getRequest( "/api/albums?limit=-1" ));
        action.handleRequest();
        assertContains( res.getOutput(), "Another Album" );
        assertContains( res.getOutput(), "Beta Third" );
        assertContains( res.getOutput(), "Zan Album" );
    }
    
    public void testArtistsListedWithAlbums() throws Exception {
        action.setRequest(getRequest( "/api/albums" ));
        action.handleRequest();
        assertContains( res.getOutput(), "A Artist" );
    }
    
}
