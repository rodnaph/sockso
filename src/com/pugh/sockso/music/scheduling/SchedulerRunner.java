
package com.pugh.sockso.music.scheduling;

import com.pugh.sockso.Properties;
import com.pugh.sockso.music.indexing.Indexer;

import java.util.Calendar;

import org.apache.log4j.Logger;

/**
 *  The base scheduler provides the functionality to run and check if the indexer
 *  needs to be kicked off.
 *
 */

public class SchedulerRunner extends Thread {

    private static final Logger log = Logger.getLogger( SchedulerRunner.class );

    private final Indexer indexer;

    private final Properties p;

    /**
     *  Creates a new scheduler to run the specified indexer
     *
     *  @param indexer
     *
     */

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

            // run every minute
            try { Thread.sleep( 3000 ); }
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
