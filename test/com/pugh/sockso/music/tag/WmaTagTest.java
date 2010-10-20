
package com.pugh.sockso.music.tag;

import com.pugh.sockso.tests.SocksoTestCase;

public class WmaTagTest extends SocksoTestCase {

    public void testNotNull() throws Exception {

        final WmaTag t = new WmaTag();
        
        assertNotNull( t.notnull(null) );
        assertNotNull( t.notnull("") );
        assertNotNull( t.notnull("foo") );

    }

}
