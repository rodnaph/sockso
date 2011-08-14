
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.music.Artist;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestResponse;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.RelatedArtists;

import java.util.Date;

import static org.easymock.classextension.EasyMock.*;

public class ArtistRelatedActionTest extends SocksoTestCase {

    private ArtistRelatedAction action;
    
    private TestResponse res;

    @Override
    protected void setUp() throws Exception {
        RelatedArtists related = createMock( RelatedArtists.class );
        Artist[] artists = new Artist[] {
            new Artist( 1, "FooFoo", new Date(0), 0, 0 ),
            new Artist( 2, "BarBar", new Date(0), 0, 0 )
        };
        expect( related.getRelatedArtistsFor(1) ).andReturn( artists );
        expect( related.getRelatedArtistsFor(99) ).andThrow( new BadRequestException("") );
        replay( related );
        res = new TestResponse();
        action = new ArtistRelatedAction( related );
        action.setResponse( res );
    }
    
    public void testActionHandlesRelatedArtistUrls() {
        assertTrue( action.canHandle(getRequest("/api/artists/123/related")) );
        assertTrue( action.canHandle(getRequest("/api/artists/123/related?foo=bar")) );
    }
    
    public void testActionDoesntHandleNonRelatedArtistUrls() {
        assertFalse( action.canHandle(getRequest("/api/artists/asd/related")) );
        assertFalse( action.canHandle(getRequest("/api/albums/123/related")) );
        assertFalse( action.canHandle(getRequest("/api/artists/123")) );
    }
    
    public void testRelatedArtistsDisplayedForArtistRequested() throws Exception {
        action.setRequest(getRequest( "/api/artists/1/related" ));
        action.handleRequest();
        assertContains( res.getOutput(), "FooFoo" );
        assertContains( res.getOutput(), "BarBar" ); 
    }
    
    public void testExceptionThrownOnInvalidArtistId() throws Exception {
        boolean gotException = false;
        try {
            action.setRequest(getRequest( "/api/artists/99/related" ));
            action.handleRequest();
        }
        catch ( BadRequestException e ) {
            gotException = true;
        }
        if ( !gotException ) {
            fail( "Expected invalid artist ID to throw exception" );
        }
    }
    
}
