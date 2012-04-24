
package com.pugh.sockso.web.action;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestRequest;



public class FileServerTest extends SocksoTestCase {

    private FileServer action;

    @Override
    protected void setUp() {
        action = new FileServer( null );
    }


    public void testFileServerIgnoresLogins() {
        assertFalse( action.requiresLogin() );
    }

    public void testDoubleDotsAreIgnoredInFilePaths() throws Exception {
        TestRequest req = new TestRequest( "GET /file/some/../../file.txt HTTP/1.0" );
        action.setRequest( req );
        assertEquals( "htdocs/some/file.txt", action.getPathFromRequest() );
    }

}
