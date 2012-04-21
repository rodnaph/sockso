
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

    }

    public void handleRequest() {
        
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
