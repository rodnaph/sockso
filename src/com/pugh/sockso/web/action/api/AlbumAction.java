
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.music.Album;
import com.pugh.sockso.templates.api.TAlbum;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.Request;

import java.io.IOException;

import java.sql.SQLException;

public class AlbumAction extends BaseApiAction {

    /**
     *  Shows a requested album by ID
     * 
     *  @throws BadRequestException
     *  @throws IOException
     *  @throws SQLException 
     * 
     */
    
    public void handleRequest() throws BadRequestException, IOException, SQLException {

        final Album album = Album.find(
            getDatabase(),
            Integer.parseInt(getRequest().getUrlParam(2))
        );
        
        if ( album == null ) {
            throw new BadRequestException( "Invalid album id" );
        }
        
        showAlbum( album );
        
    }
    
    /**
     *  Write album template to response
     * 
     *  @param album
     * 
     *  @throws IOException 
     * 
     */
    
    protected void showAlbum( final Album album ) throws IOException {
        
        TAlbum tpl = new TAlbum();
        tpl.setAlbum( album );
        
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
            && req.getUrlParam(1).equals( "albums" )
            && isInteger( req.getUrlParam(2) );
        
    }

}
