
package com.pugh.sockso;

import com.pugh.sockso.tests.SocksoTestCase;

public class JsonUtilsTest extends SocksoTestCase {
    
    public void testStringEscapesDoubleQuotes() {
        assertEquals( "\"a\\\"b\"", JsonUtils.string("a\"b") );
    }
    
}
