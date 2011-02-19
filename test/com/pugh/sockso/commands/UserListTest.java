
package com.pugh.sockso.commands;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.web.User;

public class UserListTest extends SocksoTestCase {
    
    private TestDatabase db;
    
    private Command cmd;

    @Override
    public void setUp() {
        db = new TestDatabase();
        cmd = new UserList( db );
    }

    public void testListingUsersReturnsTheirDetails() throws Exception {
        User user = new User( "foo", "", "foo@bar.com", true );
        user.save( db );
        String result = cmd.execute( new String[] { "userlist" } );
        assertContains( result, String.valueOf(user.getId()) );
        assertContains( result, user.getName() );
        assertContains( result, user.getEmail() );
    }

    public void testListingUsersIncludesAdminIfTheyAreAnAdmin() throws Exception {
        User user = new User( "foo", "", "foo@bar.com", true );
        user.save( db );
        assertContains( cmd.execute( new String[] { "userlist" } ), "ADMIN" );
    }

    public void testListingUsersDoesntIncludeAdminIfTheyAreNotAnAdmin() throws Exception {
        User user = new User( "foo", "", "foo@bar.com", false );
        user.save( db );
        assertNotContains( cmd.execute( new String[] { "userlist" } ), "ADMIN" );
    }

}
