
package com.pugh.sockso;

import com.pugh.sockso.events.LatestVersionListener;

/**
 *  interface for Sockso managers like the GUI or and the Console
 * 
 */

public interface Manager extends LatestVersionListener {

    /**
     *  closes the manager
     * 
     */
    
    public void close();

    /**
     *  opens the manager ready for the user to onteract with
     * 
     */
    
    public void open();

}
