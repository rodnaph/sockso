
package com.pugh.sockso;

import com.pugh.sockso.db.HSQLDatabase;
import com.pugh.sockso.db.MySQLDatabase;
import com.pugh.sockso.db.SQLiteDatabase;
import com.pugh.sockso.gui.AppFrame;
import com.pugh.sockso.music.indexing.TrackIndexer;
import com.pugh.sockso.music.scheduling.CronScheduler;
import com.pugh.sockso.music.scheduling.ManualScheduler;
import com.pugh.sockso.music.scheduling.SimpleScheduler;
import com.pugh.sockso.resources.JarResources;
import com.pugh.sockso.resources.FileResources;
import com.pugh.sockso.web.HttpServer;
import com.pugh.sockso.web.HttpsServer;
import com.pugh.sockso.tests.SocksoTestCase;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class MainTest extends SocksoTestCase {

    @Override
    public void setUp() {
        
    }

    public void testGetServer() throws Exception {
        
        final OptionParser parser = Options.getParser();
        final OptionSet sslOptions = parser.parse( new String[] { "--ssl" } );
        final OptionSet stdOptions = parser.parse( new String[] {} );
        
        assertEquals( HttpsServer.class, Main.getServer( 4444, sslOptions ).getClass() );
        assertEquals( HttpServer.class, Main.getServer( 4444, stdOptions ).getClass() );
        
    }

    public void testGetResources() throws Exception {
        
        final OptionParser parser = Options.getParser();
        final OptionSet jarOptions = parser.parse( new String[] { "--resourcestype=jar" } );
        final OptionSet fileOptions = parser.parse( new String[] { "--resourcestype=file" } );
        final OptionSet noOptions = parser.parse( new String[] {} );
        
        assertEquals( JarResources.class, Main.getResources(jarOptions).getClass() );
        assertEquals( FileResources.class, Main.getResources(fileOptions).getClass() );
        assertEquals( FileResources.class, Main.getResources(noOptions).getClass() ); // default
        
    }

    public void testGetLocale() throws Exception {
        
        final OptionParser parser = Options.getParser();
        final OptionSet stdOptions = parser.parse( new String[] {} );
        final OptionSet nbOptions = parser.parse( new String[] { "--locale=nb" } );
        
        assertEquals( "en", Main.getLocale(stdOptions) ); // default
        assertEquals( "nb", Main.getLocale(nbOptions) );
        
    }

    public void testGetUseGui() throws Exception {
        
        final OptionParser parser = Options.getParser();
        final OptionSet guiOptions = parser.parse( new String[] {} );
        final OptionSet noGuiOptions = parser.parse( new String[] { "--nogui" } );

        assertTrue( Main.getUseGui(guiOptions) );
        assertFalse( Main.getUseGui(noGuiOptions) );
        
    }

    public void testGetDatabase() throws Exception {
        
        final OptionParser parser = Options.getParser();
        final OptionSet defaultOptions = parser.parse( new String[] {} );
        final OptionSet hsqlOptions = parser.parse( new String[] { "--dbtype=hsql" } );
        final OptionSet mysqlOptions = parser.parse( new String[] { "--dbtype=mysql" } );
        final OptionSet sqliteOptions = parser.parse( new String[] { "--dbtype=sqlite" } );
        
        assertEquals( HSQLDatabase.class, Main.getDatabase(defaultOptions).getClass() );
        assertEquals( HSQLDatabase.class, Main.getDatabase(hsqlOptions).getClass() );
        assertEquals( MySQLDatabase.class, Main.getDatabase(mysqlOptions).getClass() );
        assertEquals( SQLiteDatabase.class, Main.getDatabase(sqliteOptions).getClass() );
        
    }
    public void testGettingSavedPort() {
        final Properties p = new StringProperties();
        p.set( Constants.SERVER_PORT, 1234 );
        assertEquals( 1234, Main.getSavedPort(p) );
    }

    public void testDefaultPortReturnedWhenPortIsInvalid() {
        final Properties p = new StringProperties();
        p.set( Constants.SERVER_PORT, "INVALID PORT" );
        assertEquals( HttpServer.DEFAULT_PORT, Main.getSavedPort(p) );
    }

    public void testGettingProtocolWhenSslSpecified() {
        final OptionParser parser = Options.getParser();
        final OptionSet sslOptions = parser.parse( new String[] { "--ssl" } );
        assertEquals( Main.getProtocol(sslOptions), "https" );
    }

    public void testGettingProtocolDefaultsToHttp() {
        final OptionParser parser = Options.getParser();
        final OptionSet stdOptions = parser.parse( new String[] {} );
        assertEquals( Main.getProtocol(stdOptions), "http" );
    }

    public void testGettingTheGuiAsManager() {
        assertEquals( AppFrame.class, Main.getManager(true,null).getClass() );
    }

    public void testGettingConsoleAsManager() {
        assertEquals( Console.class, Main.getManager(false,null).getClass() );
    }

    public void testGetIndexer() {
        assertEquals( TrackIndexer.class, Main.getIndexer().getClass() );
    }

}
