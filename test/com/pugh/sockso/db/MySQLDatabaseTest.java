
package com.pugh.sockso.db;

import com.pugh.sockso.tests.SocksoTestCase;

public class MySQLDatabaseTest extends SocksoTestCase {

    public void testEscape() {
        
        final MySQLDatabase db = new MySQLDatabase();
        final String str = "it's";
        
        final String expected = "it\\'s";
        final String actual = db.escape( str );
        
        assertEquals( expected, actual );
        
    }
    
}
