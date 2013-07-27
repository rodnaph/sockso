
package com.pugh.sockso.web.action.browse;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Album;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestResponse;
import com.pugh.sockso.web.BadRequestException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.easymock.EasyMock.*;

public class ArtisterTest extends SocksoTestCase {

    public void testGetArtist() throws SQLException, BadRequestException {
        
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
        
        final Artister b = new Artister();
        b.setDatabase( db );
        final Artist artist = b.getArtist( 123 );
        
        assertNotNull( artist );
        
        verify( db );
        verify( st );
        verify( rs );
        
    }
    
    public void testGetArtistNotFound() throws SQLException {
        
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
        
        final Artister b = new Artister();
        b.setDatabase( db );
        boolean gotException = false;
        
        try {
            b.getArtist( 123 );
        }
        catch ( final BadRequestException e ) {
            gotException = true;
        }
        
        assertTrue( gotException );
        
        verify( db );
        verify( st );
        verify( rs );

    }

    public void testGetArtistAlbums() throws SQLException {
        
        final ResultSet rs = createNiceMock( ResultSet.class );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( false );
        replay( rs );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        st.setInt( 1, 123 );
        st.setInt( 2, 123 );
        expect( st.executeQuery() ).andReturn( rs ).times( 1 );
        st.close();
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );
        
        final Artister b = new Artister();
        b.setDatabase( db );
        
        final List<Album> albums = b.getArtistAlbums( 123 );
        
        assertNotNull( albums );
        assertEquals( 2, albums.size() );
        
        verify( db );
        verify( st );
        verify( rs );
        
    }

    public void testGetArtistAlbumsQuery() throws Exception {
        
        final Database db = new TestDatabase();
        final Artister b = new Artister();
        
        b.setDatabase( db );
        b.getArtistAlbums( -1 );

    }
    
    public void testGetArtistQuery() throws Exception {
        
        final Database db = new TestDatabase();
        final Artister b = new Artister();
        
        b.setDatabase( db );
        try {
            b.getArtist( -1 );
        }
        catch ( final BadRequestException e ) {
            // BadRequestException is ok, the query was good, it just
            // means the artist wasn't found, which is fine.
        }

    }
    
    public void testShowArtist() throws Exception {
        
        final TestResponse res = new TestResponse();
        final Artister b = new Artister();
        final List<Album> albums = new ArrayList<Album>();
        final Artist artist = new Artist( 1, "my artist", new Date(), 1, 1 );
        final Album album = new Album( artist, 1, "foo", "year" );

        albums.add( album );
        
        b.setResponse( res );
        b.showArtist( artist, albums );
        
        final String data = res.getOutput();

        assertTrue( data.contains(artist.getName()) );
        assertTrue( data.contains(album.getName()) );
        
    }

}
