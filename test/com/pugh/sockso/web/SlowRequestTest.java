
package com.pugh.sockso.web;

import com.pugh.sockso.tests.IntegrationTestCase;

import java.net.*;
import java.io.*;

public class SlowRequestTest extends IntegrationTestCase {

    /**
     *  This tests a problem with slow requests where Sockso wouldn't wait for
     *  the data to come through the socket.
     *
     */

    public void testSlowRequest() throws Exception {

        System.out.println( "Starting HTTP server" );

        HttpServer s = getHttpServer();

        System.out.println( "waiting..." );

        Thread.sleep( 1000 );

        Socket sock = new Socket( "127.0.0.1", 4444 );
        OutputStream out = sock.getOutputStream();
        InputStream in = sock.getInputStream();

        System.out.println( "Sockso open, pausing" );

        Thread.sleep( 100 );

        String req = "GET / HTTP/1.1\r\n" +
                     "Host: 127.0.0.1\r\n" +
                     "\r\n";

        System.out.println( "Send HTTP Request" );

        for ( int i=0; i<req.length(); i++ ) {
            out.write( req.charAt(i) );
        }

        System.out.println( "Reading Response" );

        BufferedReader br = new BufferedReader( new InputStreamReader(in) );

        String line = null;
        int chars = 0;

        while ( (line = br.readLine()) != null ) {
            chars += line.length();
        }

        System.out.println( "Read chars: " +chars );
        
        assertTrue( chars > 100 );

    }
    
}
