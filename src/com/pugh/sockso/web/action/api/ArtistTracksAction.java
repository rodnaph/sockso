
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.music.Track;
import com.pugh.sockso.templates.api.TTracks;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.Request;

import java.io.IOException;

import java.sql.SQLException;
import java.util.Vector;

public class ArtistTracksAction extends BaseApiAction {
    
    /**
     *  Indicates if the action can handle the request
     * 
     *  @param req
     * 
     *  @return 
     * 
     */
    
    public boolean canHandle( final Request req ) {
        
        return req.getUrlParam( 1 ).equals( "artists" )
            && isInteger(req.getUrlParam(2) )
            && req.getUrlParam( 3 ).equals( "tracks" );
        
    }
    
    /**
     *  Handle a request to show an artists tracks
     * 
     *  @throws BadRequestException
     *  @throws SQLException
     *  @throws IOException 
     * 
     */
    
    public void handleRequest() throws BadRequestException, SQLException, IOException {
        
        final Vector<Track> tracks = Track.getTracks(
            getDatabase(),
            "ar",
            Integer.parseInt(getRequest().getUrlParam(2))
        );
        
        showTracks( tracks );
        
    }
    
    /**
     *  Show the specified tracks as JSON
     * 
     *  @param tracks
     * 
     *  @throws IOException 
     * 
     */
    
    protected void showTracks( final Vector<Track> tracks ) throws IOException {
        
        final TTracks tpl = new TTracks();
        tpl.setTracks( tracks );
        
        getResponse().showJson( tpl.makeRenderer() );
        
    }
    
}
