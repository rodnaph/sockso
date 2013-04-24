
package com.pugh.sockso.web.action.browse;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestResponse;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;

public class ByLettererTest extends SocksoTestCase {

    public void testGetArtistsByLetter() throws SQLException {
        
        final ResultSet rs = createNiceMock( ResultSet.class );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( false );
        replay( rs );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        st.setString( 1, "a%" );
        expect( st.executeQuery() ).andReturn( rs ).times( 1 );
        st.close();
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );
        
        final ByLetterer b = new ByLetterer();
        b.setDatabase( db );
        
        final List<Artist> artists = b.getArtistsByLetter( "a" );
        
        assertNotNull( artists  );
        assertEquals( 2, artists.size() );
        
        verify( db );
        verify( st );
        verify( rs );
        
    }
    
    public void testGetArtistsByLetterQuery() throws Exception {
        
        final Database db = new TestDatabase();
        final ByLetterer b = new ByLetterer();
        
        b.setDatabase( db );
        b.getArtistsByLetter( "A" );
        
    }
    
    public void testGetArtistsByLetterBlank() throws SQLException {
        
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
        
        final ByLetterer b = new ByLetterer();
        b.setDatabase( db );
        
        final List<Artist> artists = b.getArtistsByLetter( "" );
        
        assertNotNull( artists  );
        assertEquals( 2, artists.size() );
        
        verify( db );
        verify( st );
        verify( rs );
        
    }

    public void testShowByLetter() throws Exception {
        
        final TestResponse res = new TestResponse();
        final ByLetterer b = new ByLetterer();
        final List<Artist> artists = new ArrayList<Artist>();
        final Artist artist = new Artist( 1, "my artist" );
        final String letter = "G";

        artists.add( artist );
        
        b.setResponse( res );
        b.showByLetter( letter, artists );
        
        final String data = res.getOutput();

        assertTrue( data.contains(artist.getName()) );
        
    }

}
