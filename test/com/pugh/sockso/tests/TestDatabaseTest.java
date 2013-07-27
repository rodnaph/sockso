
package com.pugh.sockso.tests;

import com.pugh.sockso.Utils;

import java.sql.ResultSet;
import java.sql.Statement;

public class TestDatabaseTest extends SocksoTestCase {

    public void testConstructor() {
        
        final TestDatabase db = new TestDatabase();

        assertNotNull( db );
        
    }

    public void testApplyFixture() throws Exception {
        
        final TestDatabase db = new TestDatabase();
        
        db.fixture( "test" );

        Statement st = null;
        ResultSet rs = null;

        try {
            st = db.getConnection().createStatement();
            rs = st.executeQuery(" select id, path from collection ");

            assertTrue(rs.next());
            assertEquals(rs.getInt("id"), 99);
            assertTrue(rs.getString("path").equals("/some/path"));
        }
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
    }

}
