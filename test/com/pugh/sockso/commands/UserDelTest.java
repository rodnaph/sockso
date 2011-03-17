
package com.pugh.sockso.commands;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestLocale;

public class UserDelTest extends SocksoTestCase {

    private TestDatabase db;
    
    private TestLocale locale;
    
    private Command cmd;
    
    @Override
    public void setUp() {
        locale = new TestLocale();
        locale.setString( "con.msg.userDeleted", "user deleted" );
        locale.setString( "con.err.errorDeletingUser", "invalid user id" );
        db = new TestDatabase();
        cmd = new UserDel( db, locale );
    }
    
    private String execute( String command ) throws Exception {
        return cmd.execute( command.split(" ") );
    }

    private void addUser() throws Exception {
        UserAdd add = new UserAdd( db, locale );
        add.execute( new String("useradd foo bar email@domain.com 0").split(" ") );
    }

    public void testDeletingAUserByIdRemovesThem() throws Exception {
        addUser();
        execute( "userdel 0" );
        assertTableSize( db, "users", 0 );
    }

    public void testUserIsReportedAsHavingBeenDeleted() throws Exception {
        addUser();
        assertEquals( execute("userdel 0"), "user deleted" );
    }

    public void testErrorStringReturnedWhenInvalidUserIdToDeleteSpecified() throws Exception {
        assertEquals( execute("userdel 1"), "invalid user id" );
    }
    
}
