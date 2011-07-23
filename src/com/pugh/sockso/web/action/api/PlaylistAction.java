
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Playlist;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.templates.api.TPlaylist;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.Request;

import java.io.IOException;

import java.sql.SQLException;

import java.util.Vector;

public class PlaylistAction extends BaseApiAction {

    /**
     *  Shows json info for requested playlist
     * 
     *  @throws IOException
     *  @throws SecurityException
     *  @throws BadRequestException
     * 
     */
    
    public void handleRequest() throws IOException, SQLException, BadRequestException {
        
        final Database db = getDatabase();
        final Playlist playlist = Playlist.find( db, Integer.parseInt( getRequest().getUrlParam(2) ) );
        
        if ( playlist == null ) {
            throw new BadRequestException( "Invalid playlist ID" );
        }
        
        showPlaylist( playlist, playlist.getTracks(db) );
        
    }
    
    /**
     *  Shows a playlist as JSON
     * 
     *  @param playlist
     *  @param tracks 
     * 
     *  @throws IOException 
     * 
     */
    
    protected void showPlaylist( final Playlist playlist, final Vector<Track> tracks ) throws IOException {
        
        TPlaylist tpl = new TPlaylist();
        tpl.setPlaylist( playlist );
        tpl.setTracks( tracks );
        
        getResponse().showJson( tpl.makeRenderer() );
        
    }

    /**
     *  Indicates if the action can handle the request
     * 
     *  @param req
     * 
     *  @return 
     * 
     */
    
    public boolean canHandle( final Request req ) {
        
        return req.getParamCount() == 3
            && req.getUrlParam(1).equals( "playlists" )
            && isInteger( req.getUrlParam(2) );
        
    }
    
}
