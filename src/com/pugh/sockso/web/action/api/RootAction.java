
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.templates.api.TApi;
import com.pugh.sockso.web.Request;

import java.io.IOException;

public class RootAction extends BaseApiAction {

    /**
     *  Handles requests to /
     * 
     *  @param req
     * 
     *  @return 
     * 
     */
    
    @Override
    public boolean canHandle( final Request req ) {
        
        return req.getParamCount() == 1;
        
    }
    
    /**
     *  Outputs server info
     * 
     *  @throws IOException 
     * 
     */
    
    @Override
    public void handleRequest() throws IOException {

        final TApi tpl = new TApi();
        tpl.setProperties( getProperties() );

        getResponse().showJson( tpl.makeRenderer() );
        
    }

}
