
package com.pugh.sockso.web.log;

import com.pugh.sockso.web.User;

/**
 *  Interface for web logging classes
 * 
 */

public interface RequestLogger {

    /**
     *  Logs a web request
     *
     *  @param user
     *  @param ipAddress
     *  @param requestUrl
     *  @param userAgent
     *  @param referer
     *  @param cookies
     *
     */

    public void log( final User user, final String ipAddress,
                     final String requestUrl, final String userAgent,
                     final String referer, final String cookies );

}
