
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.Utils;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestResponse;

public class TracksActionTest extends SocksoTestCase {

    private TracksAction action;
    
    private TestResponse res;
    
    private TestDatabase db;
    
    @Override
    protected void setUp() throws Exception {
        db = new TestDatabase();
        db.fixture( "artistsAlbumsAndTracks" );
        res = new TestResponse();
        action = new TracksAction();
        action.setDatabase( db );
        action.setResponse( res );
    }
    
    private void createTracks( int total ) throws Exception {
        for ( int i=0; i<total; i++ ) {
            String sql = " insert into tracks ( collection_id, artist_id, album_id, name, path, length, date_added, genre_id ) " +
                         " values ( 1, 1, 1, 'AUTO-" +Utils.getRandomString(20)+ "', '/path.mp3', 0, now(), 1 ) ";
            db.update( sql );
        }
    }
    
    public void testActionHandlesTracksUrl() {
        assertTrue( action.canHandle(getRequest("/api/tracks")) );
        assertTrue( action.canHandle(getRequest("/api/tracks?foo=bar")) );
    }
    
    public void testActionDoesntHandleNonTracksUrls() {
        assertFalse( action.canHandle(getRequest("/api/tracks/123")) );
        assertFalse( action.canHandle(getRequest("/api/albums")) );
    }
    
    public void testTracksAreListedWhenRequested() throws Exception {
        action.setRequest( getRequest("/api/tracks") );
        action.handleRequest();
        assertContains( res.getOutput(), "First Track" );
        assertContains( res.getOutput(), "Second Track" );
        assertContains( res.getOutput(), "Third Track" );
    }
    
    public void testTracksCanBeLimited() throws Exception {
        action.setRequest( getRequest("/api/tracks?limit=2") );
        action.handleRequest();
        assertContains( res.getOutput(), "First Track" );
        assertContains( res.getOutput(), "Second Track" );
        assertNotContains( res.getOutput(), "Third Track" );
    }
    
    public void testTracksCanBeOffsetWithLimit() throws Exception {
        action.setRequest( getRequest("/api/tracks?limit=3&offset=1") );
        action.handleRequest();
        assertNotContains( res.getOutput(), "First Track" );
        assertContains( res.getOutput(), "Second Track" );
        assertContains( res.getOutput(), "Third Track" );
    }
    
    public void testMinusOneMeansNoLimit() throws Exception {
        createTracks( 150 );
        action.setRequest( getRequest("/api/tracks?limit=-1") );
        action.handleRequest();
        assertSubstringCount( 150, res.getOutput(), "AUTO-" );
    }
    
    public void testDefaultLimitUsedWhenNoLimitSpecified() throws Exception {
        createTracks( 150 );
        action.setRequest( getRequest("/api/tracks") );
        action.handleRequest();
        assertSubstringCount( BaseApiAction.DEFAULT_LIMIT - 3, res.getOutput(), "AUTO-" );
    }
    
    public void testArtistAndAlbumInfoIsReturnedWithTracks() throws Exception {
        action.setRequest( getRequest("/api/tracks") );
        action.handleRequest();
        assertContains( res.getOutput(), "A Album" );
        assertContains( res.getOutput(), "A Artist" );
    }
    
}
