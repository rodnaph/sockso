/*
 * Nater.java
 * 
 * Created on Aug 19, 2007, 3:02:38 PM
 * 
 * For doing stuff with NAT...
 * 
 */

package com.pugh.sockso.web.action;

import com.pugh.sockso.Utils;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.templates.web.TNatTestResponse;

import java.io.IOException;

import org.apache.log4j.Logger;

public class Nater extends BaseAction {

    private static final Logger log = Logger.getLogger( Nater.class );
    
    public static final String NAT_TEST_STRING = Utils.getRandomString( 20 );
    
    /**
     *  handles a web request
     * 
     *  @param req the request object
     *  @param res the response object
     *  @param user the user object
     * 
     *  @throws IOException
     *  @throws BadRequestException
     * 
     */
    
    public void handleRequest() throws IOException, BadRequestException {

        final Request req = getRequest();
        final String type = req.getUrlParam( 1 );
        
        if ( type.equals("test") )
            sendTestResponse();
        else
            throw new BadRequestException( "unknown command", 400 );
        
    }
    
    /**
     *  a nat test request, send our test response string
     * 
     *  @param res the response object
     * 
     *  @throws IOException
     * 
     */
    
    protected void sendTestResponse() throws IOException {
        
        final TNatTestResponse tpl = new TNatTestResponse();
        tpl.setMessage( NAT_TEST_STRING );
        
        getResponse().showText( tpl.makeRenderer() );

    }
    

}
