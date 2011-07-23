
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestResponse;
import com.pugh.sockso.web.User;

public class SessionActionTest extends SocksoTestCase {

    private SessionAction action;
    
    private TestResponse res;
    
    @Override
    protected void setUp() {
        res = new TestResponse();
        action = new SessionAction();
        action.setResponse( res );
    }
    
    public void testActionDoesNotRequireLogin() {
        assertFalse( action.requiresLogin() );
    }
    
    public void testCanhandleReturnsTrueForValidUrls() {
        assertTrue( action.canHandle(getRequest("/api/session")) );
        assertTrue( action.canHandle(getRequest("/api/session?foo=bar")) );
    }
    
    public void testCanhandleReturnsFalseForInvalidUrls() {
        assertFalse( action.canHandle(getRequest("/api/sessions")) );
        assertFalse( action.canHandle(getRequest("/api")) );
        assertFalse( action.canHandle(getRequest("/api/session/create")) );
    }
    
    public void test1ReturnedWhenUserHasSession() throws Exception {
        action.setUser( new User(1,"me") );
        action.handleRequest();
        assertEndsWith( res.getOutput(), "1" );
    }
    
    public void test0ReturnedWhenUserDoesNotHaveSession() throws Exception {
        action.setUser( null );
        action.handleRequest();
        assertEndsWith( res.getOutput(), "0" );
    }
    
}
