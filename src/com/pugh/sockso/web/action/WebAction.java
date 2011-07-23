
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
    
    void handleRequest() throws Exception;

    /**
     *  Initialises the web action with the "standard objects" associated
     *  with each request, the web action isn't really ready to be used
     *  until after this is called and it's loaded up
     *
     *  @param request request object
     *  @param response response object
     *  @param user the current user
     *  @param locale locale information
     *
     */
    
    void init(final Request req, final Response res, final User user, final Locale locale);

    /**
     *  Inidcates if this web action requires a login to be executed
     *
     *  @return boolean true if login required, false otherwise
     *
     */
    
    boolean requiresLogin();

    /**
     *  Sets the database for the action
     * 
     *  @param db
     *
     */
    
    void setDatabase(final Database db);

    /**
     *  Sets the locale for the action
     * 
     *  @param locale 
     * 
     */
    
    void setLocale(final Locale locale);

    /**
     *  Sets the properties for the action
     * 
     *  @param p 
     * 
     */
    
    void setProperties(final Properties p);

    /**
     *  Sets the request for the action
     * 
     *  @param req 
     * 
     */
    
    void setRequest(final Request req);

    /**
     *  Sets the response for the action to use
     * 
     *  @param res 
     * 
     */
    
    void setResponse(final Response res);

    /**
     *  Sets the user for the action
     * 
     *  @param user 
     * 
     */
    
    void setUser(final User user);
    
}
