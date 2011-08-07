
package com.pugh.sockso.inject;

import com.pugh.sockso.Console;
import com.pugh.sockso.DBProperties;
import com.pugh.sockso.Manager;
import com.pugh.sockso.Options;
import com.pugh.sockso.Properties;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.auth.Authenticator;
import com.pugh.sockso.auth.DBAuthenticator;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.db.MySQLDatabase;
import com.pugh.sockso.db.HSQLDatabase;
import com.pugh.sockso.db.SQLiteDatabase;
import com.pugh.sockso.gui.AppFrame;
import com.pugh.sockso.music.CollectionManager;
import com.pugh.sockso.music.DBCollectionManager;
import com.pugh.sockso.music.indexing.Indexer;
import com.pugh.sockso.music.indexing.TrackIndexer;
import com.pugh.sockso.resources.FileResources;
import com.pugh.sockso.resources.JarResources;
import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.web.HttpServer;
import com.pugh.sockso.web.HttpsServer;
import com.pugh.sockso.web.Server;

import joptsimple.OptionSet;

import org.apache.log4j.Logger;

import com.google.inject.AbstractModule;

public class SocksoModule extends AbstractModule {
    
    private static final Logger log = Logger.getLogger( SocksoModule.class );
    
    private final OptionSet options;
    
    public SocksoModule( final OptionSet options ) {
        
        this.options = options;
        
    }
    
    @Override
    protected void configure() {
        
        bind( Database.class ).to( getDatabaseClass() );
        bind( Resources.class ).to( getResourcesClass() );
        bind( Properties.class ).to( DBProperties.class );
        bind( Indexer.class ).to( TrackIndexer.class );
        bind( CollectionManager.class ).to( DBCollectionManager.class );
        bind( Authenticator.class ).to( DBAuthenticator.class );
        bind( Server.class ).to( getWebServerClass() );
        bind( Manager.class ).to( getManagerClass() );
        bind( Locale.class ).toProvider( LocaleProvider.class );
        
    }
    
    private Class<? extends Database> getDatabaseClass() {
            
        final String dbtype = options.has( Options.OPT_DBTYPE )
            ? options.valueOf(Options.OPT_DBTYPE).toString()
            : "";

        if ( dbtype.equals("mysql") ) {
            log.info( "Using MySQL Database" );
            return MySQLDatabase.class;
        }
        
        if ( dbtype.equals("sqlite") ) {
            log.info( "Using sqlite Database" );
            return SQLiteDatabase.class;
        }

        else {
            log.info( "Using HSQL Database" );
            return HSQLDatabase.class;
        }
        
    }
    
    private Class<? extends Resources> getResourcesClass() {
        
        final String resourceType = options.has( Options.OPT_RESOURCESTYPE )
            ? options.valueOf(Options.OPT_RESOURCESTYPE).toString() : "";

        log.debug( "Resources type: " +resourceType );
        
        return resourceType.equals( "jar" )
                ? JarResources.class
                : FileResources.class;

    }
    
    private Class<? extends Server> getWebServerClass() {
        
        return options.has( Options.OPT_SSL )
            ? HttpsServer.class
            : HttpServer.class;
        
    }
    
    private Class<? extends Manager> getManagerClass() {
        
        return options.has( Options.OPT_NOGUI )
            ? Console.class
            : AppFrame.class;
            
    }
    
}
