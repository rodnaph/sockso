
package com.pugh.sockso.web.action;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Album;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.music.Genre;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestResponse;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;

public class HomerTest extends SocksoTestCase {
    
    public void testGetRecentlyPlayedAlbums() throws SQLException {
        
        final int total = 5;
        
        final ResultSet rs = createNiceMock( ResultSet.class );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( false );
        replay( rs );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        st.setInt( 1, total );
        expect( st.executeQuery() ).andReturn( rs ).times( 1 );
        st.close();
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );
        
        final Homer h = new Homer();
        h.setDatabase( db );
        
        final List<Album> albums = h.getRecentlyPlayedAlbums( total );
        
        assertNotNull( albums );
        assertEquals( 2, albums.size() );
        
        verify( db );
        verify( st );
        verify( rs );
        
        
    }

    public void testGetRecentlyPlayedAlbumsQuery() throws Exception {

        final Database db = new TestDatabase();
        final Homer h = new Homer();
        
        h.setDatabase( db );
        h.getRecentlyPlayedAlbums( 10 );
        
    }
    
    public void testGetRecentlyPlayedTracks() throws SQLException {
        
        final long total = 10;
                
        final ResultSet rs = createNiceMock( ResultSet.class );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( false );
        replay( rs );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        st.setInt( 1, (int) total );
        expect( st.executeQuery() ).andReturn( rs ).times( 1 );
        st.close();
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );
        
        final Homer h = new Homer();
        h.setDatabase( db );
        
        final List<Track> tracks = h.getRecentlyPlayedTracks( 10 );
        
        assertNotNull( tracks );
        assertEquals( 2, tracks.size() );
        
        verify( db );
        verify( st );
        verify( rs );
        
    }

    public void testGetRecentlyPlayedTracksQuery() throws Exception {

        final Database db = new TestDatabase();
        final Homer h = new Homer();
        
        h.setDatabase( db );
        h.getRecentlyPlayedTracks( 10 );
        
    }
    
    public void testGetTopArtists() throws SQLException {
        
        final long total = 10;
        
        final ResultSet rs = createNiceMock( ResultSet.class );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( false );
        replay( rs );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        st.setInt( 1, (int) total );
        expect( st.executeQuery() ).andReturn( rs ).times( 1 );
        st.close();
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );
        
        final Homer h = new Homer();
        h.setDatabase( db );
        
        final List<Artist> artists = h.getTopArtists( 10 );
        
        assertNotNull( artists );
        assertEquals( 2, artists.size() );
        
        verify( db );
        verify( st );
        verify( rs );
        
    }
    
    public void testGetTopArtistsQuery() throws Exception {

        final Database db = new TestDatabase();
        final Homer h = new Homer();
        
        h.setDatabase( db );
        h.getTopArtists( 10 );
        
    }
    
    public void testShowMain() throws Exception {

        final Database db = new TestDatabase();
        final List<Track> recentlyPlayedTracks = new ArrayList<Track>();
        final List<Artist> topArtists = new ArrayList<Artist>();
        final List<Album> recentlyPlayedAlbums = new ArrayList<Album>();
        final TestResponse res = new TestResponse( db );
        final Homer h = new Homer();

        final Artist artist = new Artist( 1, "my ARtiST" );
        final Album album = new Album( artist, 1, "MY alBBuuMM", "year" );
        final Genre genre = new Genre( 3, "my geNRE" );

        Track.Builder builder = new Track.Builder();
        builder.artist( artist )
                .album( album )
                .genre( genre )
                .id(1)
                .name("TRRRAck")
                .number(1)
                .path("")
                .dateAdded(null);
        final Track track = builder.build();
        
        recentlyPlayedTracks.add( track );
        recentlyPlayedAlbums.add( album );
        topArtists.add( artist );
        
        h.setResponse( res );
        h.showMain( recentlyPlayedTracks, topArtists, recentlyPlayedAlbums );
        
        final String data = res.getOutput();
        
        assertTrue( data.contains(track.getName()) );
        assertTrue( data.contains(artist.getName()) );
        
    }
    
}
