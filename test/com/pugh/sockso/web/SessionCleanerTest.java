
package com.pugh.sockso.web;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;

public class SessionCleanerTest extends SocksoTestCase {

    public void testConstructor() {
        assertNotNull( new SessionCleaner(null) );
    }

    public void testCleanSessionsTable() throws Exception {

        final TestDatabase db = new TestDatabase();
        final SessionCleaner c = new SessionCleaner( db );
        final String code = "abcdefghij";
        
        db.update(
            " insert into sessions ( id, code, user_id, date_created ) " +
            " values ( 1, '" +code+ "', 1, 0 ) "
        );
        assertRowExists( db, "sessions", "code", code );
        
        c.cleanSessionsTable();
        
        assertRowDoesntExist( db, "sessions", "code", code );

    }

    public void testCurrentSessionNotDeletedOnClean() throws Exception {

        final TestDatabase db = new TestDatabase();
        final SessionCleaner c = new SessionCleaner( db );
        final String code = "abcdefghij";

        db.update(
            " insert into sessions ( id, code, user_id, date_created ) " +
            " values ( 1, '" +code+ "', 1, current_timestamp ) "
        );
        assertRowExists( db, "sessions", "code", code );

        c.cleanSessionsTable();

        assertRowExists( db, "sessions", "code", code );

    }

}
