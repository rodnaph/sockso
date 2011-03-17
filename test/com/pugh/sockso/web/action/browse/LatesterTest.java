
package com.pugh.sockso.web.action.browse;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.music.Album;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.tests.TestUtils;
import com.pugh.sockso.tests.TestResponse;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.util.Vector;

import static org.easymock.EasyMock.*;

public class LatesterTest extends SocksoTestCase {

    public void testGetLatestArtists() throws SQLException {
        
        final Properties p = createMock( Properties.class );
        expect( p.get(Constants.WWW_BROWSE_LATEST_ARTISTS_COUNT,10) ).andReturn( 10l );
        replay( p );
        
        final ResultSet rs = createNiceMock( ResultSet.class );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( false );
        replay( rs );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        st.setInt( 1, 10 );
        expect( st.executeQuery() ).andReturn( rs ).times( 1 );
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );
        
        final Latester b = new Latester();
        b.setProperties( p );
        b.setDatabase( db );
        
        final Vector<Artist> artists = b.getLatestArtists();
        
        assertNotNull( artists );
        assertEquals( 2, artists.size() );
        
        verify( db );
        verify( st );
        verify( rs );
        verify( p );
        
    }
    
    public void testGetLatestArtistsQuery() throws Exception {
        
        final Database db = new TestDatabase();
        final Latester b = new Latester();
        
        b.setProperties( new StringProperties() );
        b.setDatabase( db );
        b.getLatestArtists();
        
    }

    public void testShowLatest() throws Exception {
        
        final TestResponse res = new TestResponse();
        final Latester b = new Latester();
        final Vector<Track> tracks = new Vector<Track>();
        final Vector<Artist> artists = new Vector<Artist>();
        final Vector<Album> albums = new Vector<Album>();
        final Artist artist = new Artist( 1, "my artist" );
        final Album album = new Album( artist, 1, "my album" );
        final Track track = new Track( artist, null, 1, "my track", "", 1, null );

        tracks.add( track );
        artists.add( artist );
        albums.add( album );
        
        b.setResponse( res );
        b.showLatest( tracks, artists, albums );
        
        final String data = res.getOutput();

        assertTrue( data.contains(artist.getName()) );
        assertTrue( data.contains(track.getName()) );
        
    }

    public void testGetLatestAlbums() throws SQLException {
        
        final Properties p = createMock( Properties.class );
        expect( p.get(Constants.WWW_BROWSE_LATEST_ALBUMS_COUNT,10) ).andReturn( 10l );
        replay( p );
        
        final ResultSet rs = createNiceMock( ResultSet.class );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( false );
        replay( rs );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        st.setInt( 1, 10 );
        expect( st.executeQuery() ).andReturn( rs ).times( 1 );
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );
        
        final Latester b = new Latester();
        b.setProperties( p );
        b.setDatabase( db );
        
        final Vector<Album> albums = b.getLatestAlbums();
        
        assertNotNull( albums );
        assertEquals( 2, albums.size() );
        
        verify( db );
        verify( st );
        verify( rs );
        verify( p );
        
    }

}
