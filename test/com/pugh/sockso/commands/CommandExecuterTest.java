
package com.pugh.sockso.commands;

import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.db.Database;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestLocale;

public class CommandExecuterTest extends SocksoTestCase {

    private CommandExecuter cmd;
    
    private Properties p;
    
    private Database db;

    private TestLocale locale;

    @Override
    public void setUp() {
        p = new StringProperties();
        db = new TestDatabase();
        locale = new TestLocale();
        locale.setString( "con.desc.commands", "Usage:" );
        locale.setString( "con.msg.propertySaved", "property saved" );
        cmd = new CommandExecuter( db, p, null, locale );
    }


    public void testCommandsCanBeExecutedByName() throws Exception {
        cmd.execute( "propset foo bar" );
        assertEquals( "bar", p.get("foo") );
    }

    public void testCommandsCanBePassedParameters() throws Exception {
        cmd.execute( "propset foo bar" );
        assertEquals( "bar", p.get("foo") );
    }

    public void testErrorReportedWhenExtactNumberOfRequiredArgumentsIsNotSpecified() throws Exception {
        assertContains( cmd.execute("propset foo"), "requires" );           // too few
        assertContains( cmd.execute("propset foo bar baz"), "requires" );   // too many

    }

    public void testCommandResultIsReturnedWhenACommandIsRun() throws Exception {
        assertEquals( "property saved", cmd.execute("propset foo bar") );
    }

    public void testUsageIsReturnedWhenAnUnknownCommandIsSpecified() throws Exception {
        assertContains( cmd.execute("bloopper"), "Usage:" );
    }

    public void testCommandNamesListedInUsageReturned() throws Exception {
        assertContains( cmd.execute("help"), "propset" );
    }

    public void testCommandArgumentsListedInUsageReturned() throws Exception {
        assertContains( cmd.execute("help"), "NAME VALUE" );
    }

    public void testCommandDescriptionListedInUsageReturned() throws Exception {
        assertContains( cmd.execute("help"), "Sets a property" );
    }

    // tests to move to command classes


//    public void testListingUsersReturnsTheirDetails() throws Exception {
//        User user = new User( "foo", "", "foo@bar.com", true );
//        user.save( db );
//        String result = cmd.execute( "userlist" );
//        assertContains( result, String.valueOf(user.getId()) );
//        assertContains( result, user.getName() );
//        assertContains( result, user.getEmail() );
//    }
//
//    public void testListingUsersIncludesAdminIfTheyAreAnAdmin() throws Exception {
//        User user = new User( "foo", "", "foo@bar.com", true );
//        user.save( db );
//        assertContains( cmd.execute("userlist"), "ADMIN" );
//    }
//
//    public void testListingUsersDoesntIncludeAdminIfTheyAreNotAnAdmin() throws Exception {
//        User user = new User( "foo", "", "foo@bar.com", false );
//        user.save( db );
//        assertNotContains( cmd.execute("userlist"), "ADMIN" );
//    }
//
//    public void testAddingAUserCreatedThenInTheDatabase() throws Exception {
//        cmd.execute( "useradd name pass email@domain.com 1" );
//        assertTableSize( db, "users", 1 );
//    }
//
//    public void testAddingAUserInsertsTheirDetailsCorrectly() throws Exception {
//        cmd.execute( "useradd name pass email@domain.com 1" );
//        User u = User.find( db, 0 );
//        assertEquals( 0, u.getId() );
//        assertEquals( "name", u.getName() );
//        assertEquals( "email@domain.com", u.getEmail() );
//    }
//
//    public void testUserIsAddedAsAnAdminWhen1Specified() throws Exception {
//        cmd.execute( "useradd name pass email@domain.com 1" );
//        assertTrue( User.find( db, 0 ).isAdmin() );
//    }
//
//    public void testUserIsNotAddedAsAnAdminWhen0Specified() throws Exception {
//        cmd.execute( "useradd name pass email@domain.com 0" );
//        assertFalse( User.find( db, 0 ).isAdmin() );
//    }
//
//    public void testUserReportedAsHavingBeenAddedWhenTheyAre() throws Exception {
//        String message = cmd.execute( "useradd name pass email@domain.com 0" );
//        assertContains( message, "user added" );
//    }
//
//    public void testUserReportedTextUsesLocale() throws Exception {
//        String message = cmd.execute( "useradd name pass email@domain.com 0" );
//        assertEquals( message, "user added" );
//    }
//
//    public void testUsageReturnedWhenTooManyArgumentsPassedToUseradd() throws Exception {
//        assertContains( cmd.execute("useradd foo bar email@domain.com 0 asd qwe"), "Usage:" );
//    }
//
//    public void testUsageReturnedWhenNotEnoughArgumentsPassedToUseradd() throws Exception {
//        assertContains( cmd.execute("useradd foo bar email@domain.com"), "Usage:" );
//    }
//
//    public void testAddingAUserWithDuplicateUsernameDoesNotCreateThem() throws Exception {
//        cmd.execute( "useradd foo bar email@domain.com 0" );
//        cmd.execute( "useradd foo bar email@domain.com 0" );
//        assertTableSize( db, "users", 1 );
//    }
//
//    public void testAddingAUserWithDuplicateUsernameReturnsTheErrorMessage() throws Exception {
//        cmd.execute( "useradd foo bar email@domain.com 0" );
//        String message = cmd.execute( "useradd foo bar email@domain.com 0" );
//        assertEquals( message, "username exists" );
//    }
//
//    public void testDeletingAUserByIdRemovesThem() throws Exception {
//        cmd.execute( "useradd foo bar email@domain.com 0" );
//        cmd.execute( "userdel 0" );
//        assertTableSize( db, "users", 0 );
//    }
//
//    public void testUserIsReportedAsHavingBeenDeleted() throws Exception {
//        cmd.execute( "useradd foo bar email@domain.com 0" );
//        assertEquals( cmd.execute("userdel 0"), "user deleted" );
//    }
//
//    public void testErrorStringReturnedWhenInvalidUserIdToDeleteSpecified() throws Exception {
//        assertEquals( cmd.execute("userdel 1"), "invalid user id" );
//    }
//
//    public void testUsageReturnedWhenTooManyArgsToUserdel() throws Exception {
//        assertContains( cmd.execute("userdel 1 2"), "Usage:" );
//    }
//
//    public void testUsageReturnedWhenNotEnoughArgsToUserdel() throws Exception {
//        assertContains( cmd.execute("userdel"), "Usage:" );
//    }
//
//    public void testMakingAUserAdminDoesSo() throws Exception {
//        cmd.execute( "useradd foo bar foo@bar.com 0" );
//        cmd.execute( "useradmin 0 1" );
//        assertRowExists( db, "users", "is_admin", "1" );
//    }
//
//    public void testRevokingAUsersAdminDoesSo() throws Exception {
//        cmd.execute( "useradd foo bar foo@bar.com 1" );
//        cmd.execute( "useradmin 0 0" );
//        assertRowExists( db, "users", "is_admin", "0" );
//    }
//
//    public void testUsageReturnedWhenTooManyArgsToUseradmin() throws Exception {
//        assertContains( cmd.execute("useradmin 0 1 2"), "Usage:" );
//    }
//
//    public void testUsageReturnedWhenNotEnoughArgsToUseradmin() throws Exception {
//        assertContains( cmd.execute("useradmin 0"), "Usage:" );
//    }
//
//    public void testMessageReturnedWhenUserIsGivenAdmin() throws Exception {
//        cmd.execute( "useradd foo bar foo@bar.com 0" );
//        assertEquals( cmd.execute("useradmin 0 1"), "user updated" );
//    }
//
//    public void testMessageReturnedWhenUserIsRevokedFromAdmin() throws Exception {
//        cmd.execute( "useradd foo bar foo@bar.com 1" );
//        assertEquals( cmd.execute("useradmin 0 0"), "user updated" );
//    }
//
//    public void testErrorMessageReturnedWhenUseradminUsedWithInvalidUserId() throws Exception {
//        cmd.execute( "useradd foo bar foo@bar.com 0" );
//        assertEquals( cmd.execute("useradmin 1 1"), "invalid user id" );
//    }
//
//    public void testErrorMessageReturnedWhenIsadminValueIsNot1Or0() throws Exception {
//        cmd.execute( "useradd foo bar foo@bar.com 1" );
//        assertEquals( cmd.execute("useradmin 0 2"), "error updating user" );
//    }

}
