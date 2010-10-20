
package com.pugh.sockso.db;

import com.pugh.sockso.tests.SocksoTestCase;

public class SQLiteDatabaseTest extends SocksoTestCase {

    public void testEscape() {
        
        final SQLiteDatabase db = new SQLiteDatabase();
        final String str = "it's";
        
        final String expected = "it''s";
        final String actual = db.escape( str );
        
        assertEquals( expected, actual );
        
    }
    
}
