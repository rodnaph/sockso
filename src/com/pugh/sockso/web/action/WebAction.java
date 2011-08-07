
package com.pugh.sockso.web.action;

import com.pugh.sockso.Properties;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.Response;
import com.pugh.sockso.web.User;

public interface WebAction {

    /**
     *  the handler for executing this web action
     *
     *  @throws Exception
     *
     */
    
    public void handleRequest() throws Exception;

    /**
     *  Inidcates if this web action requires a login to be executed
     *
     *  @return boolean true if login required, false otherwise
     *
     */
    
    public boolean requiresLogin();

    /**
     *  Sets the database for the action
     * 
     *  @param db
     *
     */
    
    public void setDatabase( final Database db );

    /**
     *  Sets the properties for the action
     * 
     *  @param p 
     * 
     */
    
    public void setProperties( final Properties p );

    /**
     *  Sets the locale for the action
     * 
     *  @param locale 
     * 
     */
    
    public void setLocale( final Locale locale );

    /**
     *  Sets the request for the action
     * 
     *  @param req 
     * 
     */
    
    public void setRequest( final Request req );

    /**
     *  Sets the response for the action to use
     * 
     *  @param res 
     * 
     */
    
    public void setResponse( final Response res );

    /**
     *  Sets the user for the action
     * 
     *  @param user 
     * 
     */
    
    public void setUser( final User user );
    
}
