
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.tests.SocksoTestCase;

public class RootActionTest extends SocksoTestCase {

    private BaseApiAction action;
    
    @Override
    protected void setUp() {
        action = new RootAction();
    }
    
    public void testActionRespondsToRootUrl() {
        assertTrue( action.canHandle(getRequest( "/api" )) );
        assertTrue( action.canHandle(getRequest( "/api/" )) );
    }
    
    public void testActionDoesNotRespondToNonRootUrl() {
        assertFalse( action.canHandle(getRequest( "/api/tracks" )) );
    }
    
}
