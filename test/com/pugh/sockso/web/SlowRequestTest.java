
package com.pugh.sockso.web;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.SocksoTestModule;

import java.net.*;
import java.io.*;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.hsqldb.lib.StringInputStream;

public class SlowRequestTest extends SocksoTestCase {

    /**
     *  This tests a problem with slow requests where Sockso wouldn't wait for
     *  the data to come through the socket.
     *
     */

    public void testSlowRequest() throws Exception {

        Injector injector = Guice.createInjector( new SocksoTestModule() );
        Socket sock = new Socket() {
            private OutputStream out;
            private InputStream in;
            public OutputStream getOutputStream() {
                if ( out == null ) {
                    out = new StringOutputStream();
                }
                return out;
            }
            public InputStream getInputStream() {
                if ( in == null ) {
                    String req = "GET / HTTP/1.1" +HttpResponse.HTTP_EOL+
                                 "Host: 127.0.0.1" +HttpResponse.HTTP_EOL+
                                 HttpResponse.HTTP_EOL;
                    in = new StringInputStream( req );
                }
                return in;
            }
        };
        
        ServerThread st = injector.getInstance( ServerThread.class );
        st.setClientSocket( sock );
        st.start();

        BufferedReader br = new BufferedReader( new InputStreamReader(sock.getInputStream()) );

        String line = null;
        int chars = 0;

        while ( (line = br.readLine()) != null ) {
            chars += line.length();
        }

        assertTrue( chars > 0 );

    }
    
}
