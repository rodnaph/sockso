
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.web.Request;

import java.io.IOException;

public class SessionAction extends BaseApiAction {
    
    /**
     *  This does not require login as it's for testing logged in/out
     * 
     *  @return 
     * 
     */
    
    @Override
    public boolean requiresLogin() {
        
        return false;
        
    }
    
    /**
     *  Indicates if this action can handle the request
     * 
     *  @param req
     * 
     *  @return 
     * 
     */
    
    public boolean canHandle( final Request req ) {
        
        return req.getParamCount() == 2
            && req.getUrlParam( 1 ).equals( "session" );
        
    }
    
    /**
     *  Shows 1 if the user is logged in, 0 otherwise
     * 
     *  @throws IOException 
     * 
     */
    
    public void handleRequest() throws IOException {
        
        getResponse().showText( getUser() != null ? "1" : "0" );
        
    }
    
}
