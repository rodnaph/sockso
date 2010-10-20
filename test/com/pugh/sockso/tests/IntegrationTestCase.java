
package com.pugh.sockso.tests;

import com.pugh.sockso.Properties;
import com.pugh.sockso.DBProperties;
import com.pugh.sockso.db.*;
import com.pugh.sockso.resources.*;
import com.pugh.sockso.web.*;
import com.pugh.sockso.music.*;
import com.pugh.sockso.music.indexing.Indexer;

import static org.easymock.EasyMock.*;

public class IntegrationTestCase extends SocksoTestCase {

    private Database db;
    private Resources r;
    private Properties p;
    private HttpServer httpServer;
    private CollectionManager cm;

    public Database getDatabase() throws Exception {

        if ( db == null ) {
            db = new HSQLDatabase();
            db.connect( null );
        }

        return db;
        
    }
    
    public Resources getResources() throws Exception {
        
        if ( r == null ) {
            r = new FileResources();
            r.init( "en" );
        }
                
        return r;
        
    }
    
    public Properties getProperties() throws Exception {

        if ( p == null ) {
            p = new DBProperties( getDatabase() );
        }
        
        return p;
        
    }
    
    public HttpServer getHttpServer() throws Exception {

        if ( httpServer == null ) {
            httpServer = new HttpServer( 4444, null, getDatabase(), getProperties(), getResources() );
            httpServer.start();
        }

        return httpServer;

    }

    public CollectionManager getCollectionManager() throws Exception {
        
        if ( cm == null ) {
            cm = new DBCollectionManager( getDatabase(), getProperties(), createNiceMock(Indexer.class) );
        }

        return cm;

    }

}
