/*
 * ServerThreadTest.java
 * 
 * Created on Jul 24, 2007, 12:14:14 AM
 * 
 * Tests the ServerThread class
 * 
 */

package com.pugh.sockso.web;

import com.pugh.sockso.StringProperties;
import com.pugh.sockso.tests.TestUtils;
import com.pugh.sockso.Properties;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.CollectionManager;
import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestRequest;

import java.io.InputStream;
import java.io.OutputStream;

import java.net.Socket;

import static org.easymock.EasyMock.*;

public class ServerThreadTest extends SocksoTestCase {

    private Server server;
    private Database db;
    private Properties p;
    private Resources r;
    private CollectionManager cm;
    
    public void setUp() {
        server = createMock( Server.class );
        db = createMock( Database.class );
        p = new StringProperties();
        r = createMock( Resources.class );
        cm = createNiceMock( CollectionManager.class );
    }
    
    public void tearDown() {
        server = null;
        db = null;
        p = null;
        r = null;
    }
    
    class MyServerThread extends ServerThread {
        protected boolean wasRun = false;
        public MyServerThread( Server sv, Socket cl, Database db, Properties p, Resources r ) {
            super( sv, db, p, r, null, null );
        }
    }
    
    public void testConstructor() {
        
        Socket client = getSocket( "" );
        
        ServerThread st = new ServerThread( server, db, p, r, null, null );
        assertNotNull( st );
        
    }

    /**
     *  returns a socket whose getInmputStream will produce
     *  a stream with the specified data
     * 
     *  @param theData the input data
     * 
     */
    
    private Socket getSocket( String theData ) {
        final String theHttpData = theData;
        return new Socket() {
            @Override
            public InputStream getInputStream() {
                return TestUtils.getInputStream(theHttpData);
            }
            @Override
            public OutputStream getOutputStream() {
                return TestUtils.getOutputStream();
            }
        };
    }

}
