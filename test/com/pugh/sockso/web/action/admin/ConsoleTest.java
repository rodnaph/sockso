
package com.pugh.sockso.web.action.admin;

import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestLocale;
import com.pugh.sockso.tests.TestRequest;
import com.pugh.sockso.tests.TestResponse;

public class ConsoleTest extends SocksoTestCase {

    private TestResponse res;
    
    private Console c;

    private Properties p;

    private TestLocale locale;
    
    @Override
    public void setUp() {
        locale = new TestLocale();
        res = new TestResponse();
        p = new StringProperties();
        c = new Console( null );
        c.setResponse( res );
        c.setProperties( p );
        c.setLocale( locale );
    }

    public void testDefaultRequestShowsTheConsoleHtmlPage() throws Exception {
        c.showConsole();
        String html = res.getOutput();
        assertTrue( html.contains("admin-console") );
    }

    public void testTheSendUrlProcessesAConsoleCommand() throws Exception {
        TestRequest req = new TestRequest( "GET /admin/console/send HTTP/1.0" );
        req.setArgument( "command", "propset foo bar" );
        c.setRequest( req );
        c.handleAdminRequest();
        assertEquals( "bar", p.get("foo") );
    }

    public void testCommandOutputIsSentViaTheResponseObject() throws Exception {
        TestRequest req = new TestRequest( "GET /admin/console/send HTTP/1.0" );
        req.setArgument( "command", "propset foo bar" );
        locale.setString( "con.msg.propertySaved", "property updated" );
        c.setRequest( req );
        c.handleAdminRequest();
        assertContains( res.getOutput(), "property updated" );
    }

}
