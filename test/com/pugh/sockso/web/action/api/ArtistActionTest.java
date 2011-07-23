
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestResponse;
import com.pugh.sockso.web.BadRequestException;

import java.io.IOException;

import java.sql.SQLException;

public class ArtistActionTest extends SocksoTestCase {

    private ArtistAction action;
    
    private TestResponse res;
    
    @Override
    protected void setUp() throws Exception {
        res = new TestResponse();
        TestDatabase db = new TestDatabase();
        db.fixture( "artistsAlbumsAndTracks" );
        action = new ArtistAction();
        action.setDatabase( db );
        action.setResponse( res );
    }
    
    public void testActionHandlesUrlForArtistInfo() {
        assertTrue( action.canHandle(getRequest( "/api/artists/123" )) );
        assertTrue( action.canHandle(getRequest( "/api/artists/456" )) );
        assertTrue( action.canHandle(getRequest( "/api/artists/123?foo=bar" )) );
    }
    
    public void testActionDoesntHandleUrlsNotForArtistInfo() {
        assertFalse( action.canHandle(getRequest( "/api/artists/123/tracks" )) );
        assertFalse( action.canHandle(getRequest( "/api/artists" )) );
        assertFalse( action.canHandle(getRequest( "/api/albums/456" )) );
    }
    
    public void testArtistAndAlbumInfoShowsWhenArtistRequested() throws Exception {
        action.setRequest(getRequest( "/api/artists/1" ));
        action.handleRequest();
        assertContains( res.getOutput(), "A Artist" );
        assertContains( res.getOutput(), "A Album" );
        assertContains( res.getOutput(), "Empty Album" );
    }
    
    public void testExceptionThrownWhenInvalidArtistRequested() {
        boolean gotException = false;
        action.setRequest(getRequest( "/api/artists/999" ));
        try { action.handleRequest(); }
        catch ( IOException e ) {}
        catch ( SQLException e ) {}
        catch ( BadRequestException e ) { gotException=true; }
        if ( !gotException ) {
            fail( "Expected BadRequestException when invalid artist ID specified" );
        }
    }
    
}
