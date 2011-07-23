/*
 * AbstractAction.java
 * 
 * Created on Aug 8, 2007, 7:49:12 PM
 * 
 * Abstract class for web "actions" to implement.  An action is
 * something that responds to a request.
 * 
 * NB!  The web action isn't really ready to be used until it's init()
 * method has been called and it's loaded with the request related obj's
 * 
 */

package com.pugh.sockso.web.action;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.Response;
import com.pugh.sockso.web.User;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import java.util.Vector;

public abstract class BaseAction implements WebAction {
    
    private Request req;
    private Response res;
    private User user;
    private Locale locale;
    private Database db;
    private Properties p;
    
    /**
     *  Initialises the web action with the "standard objects" associated
     *  with each request, the web action isn't really ready to be used
     *  until after this is called and it's loaded up
     * 
     *  @param request request object
     *  @param response response object
     *  @param user the current user
     *  @param locale locale information
     * 
     */
    
    @Override
    public void init( final Request req, final Response res, final User user, final Locale locale ) {
        this.req = req;
        this.res = res;
        this.user = user;
        this.locale = locale;
    }
    
    /**
     *  Inidcates if this web action requires a login to be executed
     * 
     *  @return boolean true if login required, false otherwise
     * 
     */
    
    @Override
    public boolean requiresLogin() {
        return true;
    }
    
    /**
     *  Some getters for the standard objects
     * 
     */
    
    protected Request getRequest() { return req; }
    protected Response getResponse() { return res; }
    protected User getUser() { return user; }
    protected Locale getLocale() { return locale; }
    protected Database getDatabase() { return db; }
    protected Properties getProperties() { return p; }

    /**
     *  And setters (used for DI during testing)
     * 
     */
    
    @Override
    public void setDatabase( final Database db ) { this.db = db; }
    @Override
    public void setUser( final User user ) { this.user = user; }
    @Override
    public void setResponse( final Response res ) { this.res = res; }
    @Override
    public void setRequest( final Request req ) { this.req = req; }
    @Override
    public void setLocale( final Locale locale ) { this.locale = locale; }
    @Override
    public void setProperties( final Properties p ) { this.p = p; }
    
    /**
     *  fetches the latest tracks (limited by limit, obv...)
     * 
     *  @param int limit
     * 
     *  @return
     * 
     *  @throws java.sql.SQLException
     * 
     */
    
    protected Vector<Track> getLatestTracks( final int limit ) throws SQLException {
        
        ResultSet rs = null;
        PreparedStatement st = null;
        
        try {
            
            // latest tracks
            String sql = Track.getSelectFromSql() +
                    " order by t.date_added desc " +
                    " limit ? ";
            st = db.prepare( sql );
            
            st.setInt( 1, limit );
            rs = st.executeQuery();
            
            return Track.createVectorFromResultSet( rs );
        
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
    }

    /**
     *  returns a where clause filtering tracks by the file types specified
     *  by the trackType argument from the request.  if no filter was specified
     *  then the empty string is returned (ie. no filter)
     *
     *  @return where sql filter if any was specified, empty string otherwise
     *
     */

    protected String getTrackTypeSqlFilter() {

        final StringBuffer filterBuffer = new StringBuffer( "" );
        final String trackTypes = req.getArgument( "trackType" );

        // track types can be a commer seperated string of file types
        // @TODO right() not sqlite compatible
        for ( final String type : trackTypes.split(",") ) {
            if ( !type.equals("") ) {
                final int length = type.length();
                filterBuffer.append( " and lower(substr(t.path,length(t.path)-" +(length-1)+ "," +length+ ")) = '"+type+"' " );
            }
        }

        final String filter = filterBuffer.toString();
        
        return filter.equals( "" )
                ? ""
                : " where " + filter.substring( 4 );

    }

    /**
     *  Returns a random Vector of Track objects from the collection.  The number
     *  of tracks returned is specified by the Constants.WWW_RANDOM_TRACK_LIMIT
     *  property.
     * 
     *  @return
     * 
     *  @throws java.sql.SQLException
     * 
     */

    protected Vector<Track> getRandomTracks() throws SQLException {

        final String limit = p.get( Constants.WWW_RANDOM_TRACK_LIMIT, "100" );
        final String sql = Track.getSelectFromSql() +
                     getTrackTypeSqlFilter() +
                     " order by " +db.getRandomFunction()+ "() " +
                     " limit ? ";
        
        PreparedStatement st = null;
        
        try {
            
            st = db.prepare( sql );
            st.setInt( 1, Integer.parseInt(limit) );

            return Track.createVectorFromResultSet( st.executeQuery() );

        }
        
        finally {
            Utils.close( st );
        }

    }
    
    /**
     *  Returns all tracks specified in the request, either by the passed in
     *  playArgs (eg. 'tr1/tr2'), or by a path parameter if folder browsing
     *  is enabled.
     * 
     *  @param playArgs
     * 
     *  @return
     * 
     *  @throws SQLException
     *  @throws BadRequestException 
     * 
     */
    
    protected Vector<Track> getRequestedTracks( final String[] playArgs ) throws SQLException, BadRequestException {
        
        final Vector<Track> allTracks = new Vector<Track>();
        final Vector<Track> urlParamTracks = getUrlParamTracks( playArgs );
        final Vector<Track> pathTracks = getPathTracks();
        
        allTracks.addAll( urlParamTracks );
        allTracks.addAll( pathTracks );
        
        return allTracks;
        
    }

    /**
     *  Returns any tracks specified by the "folder=XXX" parameter
     * 
     *  @return 
     * 
     */
    
    protected Vector<Track> getPathTracks() throws SQLException {

        if ( Utils.isFeatureEnabled(getProperties(),Constants.WWW_BROWSE_FOLDERS_ENABLED) ) {
            final String path = getRequest().getArgument( "path" );
            if ( path.length() > 0 ) {
                return Track.getTracksFromPath( getDatabase(), path );
            }
        }
        
        return new Vector<Track>();
        
    }

    /**
     *  Returns tracks specified via url params (eg. "tr1/ar3/pl6")
     * 
     *  @param args
     * 
     *  @return
     * 
     *  @throws SQLException
     *  @throws BadRequestException 
     * 
     */
    
    protected Vector<Track> getUrlParamTracks( final String[] args ) throws SQLException, BadRequestException {
        
        final Database db = getDatabase();
        final Request req = getRequest();
        final String orderBySql = req.getArgument("orderBy").equals("random")
                ? " order by rand() "
                : "";
        final Vector<Track> tracks = Track.getTracksFromPlayArgs( db, args, orderBySql );
        
        return tracks;
        
    }

}
