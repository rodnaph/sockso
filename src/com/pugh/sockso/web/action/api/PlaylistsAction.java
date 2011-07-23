
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.music.Playlist;
import com.pugh.sockso.templates.api.TPlaylists;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.Request;

import java.io.IOException;

import java.sql.SQLException;

import java.util.Vector;

public class PlaylistsAction extends BaseApiAction {

    /**
     *  Indicates if this action can handle the request
     * 
     *  @param req
     * 
     *  @return 
     * 
     */
    
    public boolean canHandle( final Request req ) {
        
        return (
            req.getParamCount() == 2
            && req.getUrlParam( 1 ).equals( "playlists" )
        )
            ||
        (
            req.getParamCount() == 3
            && ( req.getUrlParam(2).equals("site") || req.getUrlParam(2).equals("user") )
        );

    }
    
    /**
     *  Shows a list of playlists
     * 
     *  @throws SQLException
     *  @throws IOException
     *  @throws BadRequestException 
     * 
     */
    
    public void handleRequest() throws SQLException, IOException, BadRequestException {
        
        final Request req = getRequest();

        if ( req.getUrlParam(2).equals("site") ) {
            showPlaylists(Playlist.findAllForSite(
                getDatabase(),
                getLimit(),
                getOffset()
            ));
        }

        else if ( req.getUrlParam(2).equals("user") ) {
            showPlaylists(Playlist.findAllForUser(
                getDatabase(),
                getUser(),
                getLimit(),
                getOffset()
            ));
        }

        else {
            showPlaylists(Playlist.findAll(
                getDatabase(),
                getLimit(),
                getOffset()
            ));
        }
        
    }
    
    /**
     *  Shows the playlists as JSON
     * 
     *  @param playlists
     * 
     *  @throws IOException 
     * 
     */
    
    protected void showPlaylists( final Vector<Playlist> playlists ) throws IOException {
        
        final TPlaylists tpl = new TPlaylists();
        tpl.setPlaylists( playlists );

        getResponse().showJson( tpl.makeRenderer() );
        
    }

}
