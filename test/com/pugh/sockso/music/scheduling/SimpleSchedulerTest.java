
package com.pugh.sockso.music.scheduling;

import com.pugh.sockso.music.scheduling.Scheduler;
import com.pugh.sockso.music.scheduling.SimpleScheduler;
import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.tests.SocksoTestCase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class SimpleSchedulerTest extends SocksoTestCase {

    public void testShouldRunAt() throws Exception {
        final Properties p = new StringProperties();
        final Scheduler s = new SimpleScheduler( null, p );
        final DateFormat df = new SimpleDateFormat( "y-M-d H:m:s" );
        p.set( "scheduler.simple.interval", 10 );
        assertTrue( s.shouldRunAt(df.parse("2009-10-10 11:00:00")) );
        assertTrue( s.shouldRunAt(df.parse("2009-10-10 11:10:00")) );
        assertTrue( s.shouldRunAt(df.parse("2009-10-10 12:00:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-10-10 12:03:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-10-10 09:31:00")) );
    }

}
