
package com.pugh.sockso.web.action;

import com.pugh.sockso.web.action.covers.CovererPlugin;

import java.util.ArrayList;

public class Coverer extends BaseAction {
    
    private static final String CACHE_IMAGE_TYPE = "jpg";

    private final ArrayList<CovererPlugin> plugins;

    public Coverer() {
        
        this.plugins = new ArrayList<CovererPlugin>();
        
    }

    public void addPlugin( CovererPlugin plugin ) {

        plugins.add( plugin );

    }

    public void handleRequest() {
        
        for ( final CovererPlugin plugin : plugins ) {
            if ( plugin.serveCover("") ) {
                break;
            }
        }

    }

    @Override
    public boolean requiresLogin() {

        return false;
        
    }

    @Override
    public boolean requiresSession() {

        return false;

    }

}
