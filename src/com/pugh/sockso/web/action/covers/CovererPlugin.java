
package com.pugh.sockso.web.action.covers;

import com.pugh.sockso.Properties;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.Response;

public interface CovererPlugin {

    /**
     *  Cover plugins return true if they have got a cover to use and have
     *  set it in the response, false otherwise
     * 
     *  @param itemName
     * 
     *  @return
     * 
     *  @throws Exception 
     * 
     */

    public boolean serveCover( final String itemName ) throws Exception;

    /**
     *  Sets the database
     * 
     *  @param db 
     * 
     */
    
    public void setDatabase( final Database db );

    /**
     *  Sets the request object
     * 
     *  @param req
     * 
     */

    public void setRequest( final Request req );

    /**
     *  Sets the response object
     * 
     *  @param res 
     * 
     */
    
    public void setResponse( final Response res );

    /**
     *  Sets the properties object
     * 
     *  @param p 
     * 
     */

    public void setProperties( final Properties p );

    /**
     *  Sets the locale object
     * 
     *  @param locale 
     * 
     */

    public void setLocale( final Locale locale );

}
