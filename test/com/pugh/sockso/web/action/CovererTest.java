
package com.pugh.sockso.web.action;

import junit.framework.TestCase;

public class CovererTest extends TestCase {

    private Coverer action;

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

    }

}
