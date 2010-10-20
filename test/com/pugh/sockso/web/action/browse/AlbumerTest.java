
package com.pugh.sockso.web.action.browse;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Album;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.tests.TestResponse;
import com.pugh.sockso.tests.TestDatabase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.util.Vector;
import java.util.Date;

import static org.easymock.EasyMock.*;

public class AlbumerTest extends SocksoTestCase {

    public void testGetAlbum() throws SQLException, BadRequestException {
        
        // test finding an album
        
        final ResultSet rs = createNiceMock( ResultSet.class );
        expect( rs.next() ).andReturn( true );
        replay( rs );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        st.setInt( 1, 123 );
        expect( st.executeQuery() ).andReturn( rs ).times( 1 );
        st.close();
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );
        
        final Albumer b = new Albumer();
        b.setDatabase( db );
        final Album album = b.getAlbum( 123 );
        
        assertNotNull( album );
        
        verify( db );
        verify( st );
        verify( rs );
        
    }
    
    public void testGetAlbumNotFound() throws SQLException {
        
        // test not finding album
        
        final ResultSet rs = createNiceMock( ResultSet.class );
        expect( rs.next() ).andReturn( false );
        replay( rs );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        st.setInt( 1, 123 );
        expect( st.executeQuery() ).andReturn( rs ).times( 1 );
        st.close();
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );
        
        final Albumer b = new Albumer();
        b.setDatabase( db );
        boolean gotException = false;
        
        try {
            b.getAlbum( 123 );
        }
        catch ( final BadRequestException e ) {
            gotException = true;
        }
        
        assertTrue( gotException );
        
        verify( db );
        verify( st );
        verify( rs );

    }
    
    public void testGetAlbumTracks() throws SQLException {
        
        final ResultSet rs = createNiceMock( ResultSet.class );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( false );
        replay( rs );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        st.setInt( 1, 123 );
        expect( st.executeQuery() ).andReturn( rs ).times( 1 );
        st.close();
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );
        
        final Albumer b = new Albumer();
        b.setDatabase( db );
        
        final Vector<Track> tracks = b.getAlbumTracks( 123 );
        
        assertNotNull( tracks );
        assertEquals( 2, tracks.size() );
        
        verify( db );
        verify( st );
        verify( rs );
        
    }
    
    public void testGetAlbumTracksQuery() throws Exception {
        
        final Database db = new TestDatabase();
        final Albumer b = new Albumer();
        
        b.setDatabase( db );
        b.getAlbumTracks( -1 );
        
    }
    
    public void testGetAlbumQuery() throws Exception {
        
        final Database db = new TestDatabase();
        final Albumer b = new Albumer();
        
        b.setDatabase( db );
        try {
            b.getAlbum( -1 );
        }
        catch ( final BadRequestException e ) {
            // this is ok, just means album not found
        }
        
    }

    public void testShowAlbum() throws Exception {
        
        final TestResponse res = new TestResponse();
        final Albumer b = new Albumer();
        final Vector<Track> tracks = new Vector<Track>();
        final Artist artist = new Artist( 1, "my artist", new Date(), 1, 1 );
        final Album album = new Album( artist, 1, "foo", new Date(), 1, 1 );
        final Track track = new Track( artist, album, 1, "myTrack", "", 1, null );

        tracks.add( track );
        
        b.setResponse( res );
        b.showAlbum( album, tracks );
        
        final String data = res.getOutput();

        assertTrue( data.contains(artist.getName()) );
        assertTrue( data.contains(album.getName()) );
        assertTrue( data.contains(track.getName()) );
        
    }

}
