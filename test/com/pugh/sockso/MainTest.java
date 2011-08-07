
package com.pugh.sockso;

import com.pugh.sockso.gui.AppFrame;
import com.pugh.sockso.web.HttpServer;
import com.pugh.sockso.tests.SocksoTestCase;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class MainTest extends SocksoTestCase {

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

}
