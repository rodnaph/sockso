
package com.pugh.sockso.web.action;

import com.pugh.sockso.tests.SocksoTestCase;

public class BaseActionTest extends SocksoTestCase {

    private BaseAction action;

    class MyBaseAction extends BaseAction {
        public void handleRequest() {}
    }

    @Override
    protected void setUp() {
        action = new MyBaseAction();
    }

    public void testUserIsRequiredByDefault() {
        assertTrue( action.requiresLogin() );
    }
    
}
