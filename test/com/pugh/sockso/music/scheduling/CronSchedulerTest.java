
package com.pugh.sockso.music.scheduling;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.tests.SocksoTestCase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class CronSchedulerTest extends SocksoTestCase {

    private Properties p;
    private Scheduler s;
    private DateFormat df;

    @Override
    public void setUp() {
        
        p = new StringProperties();
        s = new CronScheduler( null, p );
        df = new SimpleDateFormat( "y-M-d H:m:s" );

    }

    public void testRunningAtAMinutePastEveryHour() throws Exception {

        p.set( Constants.SCHED_CRON_TAB, "1 * * * *" );
        
        assertTrue( s.shouldRunAt(df.parse("2009-10-10 11:01:00")) );
        assertTrue( s.shouldRunAt(df.parse("2009-10-10 09:01:00")) );
        assertTrue( s.shouldRunAt(df.parse("2009-10-10 23:01:00")) );
        assertTrue( s.shouldRunAt(df.parse("2009-10-10 00:01:00")) );

        assertFalse( s.shouldRunAt(df.parse("2009-10-10 00:02:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-10-10 12:51:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-10-10 03:11:00")) );

    }

    public void testRunningAtEveryMinute() throws Exception {

        p.set( Constants.SCHED_CRON_TAB, "* * * * *" );

        assertTrue( s.shouldRunAt(df.parse("2009-10-10 11:09:00")) );
        assertTrue( s.shouldRunAt(df.parse("2009-10-10 09:23:00")) );
        assertTrue( s.shouldRunAt(df.parse("2009-10-10 23:59:00")) );
        assertTrue( s.shouldRunAt(df.parse("2009-10-10 00:00:00")) );

    }

    public void testRunningAtAPeriodOfMinutes() throws Exception {

        p.set( Constants.SCHED_CRON_TAB, "*/5 * * * *" );

        assertTrue( s.shouldRunAt(df.parse("2009-10-10 11:05:00")) );
        assertTrue( s.shouldRunAt(df.parse("2009-10-10 09:20:00")) );
        assertTrue( s.shouldRunAt(df.parse("2009-10-10 23:00:00")) );
        assertTrue( s.shouldRunAt(df.parse("2009-10-10 00:55:00")) );

        assertFalse( s.shouldRunAt(df.parse("2009-10-10 00:02:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-10-10 12:51:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-10-10 03:11:00")) );

    }

    public void testRunningBetweenPeriodOfMinutes() throws Exception {

        p.set( Constants.SCHED_CRON_TAB, "1-6 * * * *" );

        assertTrue( s.shouldRunAt(df.parse("2009-10-10 11:05:00")) );
        assertTrue( s.shouldRunAt(df.parse("2009-10-10 09:02:00")) );
        assertTrue( s.shouldRunAt(df.parse("2009-10-10 23:01:00")) );
        assertTrue( s.shouldRunAt(df.parse("2009-10-10 00:06:00")) );

        assertFalse( s.shouldRunAt(df.parse("2009-10-10 00:00:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-10-10 12:07:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-10-10 03:11:00")) );

    }

    public void testRunningAtCertainMinutes() throws Exception {

        p.set( Constants.SCHED_CRON_TAB, "0,1,3,27 * * * *" );

        assertTrue( s.shouldRunAt(df.parse("2009-10-10 11:01:00")) );
        assertTrue( s.shouldRunAt(df.parse("2009-10-10 09:03:00")) );
        assertTrue( s.shouldRunAt(df.parse("2009-10-10 23:27:00")) );
        assertTrue( s.shouldRunAt(df.parse("2009-10-10 23:00:00")) );

        assertFalse( s.shouldRunAt(df.parse("2009-10-10 00:02:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-10-10 12:51:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-10-10 03:11:00")) );

    }

    public void testRunningAtHourPartOfSpec() throws Exception {

        p.set( Constants.SCHED_CRON_TAB, "0 5 * * *" );

        assertTrue( s.shouldRunAt(df.parse("2009-10-10 05:00:00")) );

        assertFalse( s.shouldRunAt(df.parse("2009-10-10 00:00:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-10-10 12:00:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-10-10 03:00:00")) );

    }

    public void testRunningAtDayOfMonthPartOfSpec() throws Exception {

        p.set( Constants.SCHED_CRON_TAB, "0 5 2 * *" );

        assertTrue( s.shouldRunAt(df.parse("2009-10-02 05:00:00")) );

        assertFalse( s.shouldRunAt(df.parse("2009-10-05 05:00:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-10-22 05:00:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-10-01 05:00:00")) );

    }

    public void testRunningAtMonthPartOfSpec() throws Exception {

        p.set( Constants.SCHED_CRON_TAB, "0 5 2 2 *" );

        assertTrue( s.shouldRunAt(df.parse("2009-02-02 05:00:00")) );

        assertFalse( s.shouldRunAt(df.parse("2009-12-02 05:00:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-01-02 05:00:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-03-02 05:00:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-07-02 05:00:00")) );

    }

    public void testRunningAtDayOfWeekPartOfSpec() throws Exception {

        p.set( Constants.SCHED_CRON_TAB, "* * * * 1" );

        assertTrue( s.shouldRunAt(df.parse("2009-12-28 05:00:00")) );

        assertFalse( s.shouldRunAt(df.parse("2009-12-27 05:00:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-12-29 05:00:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-12-30 05:00:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-12-31 05:00:00")) );

    }

    public void testRunningOnASundayUsingSeven() throws Exception {

        p.set( Constants.SCHED_CRON_TAB, "* * * * 7" );

        assertTrue( s.shouldRunAt(df.parse("2009-12-27 05:00:00")) );

        assertFalse( s.shouldRunAt(df.parse("2009-12-26 05:00:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-12-28 05:00:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-12-29 05:00:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-12-30 05:00:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-12-31 05:00:00")) );

    }

    public void testUsingWordAsDayOfWeekPartOfSpec() throws Exception {

        p.set( Constants.SCHED_CRON_TAB, "* * * * mon" );

        assertTrue( s.shouldRunAt(df.parse("2009-12-28 05:00:00")) );

        assertFalse( s.shouldRunAt(df.parse("2009-12-27 05:00:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-12-29 05:00:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-12-30 05:00:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-12-31 05:00:00")) );

    }

    public void testUsingWordAsMonthPartOfSpec() throws Exception {

        p.set( Constants.SCHED_CRON_TAB, "0 5 2 feb *" );

        assertTrue( s.shouldRunAt(df.parse("2009-02-02 05:00:00")) );

        assertFalse( s.shouldRunAt(df.parse("2009-12-02 05:00:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-01-02 05:00:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-03-02 05:00:00")) );
        assertFalse( s.shouldRunAt(df.parse("2009-07-02 05:00:00")) );

    }

    public void testSpecialContabEntries() throws Exception {

        p.set( Constants.SCHED_CRON_TAB, "@yearly" );
        assertTrue( s.shouldRunAt(df.parse("2009-01-01 00:00:00")) );

        p.set( Constants.SCHED_CRON_TAB, "@annually" );
        assertTrue( s.shouldRunAt(df.parse("2009-01-01 00:00:00")) );

        p.set( Constants.SCHED_CRON_TAB, "@monthly" );
        assertTrue( s.shouldRunAt(df.parse("2009-01-01 00:00:00")) );
        assertTrue( s.shouldRunAt(df.parse("2009-12-01 00:00:00")) );

        p.set( Constants.SCHED_CRON_TAB, "@weekly" );
        assertTrue( s.shouldRunAt(df.parse("2009-12-27 00:00:00")) );
        assertTrue( s.shouldRunAt(df.parse("2009-12-20 00:00:00")) );

        p.set( Constants.SCHED_CRON_TAB, "@daily" );
        assertTrue( s.shouldRunAt(df.parse("2009-01-01 00:00:00")) );
        assertTrue( s.shouldRunAt(df.parse("2010-01-10 00:00:00")) );
        assertTrue( s.shouldRunAt(df.parse("2009-01-21 00:00:00")) );

        p.set( Constants.SCHED_CRON_TAB, "@midnight" );
        assertTrue( s.shouldRunAt(df.parse("2009-01-01 00:00:00")) );
        assertTrue( s.shouldRunAt(df.parse("2010-01-10 00:00:00")) );
        assertTrue( s.shouldRunAt(df.parse("2009-01-21 00:00:00")) );

        p.set( Constants.SCHED_CRON_TAB, "@hourly" );
        assertTrue( s.shouldRunAt(df.parse("2009-01-01 00:00:00")) );
        assertTrue( s.shouldRunAt(df.parse("2009-01-01 15:00:00")) );
        assertTrue( s.shouldRunAt(df.parse("2009-01-01 23:00:00")) );

    }

    public void testSpecialCaseForDayOfWeekAndDayOfMonth() throws Exception {

        // day of month wrong, but day of week ok
        p.set( Constants.SCHED_CRON_TAB, "0 0 28 * 0" );
        assertTrue( s.shouldRunAt(df.parse("2009-12-27 00:00:00")) );

        // day of week wrong, but day of month ok
        p.set( Constants.SCHED_CRON_TAB, "0 0 27 * 1" );
        assertTrue( s.shouldRunAt(df.parse("2009-12-27 00:00:00")) );

    }

    public void testFalseReturnedWhenCrontabIsInvalid() throws Exception {
        
        p.set( Constants.SCHED_CRON_TAB, "* * * *" ); // only 4
        assertFalse( s.shouldRunAt(df.parse("2009-09-09 12:00:00")) );
        
    }

}
