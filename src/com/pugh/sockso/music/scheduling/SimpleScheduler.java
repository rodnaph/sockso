
package com.pugh.sockso.music.scheduling;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;

import com.pugh.sockso.music.indexing.Indexer;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

public class SimpleScheduler implements Scheduler {

    private static final Logger log = Logger.getLogger( SimpleScheduler.class );

    private static final int DEFAULT_INTERVAL = 30;
    
    private final Properties p;

    /**
     * Constructor
     *
     * @param p
     *
     */

    public SimpleScheduler( final Properties p ) {

        this.p = p;

    }

    /**
     * Indicates if the scheduler should run at the specified time
     *
     * @param date
     *
     * @return
     *
     */

    public boolean shouldRunAt( final Date date ) {
        
        final int interval = (int) p.get( Constants.SCHED_SIMPLE_INTERVAL, DEFAULT_INTERVAL );
        final Calendar now = Calendar.getInstance();

        now.setTime( date );
        
        final int minsSinceMidnight = now.get( Calendar.HOUR_OF_DAY ) * 60 + now.get( Calendar.MINUTE );
        final int diff = minsSinceMidnight % interval;

        return diff == 0;
        
    }

}
