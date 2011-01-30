
package com.pugh.sockso;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.web.User;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;

public class CommandExecuterTest extends SocksoTestCase {

    private CommandExecuter cmd;
    
    private Properties p;
    
    private Database db;

    @Override
    public void setUp() {
        p = new StringProperties();
        db = new TestDatabase();
        cmd = new CommandExecuter( db, p, null );
    }

    public void testListingUsersReturnsTheirDetails() throws Exception {
        User user = new User( "foo", "", "foo@bar.com", true );
        user.save( db );
        String result = cmd.execute( "userlist" );
        assertContains( result, String.valueOf(user.getId()) );
        assertContains( result, user.getName() );
        assertContains( result, user.getEmail() );
    }
    
    public void testListingUsersIncludesAdminIfTheyAreAnAdmin() throws Exception {
        User user = new User( "foo", "", "foo@bar.com", true );
        user.save( db );
        assertContains( cmd.execute("userlist"), "ADMIN" );
    }

    public void testListingUsersDoesntIncludeAdminIfTheyAreNotAnAdmin() throws Exception {
        User user = new User( "foo", "", "foo@bar.com", false );
        user.save( db );
        assertNotContains( cmd.execute("userlist"), "ADMIN" );
    }

    public void testAddingAUserCreatedThenInTheDatabase() throws Exception {
        cmd.execute( "useradd name pass email@domain.com 1" );
        assertTableSize( db, "users", 1 );
    }

    public void testAddingAUserInsertsTheirDetailsCorrectly() throws Exception {
        cmd.execute( "useradd name pass email@domain.com 1" );
        User u = User.find( db, 0 );
        assertEquals( 0, u.getId() );
        assertEquals( "name", u.getName() );
        assertEquals( "email@domain.com", u.getEmail() );
    }

    public void testUserIsAddedAsAnAdminWhen1Specified() throws Exception {
        cmd.execute( "useradd name pass email@domain.com 1" );
        assertTrue( User.find( db, 0 ).isAdmin() );
    }

    public void testUserIsNotAddedAsAnAdminWhen0Specified() throws Exception {
        cmd.execute( "useradd name pass email@domain.com 0" );
        assertFalse( User.find( db, 0 ).isAdmin() );
    }

    public void testUserReportedAsHavingBeenAddedWhenTheyAre() {

    }

    public void testUserReportedTextUsesLocale() {
        
    }

}
