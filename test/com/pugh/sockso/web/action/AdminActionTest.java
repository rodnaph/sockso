
package com.pugh.sockso.web.action;

import com.pugh.sockso.web.User;

import com.pugh.sockso.tests.TestLocale;

import junit.framework.TestCase;

public class AdminActionTest extends TestCase {

    private MyAdminAction action;
    
    public void setUp() {
        action = new MyAdminAction();
        action.setLocale( new TestLocale() );
    }

    class MyAdminAction extends AdminAction {
        public boolean requestHandled = false;
        public void handleAdminRequest() {
            requestHandled = true;
        }
    }

    public void testAdminActionRequiresLogin() {
        assertTrue( action.requiresLogin() );
    }

    public void testHandleRequestThrowsBadRequestExceptionWhenCurrentUserIsNotAdmin() {
        action.setUser(new User( 1, "foo", "foo@bar.com", false ));
        try {
            action.handleRequest();
            fail( "Expected exception to be thrown when user is not an admin" );
        }
        catch ( final Exception e ) {}
    }

    public void testHandleAdminRequestCalledWhenCurrentUserIsAnAdmin() throws Exception {
        action.setUser(new User( 1, "foo", "foo@bar.com", true ));
        action.handleRequest();
        assertTrue( action.requestHandled );
    }

}
