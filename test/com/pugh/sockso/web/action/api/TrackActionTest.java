
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestResponse;
import com.pugh.sockso.web.BadRequestException;
import java.io.IOException;

import java.sql.SQLException;

public class TrackActionTest extends SocksoTestCase {

    private TrackAction action;
    
    private TestDatabase db;
    
    @Override
    protected void setUp() {
        db = new TestDatabase();
        action = new TrackAction();
        action.setDatabase( db );
    }
    
    public void testTracksActionRespondsToTrackUrls() {
        assertTrue( action.canHandle(getRequest( "/api/tracks/123" )) );
        assertTrue( action.canHandle(getRequest( "/api/tracks/456" )) );
        assertTrue( action.canHandle(getRequest( "/api/tracks/789?limit=123" )) );
    }
    
    public void testTrackActionDoesntRespondToNonTrackActions() {
        assertFalse( action.canHandle(getRequest( "/api/tracks" )) );
        assertFalse( action.canHandle(getRequest( "/api/tracks/asd" )) );
    }
    
    public void testHandlerequestThrowsExceptionWhenTrackNotFound() {
        boolean gotException = false;
        try {
            action.setRequest(getRequest( "/api/tracks/123" ));
            action.handleRequest();
        }
        catch ( IOException e ) {}
        catch ( SQLException e ) {}
        catch ( BadRequestException e ) { gotException = true; }
        if ( !gotException ) {
            fail( "Expected exception on invalid track id" );
        }
    }
    
    public void testTrackJsonReturnedOnValidRequest() throws Exception {
        TestResponse res = new TestResponse();
        db.fixture( "singleTrack" );
        action.setResponse( res );
        action.setRequest(getRequest( "/api/tracks/1" ));
        action.handleRequest();
        assertContains( res.getOutput(), "My Track" );
        assertContains( res.getOutput(), "My Album" );
        assertContains( res.getOutput(), "My Artist" );
    }
    
}
