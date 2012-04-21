
package com.pugh.sockso.web.action;

import com.pugh.sockso.web.action.covers.CovererPlugin;

import java.util.ArrayList;

public class Coverer extends BaseAction {
    
    private static final String CACHE_IMAGE_TYPE = "jpg";

    private final ArrayList<CovererPlugin> plugins;

    /**
     *  Create a new coverer action
     * 
     */

    public Coverer() {
        
        this.plugins = new ArrayList<CovererPlugin>();
        
    }

    /**
     *  Add a plugin, these are evaluated in order added
     * 
     *  @param plugin 
     * 
     */

    public void addPlugin( CovererPlugin plugin ) {

        plugins.add( plugin );

    }

    /**
     *  When handling a request just try and find a plugin to serve the cover
     * 
     */

    public void handleRequest() {
        
        for ( final CovererPlugin plugin : plugins ) {
            if ( plugin.serveCover("") ) {
                break;
            }
        }

    }

    /**
     *  No login required when serving covers, same as FileServer
     * 
     *  @return 
     * 
     */

    @Override
    public boolean requiresLogin() {

        return false;
        
    }

    /**
     *  No login means no session required at all
     * 
     *  @return
     * 
     */

    @Override
    public boolean requiresSession() {

        return false;

    }

}
