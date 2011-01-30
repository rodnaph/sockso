
package com.pugh.sockso.music.scheduling;

import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.tests.SocksoTestCase;

public class SchedulerRunnerTest extends SocksoTestCase {

    private Properties p;

    @Override
    public void setUp() {
        p = new StringProperties();
    }

    private SchedulerRunner getInstance( final String scheduler ) {
        p.set( "scheduler", scheduler );
        return new SchedulerRunner( null, p );
    }

    public void testGetSchedulerReturnsSimpleSchedulerWhenNothingSpecified() {
        assertEquals( SimpleScheduler.class, getInstance("").getScheduler().getClass() );
    }

    public void testGetSchedulerReturnsCronSchedulerWhenSpecified() {
        assertEquals( CronScheduler.class, getInstance("cron").getScheduler().getClass() );
    }

    public void testGetSchedulerReturnsManualSchedulerWhenSpecified() {
        assertEquals( ManualScheduler.class, getInstance("manual").getScheduler().getClass() );
    }

    public void testGetSchedulerReturnsSimpleSchedulerWhenInvalidOneSpecified() {
        assertEquals( SimpleScheduler.class, getInstance("doesnotexist").getScheduler().getClass() );
    }

    public void testGetSchedulerReturnsNewValueWhenSchedulerIsChanged() {
        SchedulerRunner runner = getInstance( "" );
        assertEquals( SimpleScheduler.class, runner.getScheduler().getClass() );
        p.set( "scheduler", "cron" );
        assertEquals( CronScheduler.class, runner.getScheduler().getClass() );
    }

}
