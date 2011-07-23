
package com.pugh.sockso.web.action.browse;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Playlist;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.templates.web.browse.TPlaylist;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.action.BaseAction;

import java.sql.SQLException;

import java.io.IOException;

import java.util.Vector;

public class Playlister extends BaseAction {
    
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
        final Database db = getDatabase();
        final int id = Integer.parseInt( req.getUrlParam(2)  );        
        final Playlist playlist = Playlist.find( db, id );
        
        if ( playlist == null ) {
            throw new BadRequestException( "Invalid playlist ID", 404 );
        }
        
        final Vector<Track> tracks = playlist.getTracks( db );

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
    
}
