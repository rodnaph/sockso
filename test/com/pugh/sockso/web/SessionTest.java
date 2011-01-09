
package com.pugh.sockso.web;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.tests.SocksoTestCase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.easymock.EasyMock.*;

public class SessionTest extends SocksoTestCase {

    public void testGetCurrentUser() throws SQLException {

        Request req = createMock( Request.class );

        expect( req.getCookie(Session.SESS_ID_COOKIE) ).andReturn( "1" );
        expect( req.getArgument(Session.SESS_ID_COOKIE) ).andReturn( "" );
        expect( req.getCookie(Session.SESS_CODE_COOKIE) ).andReturn( "ABCDEFGHIJ" );
        expect( req.getArgument(Session.SESS_CODE_COOKIE) ).andReturn( "" );
        replay( req );

        final ResultSet rs = createNiceMock( ResultSet.class );
        expect( rs.next() ).andReturn( true );
        expect( rs.getInt("id") ).andReturn( 1 );
        expect( rs.getString("name") ).andReturn( "foo" );
        expect( rs.getString("email") ).andReturn( "me@you.com" );
        rs.close();
        replay( rs );

        final PreparedStatement st = createNiceMock( PreparedStatement.class );
        expect( st.executeQuery() ).andReturn( rs );
        st.close();
        replay( st );

        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );

        final Session sess = new Session( db, req, null );
        User user = sess.getCurrentUser();

        assertEquals( 1, user.getId() );
        assertEquals( "foo", user.getName() );
        assertEquals( "me@you.com", user.getEmail() );
        assertEquals( 1, user.getSessionId() );
        assertEquals( "ABCDEFGHIJ", user.getSessionCode() );

        verify( req );
        verify( db );

    }

    public void testSessionInfo() {

        final int sessionId = 12344;
        final String sessionCode = "HGASJDGAJ";
        final User u = new User( 1, "", "", "", sessionId, sessionCode, true );

        assertEquals( sessionId, u.getSessionId() );
        assertEquals( sessionCode, u.getSessionCode() );

    }

    public void testFetchSessionIdFromCookie() {

        final Session sess = new Session( null, null, null );
        final Request req = createMock( Request.class );
        expect( req.getCookie(Session.SESS_ID_COOKIE) ).andReturn( "123" ).times( 1 );
        expect( req.getArgument(Session.SESS_ID_COOKIE) ).andReturn( "" ).times( 1 );
        replay( req );

        assertEquals( 123, sess.fetchSessionId(req) );

        verify( req );

    }

    public void testFetchSessionIdFromArgument() {

        final Session sess = new Session( null, null, null );
        final Request req = createMock( Request.class );
        expect( req.getCookie(Session.SESS_ID_COOKIE) ).andReturn( "" ).times( 1 );
        expect( req.getArgument(Session.SESS_ID_COOKIE) ).andReturn( "456" ).times( 1 );
        replay( req );

        assertEquals( 456, sess.fetchSessionId(req) );

        verify( req );

    }

    public void testFetchSessionIdInvalid() {

        final Session sess = new Session( null, null, null );
        final Request req = createMock( Request.class );
        expect( req.getCookie(Session.SESS_ID_COOKIE) ).andReturn( "GHASD" ).times( 1 );
        expect( req.getArgument(Session.SESS_ID_COOKIE) ).andReturn( "ASDJK" ).times( 1 );
        replay( req );

        assertEquals( -1, sess.fetchSessionId(req) );

        verify( req );

    }

    public void testFetchSessionCodeFromCookie() {

        final Session sess = new Session( null, null, null );
        final Request req = createMock( Request.class );
        expect( req.getCookie(Session.SESS_CODE_COOKIE) ).andReturn( "GHASD" ).times( 1 );
        expect( req.getArgument(Session.SESS_CODE_COOKIE) ).andReturn( "ASDJK" ).times( 1 );
        replay( req );

        assertEquals( "GHASD", sess.fetchSessionCode(req) );

        verify( req );

    }

    public void testFetchSessionCodeFromArgument() {

        final Session sess = new Session( null, null, null );
        final Request req = createMock( Request.class );
        expect( req.getCookie(Session.SESS_CODE_COOKIE) ).andReturn( "" ).times( 1 );
        expect( req.getArgument(Session.SESS_CODE_COOKIE) ).andReturn( "ASDJK" ).times( 1 );
        replay( req );

        assertEquals( "ASDJK", sess.fetchSessionCode(req) );

        verify( req );

    }

    public void testFetchSessionCodeBlank() {

        final Session sess = new Session( null, null, null );
        final Request req = createMock( Request.class );
        expect( req.getCookie(Session.SESS_CODE_COOKIE) ).andReturn( "" ).times( 1 );
        expect( req.getArgument(Session.SESS_CODE_COOKIE) ).andReturn( "" ).times( 1 );
        replay( req );

        assertEquals( "", sess.fetchSessionCode(req) );

        verify( req );

    }
    
}
