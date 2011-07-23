
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.Utils;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestResponse;

import java.sql.SQLException;

public class ArtistsActionTest extends SocksoTestCase {

    private ArtistsAction action;
    
    private TestResponse res;
    
    private TestDatabase db;

    @Override
    protected void setUp() throws Exception {
        res = new TestResponse();
        db = new TestDatabase();
        db.fixture( "artistsAlbumsAndTracks" );
        action = new ArtistsAction();
        action.setDatabase( db );
        action.setResponse( res );
    }
    
    protected void createArtists( int numOfArtists ) throws SQLException {
        for ( int i=0; i<numOfArtists; i++ ) {
            String name = Utils.getRandomString( 20 );
            db.update( " insert into artists ( name, date_added ) values ( '" +name+ "', '2011-01-01 01:02:03' ) " );
        }
    }
    
    public void testActionCanHandleArtistsUrl() {
        assertTrue( action.canHandle(getRequest( "/api/artists")) );
        assertTrue( action.canHandle(getRequest( "/api/artists?foo=bar")) );
    }
    
    public void testActionDoesntHandleNonArtistsUrls() {
        assertFalse( action.canHandle(getRequest( "/api/artists/123")) );
        assertFalse( action.canHandle(getRequest( "/api/artists/123/tracks")) );
        assertFalse( action.canHandle(getRequest( "/api/albums")) );
    }
    
    public void testArtistsListed() throws Exception {
        action.setRequest(getRequest( "/api/artists" ));
        action.handleRequest();
        assertContains( res.getOutput(), "A Artist" );
        assertContains( res.getOutput(), "Empty Artist" );
    }
    
    public void testArtistListCanBeOffset() throws Exception {
        action.setRequest(getRequest( "/api/artists?offset=1" ));
        action.handleRequest();
        assertNotContains( res.getOutput(), "A Artist" );
        assertContains( res.getOutput(), "Empty Artist" );
    }
    
    public void testArtistListCanBeLimited() throws Exception {
        action.setRequest(getRequest( "/api/artists?limit=1" ));
        action.handleRequest();
        assertContains( res.getOutput(), "A Artist" );
        assertNotContains( res.getOutput(), "Empty Artist" );
    }
    
    public void testLimitIsDefaultWhenNotSpecified() throws Exception {
        createArtists( 110 );
        action.setRequest(getRequest( "/api/artists" ));
        action.handleRequest();
        assertSubstringCount( 100, res.getOutput(), "\"id\":" );
    }
    
    public void testLimitOfMinusOneMeansNoLimitApplies() throws Exception {
        createArtists( 150 );
        action.setRequest(getRequest( "/api/artists?limit=-1" ));
        action.handleRequest();
        assertSubstringCount( 152, res.getOutput(), "\"id\":" ); // 150 + 2 from fixture
    }
    
    public void testArtistsAreListedAlphabeticallyAscending() {
        
    }
    
    public void testArtistsListedIncludeDateArtistWasAdded() throws Exception {
        createArtists( 1 );
        action.setRequest(getRequest( "/api/artists" ));
        action.handleRequest();
        assertContains( res.getOutput(), "2011-01-01" );
    }
    
}
