
package com.pugh.sockso.web.action;

import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.Server;
import com.pugh.sockso.web.User;
import com.pugh.sockso.tests.TestResponse;
import com.pugh.sockso.tests.SocksoTestCase;

import static org.easymock.EasyMock.*;

public class SharerTest extends SocksoTestCase {

    private Locale locale;

    @Override
    public void setUp() {
        
        locale = createNiceMock( Locale.class );
        replay( locale );
        
    }

    public void testHandleRequest() throws Exception {

        String host = "domain.com";

        TestResponse res = new TestResponse();
        Server sv = createNiceMock( Server.class );
        String skin = "hsdjkahsdjkahsdk";

        Properties p = createMock( Properties.class );
        expect( p.get((String) anyObject(), (String) anyObject()) ).andReturn( skin ).times( 1 );
        replay( p );

        Request req = createMock( Request.class );
        expect( req.getPlayParams(false) ).andReturn( new String[] { "share", "123" } ).times( 1 );
        replay( req );

        Sharer s = new Sharer( host );
        s.setResponse( res );
        s.setRequest( req );
        s.setLocale( locale );
        s.setProperties( p );
        s.handleRequest();

        String data = res.getOutput();

        assertTrue( data.length() > 0 );
        assertTrue( data.contains(skin) );
        assertTrue( data.contains(host) );

        verify( req );
        verify( p );

    }

    public void testRenderingTheSharePage() throws Exception {
        TestResponse res = new TestResponse();
        Sharer s = new Sharer( "domain.com" );
        s.setLocale( locale );
        s.setResponse( res );
        s.setUser( new User(-1,"foo") );
        s.setProperties( new StringProperties() );
        s.showSharePage( new String[] { "ar123" } );
        String data = res.getOutput();
        assertTrue( data.contains("domain.com") );
        assertTrue( data.contains("ar123") );
    }

}
