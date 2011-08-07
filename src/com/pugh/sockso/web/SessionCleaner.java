
package com.pugh.sockso.web;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.Date;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

/**
 *  This class runs in the background to keep the sessions table clean of any
 *  old data not needed any more.
 *
 *  If it arises that we need to do this for other tables as well then this class
 *  can probably be generified.
 *
 */

public class SessionCleaner {

    public static Logger log = Logger.getLogger( SessionCleaner.class );

    private static final long ONE_HOUR = 60 * 60 * 1000;

    private final Database db;

    /**
     *  Creates a new session cleaner
     *
     *  @param db
     * 
     */

    @Inject
    public SessionCleaner( final Database db ) {
        
        this.db = db;
        
    }

    /**
     *  starts the thread that checks the sessions table to clean
     *  out sessions that have expired.  this runs once an hour.
     *
     */

    public void init() {

        new Thread() {

            @Override
            public void run() {

                while ( true) {
                    cleanSessionsTable();
                    try { Thread.sleep( ONE_HOUR ); }
                        catch ( final InterruptedException e ) {}
                }

            }

        }.start();

    }

    /**
     *  Cleans the sessions table of any stale data
     * 
     */
    
    protected void cleanSessionsTable() {

        PreparedStatement st = null;

        try {

            // delete any sessions greater than a week old

            final Timestamp oneWeekAgo = new Timestamp( new Date().getTime() - Constants.ONE_WEEK_IN_MILLIS );
            final String sql = " delete from sessions " +
                               " where date_created < ? ";

            st = db.prepare( sql );
            st.setTimestamp( 1, oneWeekAgo );
            st.execute();

        }

        catch ( final SQLException e ) {
            log.debug( e );
        }

        finally {
            Utils.close( st );
        }

    }

}
