
package com.pugh.sockso.commands;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestLocale;
import com.pugh.sockso.web.User;

public class UserActiveTest extends SocksoTestCase {

    private TestLocale locale;

    private Database db;

    private UserActive cmd;

    private User user;

    @Override
    public void setUp() {
        db = new TestDatabase();
        locale = new TestLocale();
        cmd = new UserActive( db, locale );
        user = new User( -1, "name", "pass", "email@domain.com" );
    }

    private String execute( final String line ) throws Exception {
        return cmd.execute(
            line.split( " " )
        );
    }

    public void testUserCanBeSetAsInactive() throws Exception {
        user.save( db );
        execute( "useractive 0 0" );
        assertFalse( User.find(db,0).isActive() );
    }
    
    public void testUserCanBeSetAsActive() throws Exception {
        user.setActive( false );
        user.save( db );
        execute( "useractive 0 1" );
        assertTrue( User.find(db,0).isActive() );
    }

    public void testErrorIsReportedForInvalidUserId() throws Exception {
        locale.setString( "con.err.invalidUserId", "invalid" );
        assertContains( execute("useractive 999 1"), "invalid" );
    }

}
