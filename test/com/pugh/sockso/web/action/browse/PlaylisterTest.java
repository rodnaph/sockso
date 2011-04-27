
package com.pugh.sockso.web.action.browse;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Album;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.music.Playlist;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestResponse;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.web.BadRequestException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.util.Vector;
import java.util.Date;

import static org.easymock.EasyMock.*;

public class PlaylisterTest extends SocksoTestCase {

    public void testGetPlaylist() throws SQLException {
        
        // test bad playlist id
        
        ResultSet rs = createMock( ResultSet.class );
        expect( rs.next() ).andReturn( false );
        rs.close();
        
        PreparedStatement st = createMock( PreparedStatement.class );
        expect( st.executeQuery() ).andReturn( rs );
        st.setInt( 1, 123 );
        st.close();
        
        Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        
        replay( rs );
        replay( st );
        replay( db );
        
        final Playlister b = new Playlister();
        b.setDatabase( db );
        boolean gotException = false;
        
        try {
            b.getPlaylist( 123 );
        }
        catch ( final BadRequestException e ) {
            gotException = true;
        }
        
        assertTrue( gotException );

        verify( rs );
        verify( st );
        verify( db );
        
        // test good playlist id
        
        ResultSet rs2 = createMock( ResultSet.class );
        expect( rs2.next() ).andReturn( true );
        expect( rs2.getInt("id") ).andReturn( 1 );
        expect( rs2.getString("name") ).andReturn( "foo" );
        rs2.close();
        
        PreparedStatement st2 = createMock( PreparedStatement.class );
        expect( st2.executeQuery() ).andReturn( rs2 );
        st2.setInt( 1, 123 );
        st2.close();
        
        Database db2 = createMock( Database.class );
        expect( db2.prepare((String)anyObject()) ).andReturn( st2 ).times( 1 );
        
        replay( rs2 );
        replay( st2 );
        replay( db2 );
        
        final Playlister b2 = new Playlister();
        b2.setDatabase( db2 );
        
        try {
            Playlist p = b2.getPlaylist( 123 );
            assertNotNull( p );
        }
        catch ( final BadRequestException e ) {
            fail( e.getMessage() );
        }

    }
    
    public void testGetPlaylistTracks() throws SQLException {
        
        final ResultSet rs = createNiceMock( ResultSet.class );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( false );
        replay( rs );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        expect( st.executeQuery() ).andReturn( rs ).times( 1 );
        st.close();
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );
        
        final Playlister b = new Playlister();
        b.setDatabase( db );
        
        final Vector<Track> tracks = b.getPlaylistTracks( 123 );
        
        assertNotNull( tracks );
        assertEquals( 2, tracks.size() );
        
        verify( db );
        verify( st );
        verify( rs );
        
    }
    
    public void testGetPlaylistTracksQuery() throws Exception {
        
        final Database db = new TestDatabase();
        final Playlister b = new Playlister();
        
        b.setDatabase( db );
        b.getPlaylistTracks( -1 );
        
    }
    
    public void testGetPlaylistQuery() throws Exception {
        
        final Database db = new TestDatabase();
        final Playlister b = new Playlister();
        
        b.setDatabase( db );
        try {
            b.getPlaylist( -1 );
        }
        catch ( final BadRequestException e ) {
            // not found is ok
        }
        
    }

    public void testShowPlaylist() throws Exception {
        
        final TestResponse res = new TestResponse();
        final Playlister b = new Playlister();
        final Vector<Track> tracks = new Vector<Track>();
        final Artist artist = new Artist( 1, "my artist", new Date(), 1, 1 );
        final Album album = new Album( artist, 1, "foo", "year", new Date(), 1, 1 );
        final Track track = new Track( artist, album, 1, "myTrack", "", 1, null );
        final Playlist playlist = new Playlist( 1, "My PLayLIst" );

        tracks.add( track );
        
        b.setResponse( res );
        b.showPlaylist( playlist, tracks );
        
        final String data = res.getOutput();

        assertTrue( data.contains(playlist.getName()) );
        assertTrue( data.contains(track.getName()) );
        
    }

}
