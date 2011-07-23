
package com.pugh.sockso.web;

import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Date;

import org.apache.log4j.Logger;

/**
 *  Handles storing and retreiving information from the session
 * 
 */

public class Session {

    protected static final String SESS_ID_COOKIE = "sessId";
    protected static final String SESS_CODE_COOKIE = "sessCode";

    private final Logger log = Logger.getLogger( Session.class );
    
    private final Database db;
    private final Request req;
    private final Response res;

    /**
     *  Constructor
     *
     *  @param db
     *  @param req
     *  @param res
     *
     */

    public Session( final Database db, final Request req, final Response res ) {

        this.db = db;
        this.req = req;
        this.res = res;

    }

    /**
     *  Creates a new session for the user with the specified ID
     * 
     *  @param userId
     * 
     *  @throws java.sql.SQLException
     * 
     */
    
    public void create( final int userId ) throws SQLException {

        PreparedStatement st = null;
        ResultSet rs = null;
        String sql = "";

        try {

            final String sessCode = Utils.getRandomString( 10 );
            sql = " insert into sessions ( code, user_id, date_created ) " +
                  " values ( ?, ?, current_timestamp ); ";
            st = db.prepare( sql );
            st.setString( 1, sessCode );
            st.setInt( 2, userId );
            st.execute();

            Utils.close( st );

            // fetch created session info, then set cookies
            // and send user back to home page
            sql = " select s.id as id " +
                  " from sessions s " +
                    " where s.code = ? " +
                        " and s.user_id = ? " +
                  " order by s.date_created desc " +
                  " limit 1 ";
            st = db.prepare( sql );
            st.setString( 1, sessCode );
            st.setInt( 2, userId );
            rs = st.executeQuery();

            if ( !rs.next() )
                throw new SQLException( "could not fetch session id" );

            final int sessId = rs.getInt( "id" );

            res.addCookie( new HttpResponseCookie(SESS_ID_COOKIE,Integer.toString(sessId)) );
            res.addCookie( new HttpResponseCookie(SESS_CODE_COOKIE,sessCode) );

        }

        finally {
            Utils.close( rs );
            Utils.close( st );
        }

    }

    /**
     *  Destroys the current session
     * 
     */

    public void destroy() {

        res.addCookie( new HttpResponseCookie(SESS_ID_COOKIE,"",new Date(),"/") );
        res.addCookie( new HttpResponseCookie(SESS_CODE_COOKIE,"",new Date(),"/") );

    }

    /**
     *  tries to use cookies to restore a user session
     *
     *  @return User if logged in, null otherwise
     *
     */

    protected User getCurrentUser() throws SQLException {

        User user = null;
        ResultSet rs = null;
        PreparedStatement st = null;

        try {

            final int sessId = fetchSessionId( req );
            final String sessCode = fetchSessionCode( req );
            final String sql = " select u.id, u.name, u.email, u.is_admin " +
                               " from users u " +
                                   " inner join sessions s " +
                                   " on s.user_id = u.id " +
                               " where s.id = ? " +
                                   " and s.code = ? ";

            st = db.prepare( sql );
            st.setInt( 1, sessId );
            st.setString( 2, sessCode );

            rs = st.executeQuery();

            if ( rs.next() ) {
                log.debug( "Fetched user!" );
                user = new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    "",
                    rs.getString("email"),
                    sessId,
                    sessCode,
                    rs.getBoolean("is_admin")
                );
            }

        }

        catch ( final NumberFormatException e ) {
            // probably just no session
            log.error( e );
        }

        finally {
            Utils.close( rs );
            Utils.close( st );
        }

        return user;

    }

    /**
     *  returns the session id for the session, preferably from a cookie, but will
     *  get it from the request if it's not present here.  if nothing is found (or
     *  invalid data is found) it'll return -1.
     *
     *  @param Request req
     *
     *  @return
     *
     */

    protected int fetchSessionId( final Request req ) {

        try {

            final String[] sessIds = new String[] {
                req.getCookie( SESS_ID_COOKIE ),
                req.getArgument( SESS_ID_COOKIE )
            };

            for ( final String sessId : sessIds ) {
                if ( sessId.matches("^\\d+$") )
                    return Integer.parseInt( sessId );
            }

        }

        catch ( final NumberFormatException e ) { /* ignore, we'll return the default */ }

        return -1;

    }

    /**
     *  fetches the session code first from a cookie, but will also try from the request.
     *  return the empty string if nothing is found.
     *
     *  @param Request req
     *
     *  @return
     *
     */

    protected String fetchSessionCode( final Request req ) {

        final String[] sessCodes = new String[] {
            req.getCookie( SESS_CODE_COOKIE ),
            req.getArgument( SESS_CODE_COOKIE )
        };

        for ( final String sessCode : sessCodes ) {
            if ( !sessCode.equals("") ) {
                return sessCode;
            }
        }

        return "";

    }

}
