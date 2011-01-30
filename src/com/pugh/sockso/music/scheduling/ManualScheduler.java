
package com.pugh.sockso.music.scheduling;

import java.util.Date;

/**
 *  The manual scheduler never runs the indexer, so to update the collection
 *  you need to manually run the scan.
 *
 */

public class ManualScheduler implements Scheduler {

    /**
     *  Always return false
     *
     *  @param date
     *
     *  @return
     *
     */
    
    public boolean shouldRunAt( final Date date ) {
        
        return false;
        
    }

}
