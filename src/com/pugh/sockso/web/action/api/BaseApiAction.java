
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.web.action.BaseAction;

abstract public class BaseApiAction extends BaseAction implements ApiAction {

    public static final int DEFAULT_OFFSET = 0;
    
    public static final int DEFAULT_LIMIT = 100;
    
    public static final long DEFAULT_SINCE = 0;
    
    public static final String FROM_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Override
    public long getFromDate() {
        
        try {

            if ( getRequest().hasArgument( "fromDate" ) ) {

                long dateFrom = Long.parseLong( getRequest().getArgument( "fromDate" ) );
                
                return dateFrom;
            }

        }
        
        catch ( final NumberFormatException ignored ) {}

        return DEFAULT_SINCE;
    }
    
    /**
     *  Return the number of results to limit by
     *
     *  @return
     *
     */

    @Override
    public int getLimit() {

        return getUrlArgument( "limit", DEFAULT_LIMIT );

    }

    /**
     *  Returns the number to offset results by
     *
     *  @return
     *
     */

    @Override
    public int getOffset() {

        return getUrlArgument( "offset", DEFAULT_OFFSET );
        
    }
    
    /**
     *  Returns the value of the named url argument as an integer if specified,
     *  or returns the default if it's not or invalid
     * 
     *  @param name
     *  @param defaultValue
     * 
     *  @return 
     * 
     */
    
    protected int getUrlArgument( final String name, final int defaultValue ) {
        
        try {

            if ( getRequest().hasArgument(name) ) {
                return Integer.parseInt(
                    getRequest().getArgument( name )
                );
            }

        }
        
        catch ( final NumberFormatException ignored ) {}

        return defaultValue;

    }

    /**
     *  Indicates if the given string is an integer
     * 
     *  @param integer
     * 
     *  @return 
     * 
     */
   
    protected boolean isInteger( final String integer ) {
       
        try {
            Integer.parseInt( integer );
            return true;
        }
       
        catch ( final NumberFormatException e ) {}
       
        return false;
       
    }

}
