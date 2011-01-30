
package com.pugh.sockso.music.scheduling;

import com.pugh.sockso.tests.SocksoTestCase;

import java.util.Date;

public class ManualSchedulerTest extends SocksoTestCase {

    public void testAlwaysReturnsFalse() {
        // not quite testing "always"...
        final Scheduler s = new ManualScheduler();
        assertFalse( s.shouldRunAt(new Date()) );
    }

}
