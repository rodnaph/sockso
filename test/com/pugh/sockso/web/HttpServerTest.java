
package com.pugh.sockso.web;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.music.CollectionManager;

import java.io.IOException;

import java.net.Socket;
import java.net.ServerSocket;

import junit.framework.TestCase;

import static org.easymock.EasyMock.*;

public class HttpServerTest extends TestCase {

    /**
     *  empty implementation for testing
     * 
     */
    
    class MyHttpServer extends HttpServer {
        public MyHttpServer( final Dispatcher dispatcher, final Database db, final Properties p, final Resources r ) {
            super( 4444, dispatcher, db, p, r );
        }
        public ServerSocket getServerSocket( final int port ) throws IOException {
            return null;
        }
        public String getProtocol() {
            return "http";
        }
    }
        
    public void testGetHost() {
        
        final Properties p = createMock( Properties.class );
        final String expectedIp = "123.435.324.653";
        
        expect( p.get(Constants.SERVER_HOST) ).andReturn( expectedIp );
        replay( p );

        final MyHttpServer s = new MyHttpServer( null, null, p, null );
        final String actualIp = s.getHost();
        
        assertEquals( expectedIp + ":4444", actualIp );
        
        verify( p );

    }
     
    public void testHandleRequest() {
        
        final Socket client = new MySocket();
        final MyHttpServer s = new MyHttpServer( null, null, null, null );
        
        s.handleRequest( client, null );
        
    }
    
    class MySocket extends Socket {
    }
    
}
