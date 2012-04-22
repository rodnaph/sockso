
package com.pugh.sockso.web.action;

import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.action.covers.CovererPlugin;

import java.util.ArrayList;

import org.apache.log4j.Logger;

public class Coverer extends BaseAction {

    private static final Logger log = Logger.getLogger( Coverer.class );
    
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

    public void handleRequest() throws Exception {

        final Request req = getRequest();
        final String itemName = req.getUrlParam( 2 );
        
        for ( final CovererPlugin plugin : plugins ) {

            plugin.setRequest( getRequest() );
            plugin.setResponse( getResponse() );
            plugin.setDatabase( getDatabase() );
            plugin.setProperties( getProperties() );
            plugin.setLocale( getLocale() );

            if ( plugin.serveCover(itemName) ) {
                log.debug( "Served cover with " + plugin.getClass().getSimpleName() );
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
