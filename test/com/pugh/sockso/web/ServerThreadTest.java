
package com.pugh.sockso.web;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.web.action.BaseAction;

public class ServerThreadTest extends SocksoTestCase {

    private ServerThread st;

    private Properties p;

    class LoginAction extends BaseAction {
        public void handleRequest() {}
    }
    
    class LoginRequiredAction extends LoginAction {
        @Override
        public boolean requiresLogin() { return true; }
    }

    class LoginNotRequiredAction extends LoginAction {
        @Override
        public boolean requiresLogin() { return false; }
    }
    
    @Override
    protected void setUp() {
        p = new StringProperties();
        p.set( Constants.WWW_USERS_REQUIRE_LOGIN, Properties.YES );
        st = new ServerThread( null, null, p, null, null, null );
    }
    
    public void testActionDeniedWhenItRequiresAUserAndSessionDoesntExist() {
        assertTrue( st.loginRequired(null,new LoginRequiredAction()) );
    }

    public void testActionOkWhenItRequiresAUserAndSessionExists() {
        assertFalse( st.loginRequired(new User(1,""), new LoginRequiredAction()) );
    }

    public void testActionOkWhenItDoesntRequireAUserAndASessionDoesntExist() {
        assertFalse( st.loginRequired(null,new LoginNotRequiredAction()) );
    }

}
