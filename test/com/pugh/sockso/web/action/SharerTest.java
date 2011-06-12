
package com.pugh.sockso.web.action;

import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.Server;
import com.pugh.sockso.web.User;
import com.pugh.sockso.tests.TestResponse;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestRequest;

import static org.easymock.EasyMock.*;

public class SharerTest extends SocksoTestCase {

    private Locale locale;

    @Override
    public void setUp() {
        
        locale = createNiceMock( Locale.class );
        replay( locale );
        
    }

    public void testHandleRequest() throws Exception {

        TestResponse res = new TestResponse();
        Server sv = createNiceMock( Server.class );
        String skin = "hsdjkahsdjkahsdk";

        Properties p = new StringProperties();
        p.set( "www.skin", skin );

        Request req = new TestRequest( "GET / HTTP/1.1" );

        Sharer s = new Sharer();
        s.setResponse( res );
        s.setRequest( req );
        s.setLocale( locale );
        s.setProperties( p );
        s.handleRequest();

        String data = res.getOutput();

        assertTrue( data.length() > 0 );
        assertTrue( data.contains(skin) );

    }

    public void testRenderingTheSharePage() throws Exception {
        TestResponse res = new TestResponse();
        Sharer s = new Sharer();
        s.setRequest(new TestRequest("/") );
        s.setLocale( locale );
        s.setResponse( res );
        s.setUser( new User(-1,"foo") );
        s.setProperties( new StringProperties() );
        s.showSharePage( new String[] { "ar123" } );
        String data = res.getOutput();
        assertTrue( data.contains("ar123") );
    }

}
