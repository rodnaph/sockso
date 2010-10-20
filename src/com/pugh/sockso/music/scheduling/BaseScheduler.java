
package com.pugh.sockso.music.scheduling;

import com.pugh.sockso.music.indexing.Indexer;

import java.util.Calendar;

import org.apache.log4j.Logger;

/**
 *  The base scheduler provides the functionality to run and check if the indexer
 *  needs to be kicked off.
 *
 */

public abstract class BaseScheduler extends Thread implements Scheduler {

    private static final Logger log = Logger.getLogger( BaseScheduler.class );

    private final Indexer indexer;

    /**
     *  Creates a new scheduler to run the specified indexer
     *
     *  @param indexer
     *
     */

    public BaseScheduler( final Indexer indexer ) {

        this.indexer = indexer;

    }

    /**
     *  This is the main loop for the scheduler
     *
     */

    @Override
    public void run() {

        while ( true ) {

            final Calendar now = Calendar.getInstance();

            log.debug( "Checking schedule" );

            if ( shouldRunAt(now.getTime()) ) {
                indexer.scan();
            }

            // run every minute
            try { Thread.sleep( 60000 ); }
                catch ( InterruptedException e ) {}

        }

    }

}
