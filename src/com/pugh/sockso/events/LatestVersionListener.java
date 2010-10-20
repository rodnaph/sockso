
package com.pugh.sockso.events;

public interface LatestVersionListener {

    /**
     *  Handler for when we receive the latest version
     *
     *  @param version
     *
     */

    public void latestVersionReceived( final LatestVersionEvent evt );

}
