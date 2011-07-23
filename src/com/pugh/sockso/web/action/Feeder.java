/*
 * Feeder.java
 * 
 * Created on Jun 16, 2007, 1:00:54 PM
 * 
 * Outputs RSS feeds
 * 
 */

package com.pugh.sockso.web.action;

import com.pugh.sockso.music.Track;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.Server;
import com.pugh.sockso.templates.TRSSLatest;

import java.sql.SQLException;

import java.io.IOException;

import java.util.Vector;

public class Feeder extends BaseAction {

    private final String host;
    
    /**
     *  constructor.
     * 
     *  @param db the database connection
     * 
     */
    
    public Feeder( final String host ) {

        this.host = host;

    }

    /**
     *  handles the rss command which generates rss feeds
     * 
     *  @param res the response object
     *  @param args the command arguments
     * 
     *  @throws SQLException
     *  @throws IOException
     *  @throws BadRequestException
     * 
     */
    
    public void handleRequest() throws SQLException, IOException, BadRequestException {
        
        final Request req = getRequest();
        final String type = req.getUrlParam( 1 );
        
        if ( type.equals("latest") )
            latest( getLatestTracks(20) );
        
        else throw new BadRequestException( "Unknown feed type (" + type + ")", 400 );
        
    }

    /**
     *  outputs the latest tracks added to the collection as an rss feed
     * 
     *  @param res the response object
     * 
     *  @throws SQLException
     *  @throws IOException
     * 
     */
    
    public void latest( final Vector<Track> tracks ) throws SQLException, IOException {
        
        final TRSSLatest tpl = new TRSSLatest();
        
        tpl.setProperties( getProperties() );
        tpl.setHost( host );
        tpl.setTracks( tracks );
        
        getResponse().showRss( tpl.makeRenderer() );
                    
    }

}
