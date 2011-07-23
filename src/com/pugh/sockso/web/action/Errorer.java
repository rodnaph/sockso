
package com.pugh.sockso.web.action;

import com.pugh.sockso.templates.web.TBurp;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.Response;
import com.pugh.sockso.web.BadRequestException;

import java.io.IOException;

import java.sql.SQLException;

/**
 *  a web action to show error pages
 * 
 */

public class Errorer extends BaseAction {

    private final BadRequestException e;
    private final boolean showStackTrace;
    
    /**
     *  constructor
     * 
     *  @param e
     *  @param showStackTrace
     * 
     */
    
    public Errorer( final BadRequestException e, final boolean showStackTrace ) {
        
        this.e = e;
        this.showStackTrace = showStackTrace;
        
    }
    
    /**
     *  handles the request to show the error page
     * 
     */
    
    public void handleRequest() throws IOException, SQLException {

        showBurp();
        
    }
    
    /**
     *  shows the main "burp" page to display the error message
     * 
     */
    
    protected void showBurp() throws IOException, SQLException {
        
        final Request req = getRequest();
        final Response res = getResponse();

        final TBurp tpl = new TBurp();
        tpl.setHost( req.getHeader("Host") );
        tpl.setReferer( req.getHeader("Referer") );
        tpl.setException( e );
        tpl.setMessage( e.getMessage() );
        tpl.setShowStackTrace( showStackTrace );
        tpl.setStatusCode( e.getStatusCode() );
        
        res.setStatus( e.getStatusCode() );
        res.showHtml( tpl );

    }
    
    /**
     *  don't require login on error pages.  cause that'd be stoopid.
     * 
     *  @return
     * 
     */
    
    @Override
    public boolean requiresLogin() {
        
        return false;
        
    }
    
}
