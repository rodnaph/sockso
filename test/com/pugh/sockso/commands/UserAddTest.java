
package com.pugh.sockso.commands;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestLocale;
import com.pugh.sockso.web.User;

public class UserAddTest extends SocksoTestCase {

    private TestLocale locale;
    
    private TestDatabase db;
    
    private Command cmd;
    
    @Override
    public void setUp() {
        locale = new TestLocale();
        locale.setString( "con.msg.userCreated", "user added" );
        locale.setString( "con.err.usernameExists", "username exists" );
        db = new TestDatabase();
        cmd = new UserAdd( db, locale );
    }
    
    private String execute( String command ) throws Exception {
        return cmd.execute( command.split(" ") );
    }

    public void testAddingAUserCreatedThenInTheDatabase() throws Exception {
        execute( "useradd name pass email@domain.com 1" );
        assertTableSize( db, "users", 1 );
    }

    public void testAddingAUserInsertsTheirDetailsCorrectly() throws Exception {
        execute( "useradd name pass email@domain.com 1" );
        User u = User.find( db, 0 );
        assertEquals( 0, u.getId() );
        assertEquals( "name", u.getName() );
        assertEquals( "email@domain.com", u.getEmail() );
    }

    public void testUserIsAddedAsAnAdminWhen1Specified() throws Exception {
        execute( "useradd name pass email@domain.com 1" );
        assertTrue( User.find( db, 0 ).isAdmin() );
    }

    public void testUserIsNotAddedAsAnAdminWhen0Specified() throws Exception {
        execute( "useradd name pass email@domain.com 0" );
        assertFalse( User.find( db, 0 ).isAdmin() );
    }

    public void testUserReportedAsHavingBeenAddedWhenTheyAre() throws Exception {
        String message = execute( "useradd name pass email@domain.com 0" );
        assertContains( message, "user added" );
    }

    public void testUserReportedTextUsesLocale() throws Exception {
        String message = execute( "useradd name pass email@domain.com 0" );
        assertEquals( message, "user added" );
    }

    public void testAddingAUserWithDuplicateUsernameDoesNotCreateThem() throws Exception {
        execute( "useradd foo bar email@domain.com 0" );
        execute( "useradd foo bar email@domain.com 0" );
        assertTableSize( db, "users", 1 );
    }

    public void testAddingAUserWithDuplicateUsernameReturnsTheErrorMessage() throws Exception {
        execute( "useradd foo bar email@domain.com 0" );
        String message = execute( "useradd foo bar email@domain.com 0" );
        assertEquals( message, "username exists" );
    }

    public void testNewUsersAreAddedAsBeingActive() throws Exception {
        execute( "useradd foo bar email@domain.com 0" );
        assertTrue( User.find(db,0).isActive() );
    }

}
