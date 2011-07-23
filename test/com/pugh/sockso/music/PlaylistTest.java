/*
 * PlaylistTest.java
 * 
 * Created on Aug 9, 2007, 8:44:41 PM
 * 
 * Tests the Playlist class
 * 
 */

package com.pugh.sockso.music;

import com.pugh.sockso.Utils;
import com.pugh.sockso.web.User;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;

import java.util.Vector;

public class PlaylistTest extends SocksoTestCase {

    private TestDatabase db;
    
    private User user;
    
    @Override
    public void setUp() throws Exception {
        user = new User( 1, "Foo" );
        db = new TestDatabase();
        db.fixture( "playlists" );
    }
    
    private void userPlaylists() throws Exception {
        db.update(" insert into playlists ( name, date_created, date_modified, user_id ) values ( '" +Utils.getRandomString(20)+ "',now(),now(),1 ) " );
    }
    
    public void testConstructor() {
        assertNotNull( new Playlist(1,"foo") );
        assertNotNull( new Playlist(1,"foo",1) );
        assertNotNull( new Playlist(1,"foo",1,null) );
    }

    public void testGetUser() {
        final User user = new User( 1, "foo" );
        final Playlist p = new Playlist( 1, "foo", 1, user );
        assertSame( user, p.getUser() );
    }
    
    public void testGetTrackCount() {
        final Playlist p = new Playlist( 1, "foo", 99 );
        assertEquals( 99, p.getTrackCount() );
    }

    public void testGetSelectTracksSql() {
        final int playlistId = 123;
        final String orderBySql = " where myfield = 1 ";
        final String sql = Playlist.getSelectTracksSql( playlistId, orderBySql );
        assertTrue( sql.matches(".*"+playlistId+".*") );
        assertTrue( sql.matches(".*"+orderBySql+".*") );
    }
    
    public void testFindReturnsPlaylistThatMatchesTheIdSpecified() throws Exception {
        Playlist playlist = Playlist.find( db, 1 );
        assertEquals( playlist.getName(), "Foo Foo" );
    }
    
    public void testFindReturnsNullWhenTheSpecifiedPlaylistDoesNotExist() throws Exception {
        assertNull( Playlist.find(db,123) );
    }
    
    public void testFindallReturnsAllPlaylists() throws Exception {
        Vector<Playlist> playlists = Playlist.findAll( db, 100, 0 );
        assertEquals( 3, playlists.size() );
    }
    
    public void testFindallCanBeOffset() throws Exception {
        Vector<Playlist> playlists = Playlist.findAll( db, 100, 1 );
        assertEquals( 2, playlists.size() );
        assertEquals( "A Playlist", playlists.get(0).getName() );
        assertEquals( "Foo Foo", playlists.get(1).getName() );
    }
    
    public void testFindallCanBeLimited() throws Exception {
        Vector<Playlist> playlists = Playlist.findAll( db, 2, 0 );
        assertEquals( 2, playlists.size() );
        assertEquals( "Bar Bar", playlists.get(0).getName() );
        assertEquals( "A Playlist", playlists.get(1).getName() );
    }
    
    public void testFindallReturnsNewestPlaylistsFirst() throws Exception {
        Vector<Playlist> playlists = Playlist.findAll( db, 100, 0 );
        assertEquals( "Bar Bar", playlists.get(0).getName() );
        assertEquals( "Foo Foo", playlists.get(2).getName() );
    }
    
    public void testFindallWithLimitOfMinusOneMeansNoLimit() throws Exception {
        for ( int i=0; i<200; i++ ) {
            db.update( " insert into playlists ( name, date_created, date_modified ) " +
                       " values ( '" +Utils.getRandomString(20)+ "', now(), now() )" );
        }
        Vector<Playlist> playlists = Playlist.findAll( db, -1, 0 );
        assertEquals( 203, playlists.size() );
    }
    
    public void testFindallReturnsUsersWithPlaylistsTheyHaveCreated() throws Exception {
        Vector<Playlist> playlists = Playlist.findAll( db, 100, 0 );
        assertEquals( "MyUser", playlists.get(1).getUser().getName() );
    }
    
    public void testFindReturnsUserWhoCreatedPlaylistWhenThereIsOne() throws Exception {
        Playlist playlist = Playlist.find( db, 2 );
        assertEquals( "MyUser", playlist.getUser().getName() );
    }
    
    public void testGettracksReturnsTheTracksForThePlaylist() throws Exception {
        Vector<Track> tracks = Playlist.find( db, 2 ).getTracks( db );
        assertEquals( 3, tracks.size() );
    }
    
    public void testGettracksReturnsEmptyWhenThereAreNoTracksForThePlaylist() throws Exception {
        Vector<Track> tracks = Playlist.find( db, 1 ).getTracks( db );
        assertEquals( 0, tracks.size() );
    }
    
    public void testGettracksReturnsEmptyWhenThePlaylistDoesntExistInTheDatabase() throws Exception {
        Vector<Track> tracks = new Playlist(99999,"Foo").getTracks( db );
        assertEquals( 0, tracks.size() );
    }
    
    public void testFindallforuserReturnsPlaylistsForSpecifiedUser() throws Exception {
        userPlaylists();
        Vector<Playlist> tracks = Playlist.findAllForUser( db, user, 100, 0 );
        assertEquals( 2, tracks.size() );
    }
    
    public void testFindallforuserReturnsPlaylistsEmptyWhenNullSpecifiedForUser() throws Exception {
        userPlaylists();
        Vector<Playlist> tracks = Playlist.findAllForUser( db, null, 100, 0 );
        assertEquals( 0, tracks.size() );
    }
    
    public void testFindallforuserCanBeLimited() throws Exception {
        userPlaylists();
        Vector<Playlist> tracks = Playlist.findAllForUser( db, user, 1, 0 );
        assertEquals( 1, tracks.size() );
    }
    
    public void testFindallforuserCanBeOffset() throws Exception {
        userPlaylists();
        Vector<Playlist> tracks = Playlist.findAllForUser( db, user, 100, 1 );
        assertEquals( 1, tracks.size() );
    }
    
    public void testFindallforuserWithLimitOfMinusOneMeansNoLimit() throws Exception {
        userPlaylists();
        Vector<Playlist> tracks = Playlist.findAllForUser( db, user, -1, 0 );
        assertEquals( 2, tracks.size() );
    }
    
    public void testFindallforsiteReturnsOnlySitePlaylists() throws Exception {
        Vector<Playlist> tracks = Playlist.findAllForSite( db, 100, 0 );
        assertEquals( 2, tracks.size() );
    }
    
    public void testFindallforsiteCanBeLimited() throws Exception {
        Vector<Playlist> tracks = Playlist.findAllForSite( db, 1, 0 );
        assertEquals( 1, tracks.size() );
    }
    
    public void testFindallforsiteCanBeOffset() throws Exception {
        Vector<Playlist> tracks = Playlist.findAllForSite( db, 100, 1 );
        assertEquals( 1, tracks.size() );
    }
    
    public void testFindallforsiteWithLimitMinusOneMeansNoLimit() throws Exception {
        Vector<Playlist> tracks = Playlist.findAllForSite( db, -1, 0 );
        assertEquals( 2, tracks.size() );
    }
    
}
