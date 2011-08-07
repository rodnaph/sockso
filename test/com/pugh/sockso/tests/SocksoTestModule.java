
package com.pugh.sockso.tests;

import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.resources.FileResources;
import com.pugh.sockso.resources.Resources;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.pugh.sockso.inject.LocaleProvider;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.web.HttpServer;
import com.pugh.sockso.web.Server;

public class SocksoTestModule extends AbstractModule {
    
    public void configure() {
        
        bind( Database.class ).to( TestDatabase.class ).in( Singleton.class );
        bind( Properties.class ).to( StringProperties.class );
        bind( Resources.class ).to( FileResources.class );
        bind( Locale.class ).toProvider( LocaleProvider.class );
        bind( Server.class ).to( HttpServer.class );
        
    }
    
}
