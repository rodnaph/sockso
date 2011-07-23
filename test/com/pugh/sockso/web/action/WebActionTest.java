
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
    
    public void testGetRandomTracks() throws Exception {

        final ResultSet rs = createNiceMock( ResultSet.class );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( false );
        replay( rs );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        st.setInt( 1, 1 );
        expect( st.executeQuery() ).andReturn( rs ).times( 1 );
        st.close();
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        expect( db.getRandomFunction() ).andReturn( "rand" ).times( 1 );
        replay( db );
        
        final Request req = createMock( Request.class );
        expect( req.getArgument((String)anyObject()) ).andReturn( "" ).anyTimes();
        replay( req );

        final Properties p = createMock( Properties.class );
        expect( p.get( (String)anyObject(), (String)anyObject() ) ).andReturn( "1" ).anyTimes();
        replay( p );

        final MyWebAction wa = new MyWebAction();
        wa.setDatabase( db );
        wa.setProperties( p );
        wa.setRequest( req );
        
        final Vector<Track> tracks = wa.getRandomTracks();
        
        assertNotNull( tracks );
        assertEquals( 2, tracks.size() );
        
        verify( db );
        verify( st );
        verify( rs );

    }

    public void testGetTrackTypeSqlFilter() {
        
        final Request req1 = createMock( Request.class );
        expect( req1.getArgument("trackType") ).andReturn( "mp3,ogg" ).times( 1 );
        replay( req1 );

        final Request req2 = createMock( Request.class );
        expect( req2.getArgument("trackType") ).andReturn( "" ).times( 1 );
        replay( req2 );

        final BaseAction wa = new MyWebAction();

        wa.setRequest( req1 );
        final String filter1 = wa.getTrackTypeSqlFilter();

        wa.setRequest( req2 );
        final String filter2 = wa.getTrackTypeSqlFilter();

        assertTrue( filter1.contains("where") );
        assertTrue( filter1.contains("mp3") );
        assertTrue( filter1.contains("ogg") );
        assertFalse( filter2.contains("mp3") );
        assertFalse( filter2.contains("ogg") );

    }

}
