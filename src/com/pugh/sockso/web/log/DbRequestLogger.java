
package com.pugh.sockso.web.log;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.web.User;

import java.sql.SQLException;

/**
 *  Logs requests to the database
 *
 */

public class DbRequestLogger implements RequestLogger {

    private final Database db;

    /**
     *  Creates a new database logger
     *
     *  @param db
     *
     */
    
    public DbRequestLogger( final Database db ) {
        
        this.db = db;
        
    }

    /**
     *  Logs a web request
     * 
     *  @param user
     *  @param ipAddress
     *  @param resource
     *  @param userAgent
     *  @param referer
     *  @param cookie
     * 
     */
    
    public void log( final User user, final String ipAddress,
                            final String resource, final String userAgent,
                            final String referer, final String cookies ) {

        try {

            final String sql = " insert into request_log ( user_id, ip_address, " +
                            " request_url, user_agent, referer, cookies, " +
                            " date_of_request ) " +
                         " values ( " +
                            (user == null ? "null" : user.getId()) + ", " +
                            "'" +db.escape(ipAddress)+ "', " +
                            "'" +db.escape(resource)+ "', " +
                            "'" +db.escape(userAgent)+ "', " +
                            "'" +db.escape(referer)+ "', " +
                            "'" +db.escape(cookies)+ "', " +
                            " current_timestamp " +
                         " ) ";

            db.update( sql );

        }

        catch ( final SQLException e ) {
            e.printStackTrace();
        }

    }

}
