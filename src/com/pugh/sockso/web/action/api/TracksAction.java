
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.music.Track;
import com.pugh.sockso.templates.api.TTracks;
import com.pugh.sockso.web.Request;

import java.io.IOException;
import java.util.List;

public class TracksAction extends BaseApiAction {

    /**
     *  Shows tracks as JSON
     * 
     *  @throws Exception 
     * 
     */
    
    public void handleRequest() throws Exception {

        List<Track> tracks = Track.findAll(
            getDatabase(),
            getLimit(),
            getOffset(),
            getFromDate()
        );
        
        showTracks( tracks );
        
    }
    
    /**
     *  Renders tracks to response
     * 
     *  @param tracks
     * 
     *  @throws IOException 
     * 
     */
    
    protected void showTracks( final List<Track> tracks ) throws IOException {
        
        TTracks tpl = new TTracks();
        tpl.setTracks( tracks );
        
        getResponse().showJson( tpl.makeRenderer() );
        
    }

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
        
        return req.getParamCount() == 2
            && req.getUrlParam( 1 ).equals( "tracks" );
        
    }
    
}
