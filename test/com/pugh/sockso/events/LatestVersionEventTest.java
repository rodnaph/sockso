
package com.pugh.sockso.events;

import com.pugh.sockso.tests.SocksoTestCase;

public class LatestVersionEventTest extends SocksoTestCase {

    public void testVersionCanBeRetreived() {
        LatestVersionEvent evt = new LatestVersionEvent( "1.2.1" );
        assertEquals( "1.2.1", evt.getVersion() );
    }

}
