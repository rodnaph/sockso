
package com.pugh.sockso.web.log;

import com.pugh.sockso.web.User;

/**
 *  A request logger that does nothing
 *
 */

public class NullRequestLogger implements RequestLogger {

    public void log( final User user, final String ipAddress,
                     final String requestUrl, final String userAgent,
                     final String referer, final String cookies ) {}


}
