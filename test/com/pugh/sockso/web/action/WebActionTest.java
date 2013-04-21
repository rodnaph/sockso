
package com.pugh.sockso.web.action;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.tests.SocksoTestCase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class WebActionTest extends SocksoTestCase {

    /**
     *  empty implementation of web action for testing
     * 
     */
    
    class MyWebAction extends BaseAction {
        @Override
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
        
        final List<Track> tracks = wa.getLatestTracks( 10 );
        
        assertNotNull( tracks );
        assertEquals( 2, tracks.size() );
        
        verify( db );
        verify( st );
        verify( rs );

    }
    
}
