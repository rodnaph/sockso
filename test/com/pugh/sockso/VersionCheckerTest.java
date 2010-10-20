
package com.pugh.sockso;

import com.pugh.sockso.events.LatestVersionEvent;
import com.pugh.sockso.events.LatestVersionListener;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.MyHttpURLConnection;

public class VersionCheckerTest extends SocksoTestCase {

    private Properties p;
    
    public void setUp() {
        p = new StringProperties();
    }

    public void testListenerReceivesEvent() {
        VersionChecker v = new VersionChecker( p );
        VersionCheckerTestListener l = new VersionCheckerTestListener();
        v.addLatestVersionListener( l );
        v.fireLatestVersionEvent( "1.2.1" );
        assertTrue( l.gotEvent );
    }

    public void testVersionFetchedCorrectlyFromUrlConnection() {
        MyHttpURLConnection cnn = new MyHttpURLConnection( "1.2.1" );
        VersionChecker v = new VersionChecker( p );
        VersionCheckerTestListener l = new VersionCheckerTestListener();
        v.addLatestVersionListener( l );
        v.fetchLatestVersionFrom( cnn );
        assertEquals( l.version, "1.2.1" );
    }

    public void testFetchingATwoPartVersionNumber() {
        MyHttpURLConnection cnn = new MyHttpURLConnection( "1.2" );
        VersionChecker v = new VersionChecker( p );
        VersionCheckerTestListener l = new VersionCheckerTestListener();
        v.addLatestVersionListener( l );
        v.fetchLatestVersionFrom( cnn );
        assertEquals( l.version, "1.2" );
    }

    public void testNothingFetchedWhenVersionCheckingDisabled() {
        p.set( Constants.VERSION_CHECK_DISABLED, p.YES );
        MyHttpURLConnection cnn = new MyHttpURLConnection( "1.2" );
        VersionChecker v = new VersionChecker( p );
        VersionCheckerTestListener l = new VersionCheckerTestListener();
        v.addLatestVersionListener( l );
        v.fetchLatestVersionFrom( cnn );
        assertEquals( l.version, "" );
    }

}

class VersionCheckerTestListener implements LatestVersionListener {

    public boolean gotEvent = false;
    public String version = "";

    public void latestVersionReceived( final LatestVersionEvent evt ) {
        gotEvent = true;
        version = evt.getVersion();
    }

}