
package com.pugh.sockso.web.action;

import com.pugh.sockso.web.Request;
import com.pugh.sockso.templates.web.TSharePopup;

import java.io.IOException;

/**
 *  Shows the page to share the player on websites, etc...
 *
 */

public class Sharer extends BaseAction {

    /**
     *  Handles the request
     *
     *  @throws IOException
     *
     */

    public void handleRequest() throws IOException {

        final Request req = getRequest();
        final String[] playArgs = req.getPlayParams( false );

        showSharePage( playArgs );

    }

    /**
     *  Shows the share page for the specified play args (eg. ar123, tr456)
     *
     *  @param playArgs
     *
     *  @throws IOException
     *
     */

    protected void showSharePage( final String[] playArgs ) throws IOException {

        final TSharePopup tpl = new TSharePopup();
        
        tpl.setLocale( getLocale() );
        tpl.setUser( getUser() );
        tpl.setRequest( getRequest() );
        tpl.setProperties( getProperties() );
        tpl.setPlayArgs( playArgs );
        
        getResponse().showHtml( tpl.makeRenderer() );

    }
    
}
