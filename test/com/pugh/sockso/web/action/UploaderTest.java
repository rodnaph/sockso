
package com.pugh.sockso.web.action;

import com.pugh.sockso.StringProperties;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestUtils;
import com.pugh.sockso.tests.TestResponse;

public class UploaderTest extends SocksoTestCase {

    public void testShowUploadForm() throws Exception {

        final Uploader u = new Uploader( null );
        final TestResponse res = new TestResponse();

        u.setLocale( TestUtils.getLocale() );
        u.setProperties( new StringProperties() );
        u.setResponse( res );
        
        u.showUploadForm();

        assertTrue( res.getOutput().length() > 500 );

    }

}
