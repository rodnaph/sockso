/*
 * Player.java
 * 
 * Created on Aug 8, 2007, 9:11:55 PM
 * 
 * Shows an embedded play with some music.
 * 
 */

package com.pugh.sockso.web.action;

import com.pugh.sockso.music.Track;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.action.playlist.XspfPlayer;
import com.pugh.sockso.templates.web.TJsPlayer;
import com.pugh.sockso.templates.web.TXspfPlayer;
import com.pugh.sockso.templates.web.TFlexPlayer;

import java.sql.SQLException;

import java.io.IOException;

import java.util.Vector;

public class Player extends WebAction {
    
    /**
     *  handles the request for one a player window
     * 
     *  @param res the response object
     *  @param args command arguments
     * 
     *  @throws IOException
     * 
     */
    
    public void handleRequest() throws IOException, SQLException, BadRequestException {
       
        final Request req = getRequest();
        final String[] playArgs = req.getPlayParams( true );
        final String type = req.getUrlParam( 1 );

        // use the JS player
        if ( type.equals("js") ) {
            showJsPlayer(
                req.getUrlParam( 2 ).equals( "random" )
                    ? getRandomTracks()
                    : Track.getTracksFromPlayArgs( getDatabase(), playArgs )
            );
        }
        
        // default to XSPF player
        else {
        
            String extraArgs = "";

            if ( req.hasArgument("orderBy") )
                extraArgs += "&orderBy=" +req.getArgument("orderBy");

            showXspfPlayer( getXspfPlayer(), extraArgs, playArgs );

        }

    }

    /**
     *  shows the jsplayer
     * 
     *  @param playArgs
     * 
     *  @throws IOException
     *  @throws SQLException
     *  @throws BadRequestException
     * 
     */
    
    protected void showJsPlayer( final Vector<Track> tracks ) throws IOException, SQLException, BadRequestException {

        final TJsPlayer tpl = new TJsPlayer();
        
        tpl.setTracks( tracks );
        tpl.setProperties( getProperties() );
        
        getResponse().showHtml( tpl.makeRenderer() );

    }
    
    /**
     *  shows the page with the xspf player on it
     * 
     *  @param tpl
     *  @param extraArgs
     *  @param playArgs
     * 
     *  @throws java.io.IOException
     * 
     */
    
    protected void showXspfPlayer( final XspfPlayer tpl, final String extraArgs, final String[] playArgs ) throws IOException {
        
        tpl.setProperties( getProperties() );
        tpl.setExtraArgs( extraArgs );
        tpl.setPlayArgs( playArgs );
        
        getResponse().showHtml( tpl.makeRenderer() );
        
    }

    /**
     *  returns the xspf player to use (determined by properties)
     * 
     *  @return
     * 
     */
    
    protected XspfPlayer getXspfPlayer() {
        
        final Request req = getRequest();
        final String player = req.getArgument( "player" );
        
        // use flex player?
        if ( player.equals("flexPlayer") )
            return new TFlexPlayer();
        
        return new TXspfPlayer();
        
    }
    
}
