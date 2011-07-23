
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.music.Album;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.templates.api.TArtist;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.Request;

import java.io.IOException;

import java.sql.SQLException;

import java.util.Vector;

public class ArtistAction extends BaseApiAction {
    
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
            && req.getUrlParam( 1 ).equals( "artists" )
            && isInteger( req.getUrlParam(2) );
        
    }
    
    /**
     *  Shows info about the artist and its albums
     * 
     *  @throws IOException
     *  @throws SQLException
     *  @throws BadRequestException
     * 
     */
    
    public void handleRequest() throws IOException, SQLException, BadRequestException {
        
        final Artist artist = Artist.find(
            getDatabase(),
            Integer.parseInt( getRequest().getUrlParam(2) )
        );
        
        if ( artist == null ) {
            throw new BadRequestException( "Invalid artist ID", 404 );
        }
        
        final Vector<Album> albums = Album.findByArtistId(
            getDatabase(),
            artist.getId()
        );
        
        showArtist( artist, albums );
        
    }
    
    /**
     *  Shows an artist and its albums as JSON
     * 
     *  @param artist
     *  @param albums
     * 
     *  @throws IOException 
     * 
     */
    
    protected void showArtist( final Artist artist, final Vector<Album> albums ) throws IOException {
        
        final TArtist tpl = new TArtist();
        tpl.setArtist( artist );
        tpl.setAlbums( albums );
        
        getResponse().showJson( tpl.makeRenderer() );
        
    }
    
}
