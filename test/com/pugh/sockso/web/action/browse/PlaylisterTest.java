
package com.pugh.sockso.web.action.browse;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestResponse;
import com.pugh.sockso.web.BadRequestException;

public class PlaylisterTest extends SocksoTestCase {

    private Playlister action;
    
    private TestResponse res;
    
    @Override
    protected void setUp() throws Exception {
        TestDatabase db = new TestDatabase();
        db.fixture( "playlists" );
        res = new TestResponse();
        action = new Playlister();
        action.setDatabase( db );
        action.setResponse( res );
    }

    public void testBadrequestExceptionThrownWhenInvalidPlaylustRequested() throws Exception {
        action.setRequest( getRequest("/browse/playlist/999") );
        boolean gotException = false;
        try { action.handleRequest(); }
        catch ( BadRequestException e ) { gotException=true; }
        if ( !gotException ) {
            fail( "Expected BadRequestException on invalid playlist" );
        }
    }
    
    public void testPlaylistRenderedWithTracksWhenRequested() throws Exception {
        action.setRequest( getRequest("/browse/playlist/2") );
        action.handleRequest();
        assertContains( res.getOutput(), "A Playlist" );
        assertContains( res.getOutput(), "My Track" );
        assertContains( res.getOutput(), "Third Track" );
    }
    
}
