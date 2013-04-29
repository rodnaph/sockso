
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.music.Artist;
import com.pugh.sockso.templates.api.TArtists;
import com.pugh.sockso.web.Request;

import java.io.IOException;
import java.util.List;

public class ArtistsAction extends BaseApiAction {

    /**
     *  Indicates if the action can handle the request
     * 
     *  @param req
     * 
     *  @return 
     * 
     */

    @Override
    public boolean canHandle( final Request req ) {
        
        return req.getParamCount() == 2
            && req.getUrlParam( 1 ).equals( "artists" );
        
    }

    /**
     *  Shows the requested list of artists
     * 
     *  @throws IOException
     * 
     */
    
    public void handleRequest() throws Exception {

        final List<Artist> artists = Artist.findAll(
            getDatabase(),
            getLimit(),
            getOffset(),
            getFromDate()
        );
        
        showArtists( artists.toArray( new Artist[] {} ) );
        
    }
    
    /**
     *  Shows the specified artists
     * 
     *  @param artists 
     * 
     *   @throws IOException
     * 
     */
    
    protected void showArtists( final Artist[] artists ) throws IOException {
        
        TArtists tpl = new TArtists();
        tpl.setArtists( artists );
        
        getResponse().showJson( tpl.makeRenderer() );
        
    }
    
}
