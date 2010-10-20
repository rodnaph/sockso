
package com.pugh.sockso.events;

public class LatestVersionEvent {

    private final String version;

    public LatestVersionEvent( final String version ) {

        this.version = version;
        
    }

    /**
     *  Returns the latest version number
     *
     *  @return
     *
     */

    public String getVersion() {

        return version;
        
    }

}
