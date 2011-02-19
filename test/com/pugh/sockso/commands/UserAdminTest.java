
package com.pugh.sockso.commands;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestLocale;

public class UserAdminTest extends SocksoTestCase {

    private TestDatabase db;

    private Command cmd;

    private TestLocale locale;

    @Override
    public void setUp() {
        locale = new TestLocale();
        locale.setString( "con.msg.userUpdated", "user updated" );
        locale.setString( "con.err.errorUpdatingUser", "error updating user" );
        db = new TestDatabase();
        cmd = new UserAdmin( db, locale );
    }

    private String execute( String command ) throws Exception {
        return cmd.execute( command.split(" ") );
    }

    private void useradd( String command ) throws Exception {
        new UserAdd( db, locale ).execute(
            ("useradd " + command).split(" ")
        );
    }

    public void testMakingAUserAdminDoesSo() throws Exception {
        useradd( "foo bar foo@bar.com 0" );
        execute( "useradmin 0 1" );
        assertRowExists( db, "users", "is_admin", "1" );
    }

    public void testRevokingAUsersAdminDoesSo() throws Exception {
        useradd( "foo bar foo@bar.com 1" );
        execute( "useradmin 0 0" );
        assertRowExists( db, "users", "is_admin", "0" );
    }

    public void testMessageReturnedWhenUserIsGivenAdmin() throws Exception {
        useradd( "foo bar foo@bar.com 0" );
        assertEquals( execute("useradmin 0 1"), "user updated" );
    }

    public void testMessageReturnedWhenUserIsRevokedFromAdmin() throws Exception {
        useradd( "foo bar foo@bar.com 1" );
        assertEquals( execute("useradmin 0 0"), "user updated" );
    }

    public void testErrorMessageReturnedWhenUseradminUsedWithInvalidUserId() throws Exception {
        useradd( "foo bar foo@bar.com 0" );
        assertEquals( execute("useradmin 1 1"), "error updating user" );
    }

    public void testErrorMessageReturnedWhenIsadminValueIsNot1Or0() throws Exception {
        useradd( "foo bar foo@bar.com 1" );
        assertEquals( execute("useradmin 0 2"), "error updating user" );
    }
    
}
