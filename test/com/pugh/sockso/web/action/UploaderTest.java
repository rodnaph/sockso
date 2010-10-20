
package com.pugh.sockso.web.action;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestUtils;
import com.pugh.sockso.tests.TestResponse;

public class UploaderTest extends SocksoTestCase {

    public void testIsValidContentType() {
        
        final Uploader u = new Uploader( null );
        
        assertTrue( u.isValidContentType("audio/mpeg") );
        assertTrue( u.isValidContentType("audio/mpg") );
        assertTrue( u.isValidContentType("application/ogg") );
        assertTrue( u.isValidContentType("audio/x-ms-wma") );
        
        assertFalse( u.isValidContentType("text/plain") );
        
    }

    public void testShowUploadForm() throws Exception {

        final Uploader u = new Uploader( null );
        final TestResponse res = new TestResponse();

        u.setLocale( TestUtils.getLocale() );
        u.setProperties( TestUtils.getProperties() );
        u.setResponse( res );
        
        u.showUploadForm();

        assertTrue( res.getOutput().length() > 500 );

    }

}
