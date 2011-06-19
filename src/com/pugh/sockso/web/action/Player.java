
package com.pugh.sockso.web.action;

import com.pugh.sockso.Utils;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.action.playlist.XspfPlayer;
import com.pugh.sockso.templates.web.THtml5Player;
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

        if ( type.equals( "html5" )) {
            showHtml5Player(
                req.getUrlParam( 2 ).equals( "random" )
                    ? getRandomTracks()
                    : getRequestedTracks( playArgs ),
                req.getUrlParam( 2 ).equals( "random" )
            );
        }
        
        // default to XSPF player
        else {
        
            String extraArgs = "";

            if ( req.hasArgument("orderBy") ) {
                extraArgs += "&orderBy=" +req.getArgument("orderBy");
            }
            
            if ( req.hasArgument("path") ) {
                extraArgs += "&path=" + Utils.URLEncode(req.getArgument("path") );
            }

            showXspfPlayer( getXspfPlayer(), extraArgs, playArgs );

        }

    }

    /**
     *  shows the HTML 5 player
     * 
     *  @param tracks
     *  @param random
     * 
     *  @throws IOException
     * 
     */
    
    protected void showHtml5Player( final Vector<Track> tracks, boolean random ) throws IOException {

        final THtml5Player tpl = new THtml5Player();
        
        tpl.setTracks( tracks );
        tpl.setProperties( getProperties() );
        tpl.setRandom( random );
        
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
