
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.action.WebAction;
import java.text.ParseException;
import java.util.Date;

public interface ApiAction extends WebAction {

    /**
     *  Indicates if the action can handle the specified request
     *
     *  @param req
     *
     *  @return
     *
     */
    
    public boolean canHandle( final Request req );

    /**
     *  Return the number of results to limit by
     *
     *  @return
     *
     */
    
    public int getLimit();

    /**
     *  Returns the number to offset results by
     *
     *  @return
     *
     */
    
    public int getOffset();
    
    /**
     *  Returns the start datetime to restrict the results by
     *
     *  @return
     *
     *  @throws ParseException if the date string cannot be parsed
     */
    
    public Date getFromDate() throws ParseException;
}
