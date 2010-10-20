
package com.pugh.sockso.music;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.tests.SocksoTestCase;

import java.sql.ResultSet;
import java.sql.PreparedStatement;

import java.util.Vector;

import static org.easymock.EasyMock.*;

public class MusicSearchTest extends SocksoTestCase {
    
    public void testConstructor() {
        
        final MusicSearch ms = new MusicSearch( null );
        
        assertNotNull( ms );
        
    }

    public void testSearch() throws Exception {
        
        final ResultSet rs = createMock( ResultSet.class );
        expect( rs.next() ).andReturn( true );
        expect( rs.getString("type") ).andReturn( "ar123" );
        expect( rs.getInt("id") ).andReturn( 123 );
        expect( rs.getString("name") ).andReturn( "myArtist" );
        expect( rs.next() ).andReturn( false );
        rs.close();
        replay( rs );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        expect( st.executeQuery() ).andReturn( rs );
        st.close();
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st );
        expect( db.escape((String)anyObject()) ).andReturn( "" ).anyTimes();
        replay( db );
        
        final MusicSearch ms = new MusicSearch( db );
        
        final Vector<MusicItem> items = ms.search( "something" );
        
        verify( db );
        verify( st );
        
        assertNotNull( items );
        assertEquals( 1, items.size() );
        assertEquals( "myArtist", items.elementAt(0).getName() );
        
    }
    
}
