
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.music.Album;
import com.pugh.sockso.templates.api.TAlbums;
import com.pugh.sockso.web.Request;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.text.ParseException;

public class AlbumsAction extends BaseApiAction {

    /**
     *  Shows albums as json
     * 
     *  @throws SQLException
     *  @throws IOException 
     * 
     */
    
    public void handleRequest() throws Exception {
    
        List<Album> albums = Album.findAll(
            getDatabase(),
            getLimit(),
            getOffset(), 
            getFromDate()
        );
        
        showAlbums( albums );
        
    }

    /**
     *  Shows the albums as json
     * 
     *  @param albums
     * 
     *  @throws IOException 
     * 
     */
    
    protected void showAlbums( final List<Album> albums ) throws IOException {
        
        TAlbums tpl = new TAlbums();
        tpl.setAlbums( albums );
        
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
    @Override
    public boolean canHandle( final Request req ) {
        
        return req.getParamCount() == 2
            && req.getUrlParam( 1 ).equals( "albums" );

    }

}
