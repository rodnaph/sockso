
package com.pugh.sockso.music.scheduling;

import com.pugh.sockso.music.indexing.Indexer;

import java.util.Date;

/**
 *  The manual scheduler never runs the indexer, so to update the collection
 *  you need to manually run the scan.
 *
 */

public class ManualScheduler extends BaseScheduler {

    /**
     *  Creates a manual scheduler
     *
     *  @param indexer
     *
     */
    
    public ManualScheduler( final Indexer indexer ) {
        
        super( indexer );
        
    }

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
