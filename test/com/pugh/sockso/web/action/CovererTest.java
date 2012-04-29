
package com.pugh.sockso.web.action;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestRequest;
import com.pugh.sockso.web.action.covers.BaseCoverer;

public class CovererTest extends SocksoTestCase {

    private Coverer action;

    public static int runState = 0;

    @Override
    protected void setUp() {
        action = new Coverer();
        action.setRequest( new TestRequest("GET /foo/bar HTTP/1.0") );
    }

    public void testNoLoginRequired() {
        assertFalse( action.requiresLogin() );
    }

    public void testNoSessionRequired() {
        assertFalse( action.requiresSession() );
    }
    
    public void testCoverPluginsAreCheckedForCoverMatches() throws Exception {
        runState = 0;
        action.addPlugin(new CovererPluginAdapter() {
            public boolean serveCover( String name ) {
                CovererTest.runState = 1;
                return true;
            }
        });
        action.handleRequest();
        assertEquals( 1, runState );
    }

    public void testPluginProcessingStopsWhenFirstPluginReturnsTrue() throws Exception {
        runState = 0;
        action.addPlugin(new CovererPluginAdapter() {
            public boolean serveCover( String name ) {
                CovererTest.runState = 1;
                return true;
            }
        });
        action.addPlugin(new CovererPluginAdapter() {
            public boolean serveCover( String name ) {
                CovererTest.runState = 2;
                return true;
            }
        });
        action.handleRequest();
        assertEquals( 1, runState );
    }

}

class CovererPluginAdapter extends BaseCoverer {
    public boolean serveCover( String name ) {
        return false;
    }
}
