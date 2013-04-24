
package com.pugh.sockso.music;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.easymock.EasyMock.*;

public class TrackTest extends SocksoTestCase {

    private Track track;

    private Properties p;

    private User user;

    @Override
    public void setUp() {
        
        final int artistId = 1, albumId = 1, trackId = 1;
        final String artistName = "foo-%/", albumName = "bar", albumYear="baz", trackName = "oof%%^\\+", trackPath = "rab";
        final int trackNumber = 1;
        final Date dateAdded = new Date();
        
        track = new Track(
            new Artist( artistId, artistName ),
            new Album( artistId, artistName, albumId, albumName, albumYear ),
            trackId, trackName, trackPath, trackNumber, dateAdded
        );

        p = new StringProperties();

        user = new User( 1, "name",null, null, 123, "ABC", true );

    }

    public void testGetters() {
        
        final int artistId = -1, albumId = -1, trackId = -1;
        final String artistName = "foo", albumName = "bar", albumYear = "baz", trackName = "oof", trackPath = "rab";
        final int trackNumber = 1;
        final Date dateAdded = new Date();
        final Artist artist = new Artist(artistId,artistName);
        final Album album = new Album( artistId, artistName, albumId, albumName, albumYear );
        final Track track = new Track( artist, album, trackId, trackName, trackPath, trackNumber, dateAdded );
        
        assertEquals( artist, track.getArtist() );
        assertEquals( album, track.getAlbum() );
        assertEquals( trackPath, track.getPath() );
        assertEquals( trackNumber, track.getNumber() );
        
    }
    
    public void testSetPlayCount() {

        final int playCount = 456;
        final Track track = new Track( null, null, -1, "", "", 1, null );

        assertEquals( 0, track.getPlayCount() );
        track.setPlayCount( playCount );
        assertEquals( playCount, track.getPlayCount() );
        
    }

    public void testCreateFromResultSet() {
        
        final ResultSet rs = createNiceMock( ResultSet.class );
        replay( rs );

        try {
            final Track track = Track.createFromResultSet( rs );
            assertNotNull( track );
        }
        
        catch ( SQLException e ) {
            fail( e.getMessage() );
        }
        
    }
    
    public void testCreateListFromResultSet() throws SQLException {

        final String albumName = "my album name";
        final String albumYear = "1984";
        
        // set up result set to return the info for 1 track
        final ResultSet rs = createMock( ResultSet.class );
        expect( rs.next() ).andReturn( true );
        expect( rs.getInt("artistId") ).andReturn( -1 );
        expect( rs.getString("artistName") ).andReturn( albumName );
        expect( rs.getInt("albumId") ).andReturn( -1 );
        expect( rs.getString("albumName") ).andReturn( albumName );
        expect( rs.getString("albumYear") ).andReturn( albumYear );
        expect( rs.getInt("trackId") ).andReturn( -1 );
        expect( rs.getString((String)anyObject()) ).andReturn( "1" ).times( 2 );
        expect( rs.getInt("trackNo") ).andReturn( 1 ).times( 1 );
        expect( rs.getDate("dateAdded") ).andReturn( null ).times( 1 );
        expect( rs.next() ).andReturn( false );
        replay( rs );
        
        try {
            final List<Track> tracks = Track.createListFromResultSet( rs );
            assertNotNull( tracks );
            assertEquals( 1, tracks.size() );
            final Track track = tracks.get( 0 );
            assertEquals( albumName, track.getAlbum().getName() );
            assertEquals( albumYear, track.getAlbum().getYear() );
            verify( rs );
        }
        
        catch ( SQLException e ) {
            fail( e.getMessage() );
        }
        
    }
    
    public void testGetSelectSql() {

        final String sql = Track.getSelectSql();
        
        assertNotNull( sql );
        assertTrue( sql.length() > 0 );
        assertTrue( sql.matches(".*select.*") );
        
    }

    public void testGetSelectFromSql() {
        
        final String sql = Track.getSelectFromSql();

        assertNotNull( sql );
        assertTrue( sql.length() > 0 );
        assertTrue( sql.matches(".*select.*") );
        assertTrue( sql.matches(".*from tracks.*") );

    }
    
    public void testTracks() throws SQLException {
        
        final ResultSet rs = createMock( ResultSet.class );
        expect( rs.next() ).andReturn( false );
        rs.close();
        replay( rs );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        expect( st.executeQuery() ).andReturn( rs );
        st.close();
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st );
        replay( db );
                
        try {
            final List<Track> tracks = Track.getTracks( db, "ar", -1 );
            verify( rs );
            verify( db );
        }
        
        catch ( BadRequestException e ) {
            fail ( e.getMessage() );
        }
        
        catch ( SQLException e ) {
            fail( e.getMessage() );
        }
        
    }
    
    public void getTracksFromPlayArgs() throws SQLException {
        
        ResultSet rs = createMock( ResultSet.class );
        expect( rs.next() ).andReturn( false );
        replay( rs );
        
        Database db = createMock( Database.class );
        expect( db.query((String)anyObject()) ).andReturn( rs );
        replay( db );

        try {
            final String[] args = { "ar123" };
            final List<Track> tracks = Track.getTracksFromPlayArgs( db, args );
            verify( rs );
            verify( db );
        }
        
        catch ( BadRequestException e ) {
            fail ( e.getMessage() );
        }
        
        catch ( SQLException e ) {
            fail( e.getMessage() );
        }
        
    }
    
    public void testGetStreamUrlIncludesSessionDataIfAuthIsRequiredWhenStreaming() {
        p.set( Constants.WWW_USERS_REQUIRE_LOGIN, p.YES );
        p.set( Constants.STREAM_REQUIRE_LOGIN, p.YES );
        String url = track.getStreamUrl( p, user );
        assertContains( url, "sessionId=123" );
        assertContains( url, "sessionCode=ABC" );
    }

    public void testGetStreamUrlDoesntIncludeSessionDataWhenNotRequired() {
        p.set( Constants.WWW_USERS_REQUIRE_LOGIN, p.NO );
        p.set( Constants.STREAM_REQUIRE_LOGIN, p.NO );
        String url = track.getStreamUrl( p, user );
        assertNotContains( url, "sessionId=123" );
        assertNotContains( url, "sessionCode=ABC" );
    }

    public void testGetStreamUrlReturnsUrlWithTrackId() {
        assertContains( track.getStreamUrl(p,user), "/stream/" +track.getId() );
    }

    public void testGetStreamUrlReturnsUrlWithArtistNameAndTrackNameWithSpecialCharactersRemoved() {
        assertEquals( track.getStreamUrl(p,user), "/stream/" +track.getId()+ "/foo-oof" );
    }

    public void testBasePathIsIncludedInGeneratedStreamUrls() {
        p.set( Constants.SERVER_BASE_PATH, "/foo" );
        assertContains( track.getStreamUrl(p,user), "/foo/stream/" );
    }
    
    public void testGettingTracksForAPathReturnsThoseInThatFolderAndSubFolders() throws Exception {
        TestDatabase db = new TestDatabase();
        db.fixture( "tracksForPath" );
        List<Track> tracks = Track.getTracksFromPath( db, "/music" );
        assertEquals( 2, tracks.size() );
    }
    
    public void testFindingTrackWithNonExistantIdReturnsNull() throws Exception {
        assertNull( Track.find(new TestDatabase(),1) );
    }
    
    public void testFindingTrackReturnsIt() throws Exception {
        TestDatabase db = new TestDatabase();
        db.fixture( "singleTrack" );
        Track track = Track.find( db, 1 );
        assertEquals( "My Track", track.getName() );
    }
    
    public void testFindingTrackReturnsArtistObjectWithTrack() throws Exception {
        TestDatabase db = new TestDatabase();
        db.fixture( "singleTrack" );
        Track track = Track.find( db, 1 );
        assertEquals( "My Album", track.getAlbum().getName() );
    }

    public void testFindingTrackReturnsAlbumObjectWithTrack() throws Exception {
        TestDatabase db = new TestDatabase();
        db.fixture( "singleTrack" );
        Track track = Track.find( db, 1 );
        assertEquals( "My Artist", track.getArtist().getName() );
    }
    
    public void testFindallReturnsAllTracksRequested() throws Exception {
        TestDatabase db = new TestDatabase();
        db.fixture( "artistsAlbumsAndTracks" );
        List<Track> tracks = Track.findAll( db, 100, 0 );
        assertEquals( 3, tracks.size() );
    }
    
    public void testFindallCanBeLimited() throws Exception {
        TestDatabase db = new TestDatabase();
        db.fixture( "artistsAlbumsAndTracks" );
        List<Track> tracks = Track.findAll( db, 2, 0 );
        assertEquals( 2, tracks.size() );
    }
    
    public void testFindallCanBeOffset() throws Exception {
        TestDatabase db = new TestDatabase();
        db.fixture( "artistsAlbumsAndTracks" );
        List<Track> tracks = Track.findAll( db, 100, 1 );
        assertEquals( 2, tracks.size() );
    }
    
    public void testFindallWithLimitOfMinusOneMeansNoLimit() throws Exception {
        TestDatabase db = new TestDatabase();
        db.fixture( "artistsAlbumsAndTracks" );
        List<Track> tracks = Track.findAll( db, -1, 0 );
        assertEquals( 3, tracks.size() );
    }

    public void testTracksAreEqualWhenTheyHaveTheSameId() {
        Track track1 = new Track( null,null, 1, "", "", 0, new Date() );        
        Track track2 = new Track( null,null, 1, "", "", 0, new Date() );        
        assertTrue( track1.equals(track2) );
    }

    public void testTracksAreNotEqualWhenTheyHaveDifferentIds() {
        Track track1 = new Track( null,null, 1, "", "", 0, new Date() );        
        Track track2 = new Track( null,null, 2, "", "", 0, new Date() );        
        assertFalse( track1.equals(track2) );
    }

    public void testTracksAreNotEqualToOtherObjects() {
        Track track = new Track( null,null, 1, "", "", 0, new Date() );        
        Album album = new Album( null, 1, "", "" );
        assertFalse( track.equals(album) );
    }

}
