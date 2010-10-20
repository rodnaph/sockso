/*
 * HttpResponseTest.java
 * 
 * Created on Jul 23, 2007, 11:38:09 PM
 * 
 * Tests the HttpResponse class
 * 
 */

package com.pugh.sockso.web;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestResponse;

import java.io.OutputStream;
import java.io.IOException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Date;
import java.util.Vector;

import org.jamon.Renderer;

import static org.easymock.EasyMock.*;

public class HttpResponseTest extends SocksoTestCase {
    
    private ResponseStream out = null;
    private HttpResponse res;


    @Override
    public void setUp() {
        out = new ResponseStream();
        res = new HttpResponse( out, null, null, null, null, false );
    }
    
    @Override
    public void tearDown() {
        out = null;
    }
    
    public void testConstructor() {
        assertNotNull( res );
    }
    
    public void testAddHeader() {
        res.addHeader( "foo", "bar" );
    }
    
    public void testSendHeaders() {
        res.addHeader( "foo", "bar" );
        res.addCookie( new HttpResponseCookie("foo","bar") );
        res.sendHeaders();
        assertTrue( out.getResponseSize() > 0 );
        final String data = out.getData();
        assertTrue( data.contains("foo:") );
        assertTrue( data.contains("Set-Cookie") );
    }

    public void testSendHeadersNoCookies() {
        res.addCookie( new HttpResponseCookie("foo","bar") );
        res.setCookiesEnabled( false );
        res.sendHeaders();
        final String data = out.getData();
        assertFalse( data.contains("Set-Cookie") );
    }
    
    public void testGetOutputStream() {
        assertNotNull( res.getOutputStream() );
    }

    public void testShowHtml() throws IOException {
        Renderer tpl = createMock( Renderer.class );
        res.showHtml( tpl );
        assertTrue( out.getResponseSize() > 0 );
    }

    public void testShowJson() throws IOException {
        Renderer tpl = createMock( Renderer.class );
        res.showJson( tpl );
        assertTrue( out.getResponseSize() > 0 );
    }

    public void testShowText() throws IOException {
        Renderer tpl = createMock( Renderer.class );
        res.showText( tpl );
        assertTrue( out.getResponseSize() > 0 );
    }

    public void testShowRss() throws IOException {
        Renderer tpl = createMock( Renderer.class );
        res.showRss( tpl );
        assertTrue( out.getResponseSize() > 0 );
    }

    public void testShowTemplateWithContentType() throws IOException {
        Renderer tpl = createMock( Renderer.class );
        res.showTemplate( tpl, "text/plain" );
        assertTrue( out.getResponseSize() > 0 );
    }

    public void testShowTemplate() throws IOException {
        Renderer tpl = getRenderer();
        res.showTemplate( tpl );
        // TODO: not sure how to test this...
        //assertTrue( out.getResponseSize() > 0 );
    }

    private Renderer getRenderer() {
        
        return new Renderer() {
            public String asString() { return ""; }
            public void renderTo( java.io.Writer r ) throws IOException {
                r.write('c');
            }
        };
        
    }
    
    public void testAddCookie() throws IOException {
        
        int count = res.cookies.size();
        HttpResponseCookie cookie = new HttpResponseCookie( "foo", "bar", new Date(), "/" );
        
        res.addCookie( cookie );
        assertEquals( res.cookies.size(), count + 1 );
        
        // add same cookie again, should overwrite old cookie
        res.addCookie( cookie );
        assertEquals( res.cookies.size(), count + 1 );
        
    }
    
    public void testSetStatus() {
        assertEquals( 200, res.status );
        res.setStatus( 300 );
        assertEquals( 300, res.status );
    }
    
    public void testRedirect() throws IOException {
        
        res.redirect( "/" );
        
        assertEquals( 302, res.status );
        
    }
    
    public void testResponseSent() throws IOException {

        assertEquals( false, res.responseSent() );
        res.showHtml( getRenderer() );
        assertEquals( true, res.responseSent() );

        HttpResponse res2 = new HttpResponse( out, null, null, null, null, false );
        assertEquals( false, res2.responseSent() );
        res2.redirect( "/" );
        assertEquals( true, res2.responseSent() );

    }

    public void testGetStatusText() {

        assertEquals( res.getStatusText(200), "Ok" );
        assertEquals( res.getStatusText(202), "Accepted" );

    }

    public void testGetRecentUsers() throws Exception {
        
        final User user = new User( 123, "asdasd" );
        
        final ResultSet rs = createMock( ResultSet.class );
        expect( rs.next() ).andReturn( true ).times( 1 );
        expect( rs.getInt("id") ).andReturn( user.getId() ).times( 1 );
        expect( rs.getString("name") ).andReturn( user.getName() ).times( 1 );
        expect( rs.next() ).andReturn( false ).times( 1 );
        rs.close();
        replay( rs );
        
        final PreparedStatement st = createNiceMock( PreparedStatement.class );
        expect( st.executeQuery() ).andReturn( rs ).times( 1 );
        st.close();
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );
        
        final HttpResponse res = new HttpResponse( null, db, null, null, null, false );
        final Vector<User> users = res.getRecentUsers();
        
        assertNotNull( users );
        assertEquals( 1, users.size() );
        assertEquals( user.getId(), users.elementAt(0).getId() );
        assertEquals( user.getName(), users.elementAt(0).getName() );
        
        verify( db );
        verify( st );
        verify( rs );
        
    }
    
    public void testGetUsersQuery() throws Exception {
     
        final Database db = new TestDatabase();
        final HttpResponse res = new HttpResponse( null, db, null, null, null, false );

        res.getRecentUsers();
        
    }
    
}

class ResponseStream extends OutputStream {

    private StringBuffer sb;
    private int responseSize = 0;

    public ResponseStream() {
        sb = new StringBuffer();
    }

    public void write( int i ) {
        sb.append( (char) i );
        responseSize++;
    }
    
    public int getResponseSize() {
        return responseSize;
    }

    public String getData() {
        return sb.toString();
    }
    
}