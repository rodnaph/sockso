
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.music.Playlist;
import com.pugh.sockso.templates.api.TPlaylists;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class PlaylistsAction extends BaseApiAction {

    /**
     *  Indicates if this action can handle the request
     * 
     *  @param req
     * 
     *  @return 
     * 
     */
    
    @Override
    public boolean canHandle( final Request req ) {
        
        return (
            req.getParamCount() == 2
            && req.getUrlParam( 1 ).equals( "playlists" )
        )
            ||
        (
            req.getParamCount() == 3
            && ( req.getUrlParam(2).equals("site") || req.getUrlParam(2).equals("user") )
        )
            ||
        (
            req.getParamCount() == 4
            && req.getUrlParam( 1 ).equals( "playlists" )
            && req.getUrlParam( 2 ).equals( "user" )
            && isInteger(req.getUrlParam(3) )
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

    @Override
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
                getPlaylistUser(),
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
     *  Returns the user to get playlists for.  This could be the current user
     *  or the user specified in the URL
     * 
     *  @return 
     * 
     */
    
    protected User getPlaylistUser() {
        
        final Request req = getRequest();
        
        return req.getParamCount() == 4
            ? User.find( getDatabase(), Integer.parseInt(req.getUrlParam(3)) )
            : getUser();

    }
    
    /**
     *  Shows the playlists as JSON
     * 
     *  @param playlists
     * 
     *  @throws IOException 
     * 
     */
    
    protected void showPlaylists( final List<Playlist> playlists ) throws IOException {
        
        final TPlaylists tpl = new TPlaylists();
        tpl.setPlaylists( playlists );

        getResponse().showJson( tpl.makeRenderer() );
        
    }

}
