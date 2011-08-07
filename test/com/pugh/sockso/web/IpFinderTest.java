
package com.pugh.sockso.web;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Options;
import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.tests.MyHttpURLConnection;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestOptionSet;

import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.Date;

public class IpFinderTest extends SocksoTestCase {

    public void testConstructor() {
        assertNotNull( new IpFinder(null) );
        assertNotNull( new IpFinder(null,null) );
    }

    public void testIpDefaultsToLoopback() {
        final IpFinder f = new IpFinder( null );
        assertEquals( IpFinder.LOOPBACK, f.getIp() );
    }

    public void testGetIpFromProperties() {
        
        final Properties p = new StringProperties();
        final IpFinder f = new IpFinder( p, null );
        final String propertiesIp = "123.456.789.101";

        p.set( Constants.SERVER_HOST_LAST_UPDATED, Long.toString(new Date().getTime()) );
        p.set( Constants.SERVER_HOST, propertiesIp );
        
        assertEquals( propertiesIp, f.getIpFromProperties() );

    }

    public void testStaleIpInProperties() {

        final Properties p = new StringProperties();
        final IpFinder f = new IpFinder( p, null );
        final String propertiesIp = "123.456.789.101";
        final String staleDate = Long.toString(new Date().getTime() - (Constants.SERVER_IP_TIMEOUT * 2));

        p.set( Constants.SERVER_HOST_LAST_UPDATED, staleDate );
        p.set( Constants.SERVER_HOST, propertiesIp );

        assertNull( f.getIpFromProperties() );

    }

    public void testGetIpFromOptions() {

        final TestOptionSet options = new TestOptionSet();
        final StringProperties p = new StringProperties();
        final String ip = "123.123.123.123";
        final IpFinder f = new IpFinder( p, options );

        options.addHas( Options.OPT_IP, ip );

        assertEquals( ip, f.getIpFromOptions() );
        
    }

    public void testGetIpFromOptionsNothingSpecified() {

        final TestOptionSet options = new TestOptionSet();
        final StringProperties p = new StringProperties();
        final String ip = "123.123.123.123";
        final IpFinder f = new IpFinder( p, options );

        assertNull( f.getIpFromOptions() );

    }

    public void testGetIpFromIntranet() {
        // @TODO
    }
    
    public void testGetIpFromInternet() {
        // @TODO
    }

    public void testGetIpFromUrl() {

        final IpFinder f = new IpFinder( null );

        // first try a failed connection

        boolean gotException = false;

        try {
            final HttpURLConnection cnn = new MyHttpURLConnection( null );
            f.getIpFromUrl( cnn );
        }
        catch ( final IOException e ) {
            gotException = true;
        }

        assertTrue( gotException );

        // now try a good connection

        final String ip = "192.168.1.2";

        gotException = false;

        try {

            final HttpURLConnection cnn = new MyHttpURLConnection( ip );
            final String gotIp = f.getIpFromUrl( cnn );

            assertEquals( ip, gotIp );

        }

        catch ( final IOException e ) {
            gotException = true;
        }

        assertFalse( gotException );

    }

    public void testIsValidIpFormat() {
        
        final IpFinder f = new IpFinder( null );

        assertTrue( f.isValidIpFormat("123.456.789.123") );
        assertTrue( f.isValidIpFormat("13.45.789.3") );
        assertFalse( f.isValidIpFormat("www.google.com") );

    }

    public void testSaveIpToProperties() {

        final StringProperties p = new StringProperties();
        final IpFinder f = new IpFinder( p );
        
        assertEquals( f.LOOPBACK, f.getIp() );
        assertEquals( "", p.get(Constants.SERVER_HOST) );
        assertEquals( "", p.get(Constants.SERVER_HOST_LAST_UPDATED) );
        
        f.save();

        assertEquals( f.LOOPBACK, p.get(Constants.SERVER_HOST) );
        assertFalse( p.get(Constants.SERVER_HOST_LAST_UPDATED).equals("") );

    }

    public void testUpdateForcesRegettingIpFromProperties() {
        
        final StringProperties p = new StringProperties();
        final IpFinder f = new IpFinder( p );
        final String firstIp = "123.123.123.123";
        final String secondIp = "456.456.456.456";

        p.set( Constants.SERVER_HOST_LAST_UPDATED, new Date().getTime() );
        p.set( Constants.SERVER_HOST, firstIp );

        f.update();
        assertEquals( firstIp, f.getIp() );

        p.set( Constants.SERVER_HOST, secondIp );

        f.update();
        assertEquals( secondIp, f.getIp() );

    }

    public void testRefreshClearsCacheFirst() {

        final StringProperties p = new StringProperties();
        final IpFinder f = new IpFinder( p );
        final String startTime = Long.toString( new Date().getTime() );

        p.set( Constants.SERVER_HOST_LAST_UPDATED, startTime );
        
        f.refresh();

        assertFalse( p.get(Constants.SERVER_HOST_LAST_UPDATED).equals(startTime) );

    }

}
