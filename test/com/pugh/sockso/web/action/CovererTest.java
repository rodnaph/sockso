
package com.pugh.sockso.web.action;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.web.action.covers.CovererPlugin;

public class CovererTest extends SocksoTestCase {

    private Coverer action;

    public static int runState = 0;

    @Override
    protected void setUp() {
        action = new Coverer();
    }

    public void testNoLoginRequired() {
        assertFalse( action.requiresLogin() );
    }

    public void testNoSessionRequired() {
        assertFalse( action.requiresSession() );
    }
    
    public void testCoverPluginsAreCheckedForCoverMatches() {
        runState = 0;
        action.addPlugin(new CovererPlugin() {
            public boolean serveCover( String name ) {
                CovererTest.runState = 1;
                return true;
            }
        });
        action.handleRequest();
        assertEquals( 1, runState );
    }

    public void testPluginProcessingStopsWhenFirstPluginReturnsTrue() {
        runState = 0;
        action.addPlugin(new CovererPlugin() {
            public boolean serveCover( String name ) {
                CovererTest.runState = 1;
                return true;
            }
        });
        action.addPlugin(new CovererPlugin() {
            public boolean serveCover( String name ) {
                CovererTest.runState = 2;
                return true;
            }
        });
        action.handleRequest();
        assertEquals( 1, runState );
    }

    // TODO
    public void testReturnsRemoteCover() {
        // mock remote cover method and make sure cache/local empty
    }

    // TODO
    public void testReturnsDefaultCover() {
        //  make sure cache/local empty and remote returns nothing
    }

    public void testLocalCoversAreServedWhenTheyExist() {
    }

    public void testRemoteCoversAreNotFetchedWhenFeatureIsDisabled() {
    }

}
