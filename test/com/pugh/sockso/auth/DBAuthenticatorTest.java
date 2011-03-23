
package com.pugh.sockso.auth;

import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.web.User;

public class DBAuthenticatorTest extends SocksoTestCase {

    private Authenticator auth;

    private TestDatabase db;

    @Override
    public void setUp() throws Exception {
        db = new TestDatabase();
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

    public void testAuthFailsWhenTheUserIsNotActive() throws Exception {
        User user = new User( -1, "fooaaaaaa", "bar", "foosss@bar.com" );
        user.setActive( false );
        user.save( db );
        assertFalse( auth.authenticate(user.getName(),"bar") );
    }

}
