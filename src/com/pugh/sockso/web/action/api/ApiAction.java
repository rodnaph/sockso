
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.ObjectCache;
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
     *  Sets the object cache for the action
     * 
     *  @param objectCache 
     * 
     */
    
    public void setObjectCache( final ObjectCache objectCache );
    
    /**
     *  Returns the actions object cache, or null if not set
     * 
     *  @return 
     * 
     */
    
    public ObjectCache getObjectCache();
    
}
