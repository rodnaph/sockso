/*
 * Playlister.java
 * 
 * Created on Jun 18, 2007, 11:55:15 PM
 * 
 * Abstract class for all playlist generators to implement
 * 
 */

package com.pugh.sockso.web.action.playlist;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.Response;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.action.WebAction;
import com.pugh.sockso.web.action.FileServer;
import com.pugh.sockso.music.Track;

import java.io.IOException;

import java.sql.SQLException;

import java.util.Vector;

public abstract class Playlister extends WebAction {
    
    protected abstract PlaylistTemplate getPlaylistTemplate();

    private final String protocol;
    private final String extension;
    
    /**
     *  constructor
     * 
     *  @param db the database connection
     * 
     */
    
    protected Playlister( final String protocol, final String extension ) {

        this.protocol = protocol;
        this.extension = extension;
        
    }

    public void handleRequest() throws IOException, SQLException, BadRequestException {
        
        final Request req = getRequest();
        final String type = req.getUrlParam( 1 );

        if ( type.equals("random") )
            createRandomPlaylist();

        else createPlaylist(
            req.getPlayParams( false )
        );
        
    }

    /**
     *  creates a playlist to play a random stream (just one track that links
     *  to a stream that keeps fetching more info)
     * 
     *  @param req the request object
     *  @param res the response object
     * 
     *  @throws IOException
     * 
     */
    
    public void createRandomPlaylist() throws IOException, SQLException {

        showPlaylist(
            getRandomTracks()
        );

    }
    
    /**
     *  creates a custom playlist based on "TY123" type url
     *  arguments (where TY is the filter type, and 123 the id)
     * 
     *  @param req the request object
     *  @param res the response object
     *  @param args the url arguments
     * 
     *  @throws SQLException
     *  @throws BadRequestException
     *  @throws IOException
     * 
     */
    
    public void createPlaylist( final String[] args ) throws SQLException, BadRequestException, IOException {
        
        final Database db = getDatabase();
        final Request req = getRequest();
        final String orderBySql = req.getArgument("orderBy").equals("random")
                ? " order by rand() "
                : "";
        final Vector<Track> tracks = Track.getTracksFromPlayArgs( db, args, orderBySql );

        showPlaylist( tracks );

    }

    /**
     *  shows a playlist with the specified tracks
     *
     *  @param tracks
     *
     */

    protected void showPlaylist( final Vector<Track> tracks ) throws IOException {

        sendHeaders();

        final PlaylistTemplate tpl = getPlaylistTemplate();

        tpl.setProperties( getProperties() );
        tpl.setUser( getUser() );
        tpl.setRequest( getRequest() );
        tpl.setTracks( tracks );
        tpl.setProtocol( protocol );

        getResponse().showTemplate( tpl.makeRenderer() );

    }
    
    /**
     *  sends the HTTP headers needed before sending this playlists to the client
     * 
     */
    
    private void sendHeaders() {

        final Response res = getResponse();
        final String filename = "playlist." +extension;
        
        res.addHeader( "Content-type", FileServer.getMimeType(filename) );
        res.addHeader( "Content-Disposition", "inline; filename=\"" +filename+ "\"" );
        res.addHeader( "Expires", "0" );
        res.addHeader( "Cache-Control", "must-revalidate, post-check=0, pre-check=0" );
        res.addHeader( "Pragma", "nocache" ); 

    }

}
