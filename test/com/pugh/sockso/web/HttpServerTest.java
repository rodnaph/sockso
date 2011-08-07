
package com.pugh.sockso.web;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.inject.SocksoModule;
import com.pugh.sockso.tests.TestOptionSet;

import java.io.IOException;

import java.net.Socket;
import java.net.ServerSocket;

import junit.framework.TestCase;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class HttpServerTest extends TestCase {

    private Injector injector;
    
    @Override
    protected void setUp() {
        injector = Guice.createInjector( new SocksoModule(new TestOptionSet()) );
    }
    
    /**
     *  empty implementation for testing
     * 
     */
    
    class MyHttpServer extends HttpServer {
        public MyHttpServer( final Properties p ) {
            super( injector, p );
        }
        public ServerSocket getServerSocket( final int port ) throws IOException {
            return null;
        }
        public String getProtocol() {
            return "http";
        }
    }
        
    public void testGetHost() {
        
        StringProperties p = new StringProperties();
        String expectedIp = "123.435.324.653";
        
        p.set( Constants.SERVER_HOST, expectedIp );

        final MyHttpServer s = new MyHttpServer( p );
        
        
        assertEquals( expectedIp + ":4444", s.getHost() );

    }
     
    public void testHandleRequest() {
        
        final Socket client = new MySocket();
        final MyHttpServer s = new MyHttpServer( null );
        
        s.handleRequest( client );
        
    }
    
    class MySocket extends Socket {
    }
    
}
