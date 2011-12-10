
package com.pugh.sockso.web;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Track;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

/**
 *  This object wraps a request for some tracks and allows a central way of 
 *  resolving track queries.
 * 
 */

public class TracksRequest {
    
    private final Request req;

    private final Database db;

    private final Properties p;

    public TracksRequest( final Request req, final Database db, final Properties p ) {

        this.req = req;
        this.db = db;
        this.p = p;
        
    }

    /**
     *  Returns any requested tracks from URL params, or a path argument.
     * 
     *  @return
     * 
     *  @throws SQLException
     *  @throws BadRequestException 
     * 
     */

    public Track[] getRequestedTracks() throws SQLException, BadRequestException {

        final Vector<Track> allTracks = new Vector<Track>();
        final Vector<Track> urlParamTracks = getUrlParamTracks();
        final Vector<Track> pathTracks = getPathTracks();
        
        allTracks.addAll( urlParamTracks );
        allTracks.addAll( pathTracks );

        if ( isRandomRequest() ) {
            Collections.shuffle( allTracks );
        }
        
        return allTracks.toArray( new Track[] {} );

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

    public Track[] getRandomTracks() throws SQLException {

        final String limit = p.get( Constants.WWW_RANDOM_TRACK_LIMIT, "100" );
        final String sql = Track.getSelectFromSql() +
                     getTrackTypeSqlFilter() +
                     " order by " +db.getRandomFunction()+ "() " +
                     " limit ? ";
        
        PreparedStatement st = null;
        
        try {
            
            st = db.prepare( sql );
            st.setInt( 1, Integer.parseInt(limit) );

            return Track.createVectorFromResultSet( st.executeQuery() )
                    .toArray( new Track[] {} );

        }
        
        finally {
            Utils.close( st );
        }

    }
    
 
    /**
     *  Returns tracks specifed via play args in the request resource
     * 
     *  @return
     * 
     *  @throws SQLException
     *  @throws BadRequestException 
     * 
     */

    protected Vector<Track> getUrlParamTracks() throws SQLException, BadRequestException {
        
        final String orderBySql = isRandomRequest()
                ? " order by " +db.getRandomFunction()+ "() "
                : "";
        final Vector<Track> tracks = Track.getTracksFromPlayArgs( db, getUrlParams(), orderBySql );
        
        return tracks;

    }

    /**
     *  Indicates if the request is for a random ordering of tracks
     * 
     *  @return 
     * 
     */

    protected boolean isRandomRequest() {
        
        return req.getArgument( "orderBy" ).equals( "random" );

    }
    /**
     *  Returns any tracks specified by the "folder=XXX" parameter
     * 
     *  @return 
     * 
     */
    
    protected Vector<Track> getPathTracks() throws SQLException {

        if ( Utils.isFeatureEnabled(p,Constants.WWW_BROWSE_FOLDERS_ENABLED) ) {
            final String path = req.getArgument( "path" );
            if ( path.length() > 0 ) {
                return Track.getTracksFromPath( db, path );
            }
        }
        
        return new Vector<Track>();
        
    }

    /**
     *  Returns and parameters from the resource part of the request URL that
     *  look like play parameters (eg. tr123)
     * 
     *  @return 
     * 
     */

    protected String[] getUrlParams() {

        final ArrayList<String> params = new ArrayList<String>();
        final int paramCount = req.getParamCount();

        for ( int i=0; i<paramCount; i++ ) {
            final String param = req.getUrlParam( i );
            if ( param.matches("(tr|al|ar)\\d+") ) {
                params.add( param );
            }
        }
        
        return params.toArray( new String[] {} );

    }

}
