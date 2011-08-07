
package com.pugh.sockso.music.scheduling;

import com.pugh.sockso.Properties;
import com.pugh.sockso.music.indexing.Indexer;

import java.util.Calendar;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 *  The scheduler runner provides the functionality to run and check if the
 *  indexer needs to be kicked off.
 *
 */

@Singleton
public class SchedulerRunner extends Thread {

    private static final int ONE_MINUTE = 60000;

    private static final Logger log = Logger.getLogger( SchedulerRunner.class );

    private final Indexer indexer;

    private final Properties p;

    /**
     *  Creates a new scheduler to run the specified indexer
     *
     *  @param indexer
     *
     */

    @Inject
    public SchedulerRunner( final Indexer indexer, final Properties p ) {

        this.indexer = indexer;
        this.p = p;

    }

    /**
     *  This is the main loop for the scheduler
     *
     */

    @Override
    public void run() {

        while ( true ) {

            final Calendar now = Calendar.getInstance();
            final Scheduler scheduler = getScheduler();

            log.debug( "Checking schedule (" +scheduler.getClass().getSimpleName()+ ")" );

            if ( scheduler.shouldRunAt(now.getTime()) ) {
                indexer.scan();
            }

            try { Thread.sleep(ONE_MINUTE); }
                catch ( InterruptedException e ) {}

        }

    }

    /**
     *  Returns a new instance of the configured scheduler
     *
     *  @return
     *
     */

    protected Scheduler getScheduler() {

        final String scheduler = p.get( "scheduler" );

        if ( scheduler.equals("cron") ) {
            return new CronScheduler( p );
        }
        
        else if ( scheduler.equals("manual") ) {
            return new ManualScheduler();
        }

        return new SimpleScheduler( p );

    }

}
