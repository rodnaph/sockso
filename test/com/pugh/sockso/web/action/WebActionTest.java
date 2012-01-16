
package com.pugh.sockso.web.action;

import com.pugh.sockso.Properties;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.web.Request;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import java.util.Vector;

import static org.easymock.EasyMock.*;

public class WebActionTest extends SocksoTestCase {

    /**
     *  empty implementation of web action for testing
     * 
     */
    
    class MyWebAction extends BaseAction {
        public void handleRequest() {}
    }
    
    public void testGetLatestTracks() throws SQLException {
        
        final ResultSet rs = createNiceMock( ResultSet.class );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( false );
        replay( rs );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        st.setInt( 1, 10 );
        expect( st.executeQuery() ).andReturn( rs ).times( 1 );
        st.close();
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );
        
        final MyWebAction wa = new MyWebAction();
        wa.setDatabase( db );
        
        final Vector<Track> tracks = wa.getLatestTracks( 10 );
        
        assertNotNull( tracks );
        assertEquals( 2, tracks.size() );
        
        verify( db );
        verify( st );
        verify( rs );

    }
    
}
