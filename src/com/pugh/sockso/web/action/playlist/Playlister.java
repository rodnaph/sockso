
package com.pugh.sockso.web.action.playlist;

import com.pugh.sockso.music.Files;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.Response;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.action.BaseAction;

import com.pugh.sockso.web.TracksRequest;
import java.io.IOException;

import java.sql.SQLException;

import java.util.Vector;

/**
 * Handles creating playlists (eg. M3u, Pls, etc...)
 * 
 * Abstract class for all playlist generators to implement
 * 
 */
public abstract class Playlister extends BaseAction {
    
    protected abstract PlaylistTemplate getPlaylistTemplate();

    private String protocol;
    private String extension;
    
    /**
     *  constructor
     * 
     *  @param db the database connection
     * 
     */
    
    public void init( final String protocol, final String extension ) {

        this.protocol = protocol;
        this.extension = extension;
        
    }

    public void handleRequest() throws IOException, SQLException, BadRequestException {
        
        final Request req = getRequest();
        final String type = req.getUrlParam( 1 );

        if ( type.equals("random") ) {
            createRandomPlaylist();
        }

        else {
            createPlaylist();
        }
        
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
            getTracksRequest().getRandomTracks()
        );

    }
    
    /**
     *  creates a custom playlist based on "TY123" type url
     *  arguments (where TY is the filter type, and 123 the id)
     * 
     *  @param playArgs the url arguments
     * 
     *  @throws SQLException
     *  @throws BadRequestException
     *  @throws IOException
     * 
     */
    
    public void createPlaylist() throws SQLException, BadRequestException, IOException {

        showPlaylist(
            getTracksRequest().getRequestedTracks()
        );

    }
    
    /**
     *  shows a playlist with the specified tracks
     *
     *  @param tracks
     *
     */

    protected void showPlaylist( final Track[] tracks ) throws IOException {

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
        
        res.addHeader( "Content-type", Files.getMimeType(filename) );
        res.addHeader( "Content-Disposition", "inline; filename=\"" +filename+ "\"" );
        res.addHeader( "Expires", "0" );
        res.addHeader( "Cache-Control", "must-revalidate, post-check=0, pre-check=0" );
        res.addHeader( "Pragma", "nocache" ); 

    }

    /**
     *  Creates a new TracksRequest object for querying for tracks
     * 
     *  @return 
     * 
     */
    
    protected TracksRequest getTracksRequest() {

        return new TracksRequest(
            getRequest(),
            getDatabase(),
            getProperties()
        );

    }

}
