
package com.pugh.sockso.auth;

import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.SocksoTestCase;

public class DBAuthenticatorTest extends SocksoTestCase {

    private Authenticator auth;

    @Override
    public void setUp() throws Exception {
        TestDatabase db = new TestDatabase();
        auth = new DBAuthenticator( db );
        db.fixture( "singleUser" );
    }

    public void testAuthenticatingAValidUser() throws Exception {
        assertTrue( auth.authenticate("foo","q") );
    }

    public void testAuthFailsWhenValidUserAndInvalidPassword() throws Exception {
        assertFalse( auth.authenticate("foo","WRONG") );
    }

    public void testAuthFailsWhenUserDoesntExist() throws Exception {
        assertFalse( auth.authenticate("baz","q") );
    }

}
