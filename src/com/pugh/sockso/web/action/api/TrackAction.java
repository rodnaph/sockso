
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.music.Track;
import com.pugh.sockso.templates.api.TTrack;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.Request;

import java.io.IOException;

import java.sql.SQLException;

public class TrackAction extends BaseApiAction {
    
    /**
     *  Indicates if this action can handle the request
     * 
     *  @param req
     * 
     *  @return 
     * 
     */
    
    public boolean canHandle( final Request req ) {
       
        return req.getUrlParam( 1 ).equals( "tracks" )
            && isInteger( req.getUrlParam(2) );
       
    }
   
    /**
     *  Show the track info, or throw an exception if not found
     * 
     *  @throws SQLException
     *  @throws BadRequestException 
     * 
     */
    
    public void handleRequest() throws SQLException, BadRequestException, IOException {
       
        final Track track = Track.find(
            getDatabase(),
            Integer.parseInt( getRequest().getUrlParam(2) )
        );

        if ( track == null ) {
            throw new BadRequestException( "Track not found", 404 );
        }
        
        showTrack( track );
        
    }
    
    /**
     *  Show a track as JSON
     * 
     *  @param track 
     * 
     *  @throws IOException
     * 
     */
    
    protected void showTrack( final Track track ) throws IOException {
        
        final TTrack tpl = new TTrack();
        tpl.setTrack( track );
        
        getResponse().showJson( tpl.makeRenderer() );
        
    }
    
}
