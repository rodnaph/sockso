
package com.pugh.sockso.music.scheduling;

import java.util.Date;

/**
 * The scheduler manages running the indexer on a schedule which is defined
 * in the application settings.
 *
 */

public interface Scheduler {

    /**
     * This method returns a boolean indicating if the scheduler should
     * mick off the indexer into running now.
     *
     * @return
     *
     */

    public boolean shouldRunAt( final Date date );

}
