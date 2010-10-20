
package com.pugh.sockso.tests;

import java.sql.ResultSet;

public class TestDatabaseTest extends SocksoTestCase {

    public void testConstructor() {
        
        final TestDatabase db = new TestDatabase();

        assertNotNull( db );
        
    }

    public void testApplyFixture() throws Exception {
        
        final TestDatabase db = new TestDatabase();
        
        db.fixture( "test" );

        final ResultSet rs = db.query( " select id, path from collection " );

        assertTrue( rs.next() );
        assertEquals( rs.getInt("id"), 99 );
        assertTrue( rs.getString("path").equals("/some/path") );

    }

}
