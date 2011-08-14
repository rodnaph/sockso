
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.RelatedArtists;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.templates.api.TArtists;

import com.google.inject.Inject;

import java.io.IOException;

import java.sql.SQLException;

public class ArtistRelatedAction extends BaseApiAction {

    private final RelatedArtists relatedArtists;

    @Inject
    public ArtistRelatedAction( final RelatedArtists relatedArtists ) {
        
        this.relatedArtists = relatedArtists;

    }

    /**
     *  Shows artists related to the specified one
     *
     */

    public void handleRequest() throws BadRequestException, SQLException, IOException {

        final int artistId = Integer.parseInt( getRequest().getUrlParam(2) );

        TArtists tpl = new TArtists();
        tpl.setArtists( relatedArtists.getRelatedArtistsFor(artistId) );

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
    
    public boolean canHandle( final Request req ) {
        
        return req.getUrlParam( 1 ).equals( "artists" )
            && isInteger( req.getUrlParam(2) )
            && req.getUrlParam( 3 ).equals( "related" );
        
    }
    
}
