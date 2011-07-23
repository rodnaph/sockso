
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.music.Album;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.templates.api.TTracks;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.Request;

import java.io.IOException;

import java.sql.SQLException;

import java.util.Vector;

public class AlbumTracksAction extends BaseApiAction {
    
    /**
     *  Indicates if this action can handle this request
     * 
     *  @param req
     * 
     *  @return 
     * 
     */
    
    public boolean canHandle( final Request req ) {
        
        return req.getUrlParam( 1 ).equals( "albums" )
                && isInteger(req.getUrlParam(2) )
                && req.getUrlParam( 3 ).equals( "tracks" );
        
    }
    
    /**
     *  Handles request to list albums tracks
     * 
     *  @throws SQLException
     *  @throws BadRequestException
     *  @throws IOException 
     * 
     */
    
    public void handleRequest() throws SQLException, BadRequestException, IOException {
        
        final int albumId = Integer.parseInt( getRequest().getUrlParam(2) );
        final Album album = Album.find( getDatabase(), albumId );
        
        if ( album == null ) {
            throw new BadRequestException( "Invalid album id" );
        }
        
        final Vector<Track> tracks = Track.getTracks( getDatabase(), "al", albumId );
        
        showTracks( tracks );
        
    }
    
    /**
     *  Shows the specified tracks as JSON
     * 
     *  @param tracks
     * 
     *  @throws IOException 
     * 
     */
    
    protected void showTracks( final Vector<Track> tracks ) throws IOException {
        
        TTracks tpl = new TTracks();
        tpl.setTracks( tracks );
        
        getResponse().showJson( tpl.makeRenderer() );
        
    }
    
}
