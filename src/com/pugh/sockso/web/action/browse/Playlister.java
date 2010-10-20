/**
 * Browser.java
 *
 * Created on May 12, 2007, 12:32 PM
 * 
 * Creates web pages for browsing the collection.
 * 
 */

package com.pugh.sockso.web.action.browse;

import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Playlist;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.templates.web.browse.TPlaylist;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.action.WebAction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.io.IOException;
import java.util.Vector;

public class Playlister extends WebAction {
    
    /**
     *  handles the "browse" command, this sends HTML pages to the user for
     *  browsing through the collection
     * 
     *  @param res the response object
     *  @param args command arguments
     *  @param user the current user
     * 
     *  @throws SQLException
     *  @throws IOException
     *  @throws BadRequestException
     * 
     */
    
    public void handleRequest() throws SQLException, IOException, BadRequestException {

        final Request req = getRequest();
        final String type = req.getUrlParam( 1 );

        if ( type.equals("playlist"))
            playlist();

        else throw new BadRequestException( "unknown browse type '" + type + "'", 400 );

    }

    /**
     *  shows the listing for a playlist
     * 
     *  @param req the request object
     *  @param res the response object
     * 
     *  @throws SQLException
     *  @throws IOException
     *  @throws BadRequestException
     * 
     */
    
    protected void playlist() throws SQLException, IOException, BadRequestException {
        
        final Request req = getRequest();
        final int id = Integer.parseInt( req.getUrlParam(2)  );        
        final Playlist playlist = getPlaylist( id );
        final Vector<Track> tracks = getPlaylistTracks( id );

        showPlaylist( playlist, tracks );
        
    }
    
    /**
     *  shows a playlists with it's tracks
     * 
     *  @param playlist
     *  @param tracks
     * 
     *  @throws java.io.IOException
     * 
     */
    
    protected void showPlaylist( final Playlist playlist, final Vector<Track> tracks ) throws IOException, SQLException {
        
        final TPlaylist tpl = new TPlaylist();
            
        tpl.setTracks( tracks );
        tpl.setPlaylist( playlist );

        getResponse().showHtml( tpl );
       
    }

    /**
     *  returns the tracks for a plsylist
     * 
     *  @param playlistId
     * 
     *  @return
     * 
     */
    
    protected Vector<Track> getPlaylistTracks( final int playlistId ) throws SQLException {

        ResultSet rs = null;
        PreparedStatement st = null;

        try {

            final Database db = getDatabase();
            final String sql = Playlist.getSelectTracksSql( playlistId, "" );
            
            st = db.prepare( sql );
            rs = st.executeQuery();
            
            return Track.createVectorFromResultSet( rs );
            
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
    }
    
    /**
     *  queries the database for a specific playlist, throws a BadRequestException
     *  if the playlist isn't found.
     * 
     *  @param id
     * 
     *  @return
     * 
     *  @throws java.sql.SQLException
     *  @throws com.pugh.sockso.web.BadRequestException
     * 
     */
    
    protected Playlist getPlaylist( final int id ) throws SQLException, BadRequestException {

        ResultSet rs = null;
        PreparedStatement st = null;

        try {

            final Database db = getDatabase();
            final String sql = " select p.id as id, p.name as name " +
                        " from playlists p " +
                        " where p.id = ? " +
                        " limit 1 ";
            st = db.prepare( sql );
            st.setInt( 1, id );
            rs = st.executeQuery();

            if ( !rs.next() )
               throw new BadRequestException( "playlist not found", 404 );

            return new Playlist(
                rs.getInt("id"), rs.getString("name")
            );

        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
    }
    
}
