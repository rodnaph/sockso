
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.action.WebAction;

public interface ApiAction extends WebAction {

    /**
     *  Indicates if the action can handle the specified request
     *
     *  @param req
     *
     *  @return
     *
     */
    
    boolean canHandle( final Request req );

    /**
     *  Return the number of results to limit by
     *
     *  @return
     *
     */
    
    int getLimit();

    /**
     *  Returns the number to offset results by
     *
     *  @return
     *
     */
    
    int getOffset();
    
}
