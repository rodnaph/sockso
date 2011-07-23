
package com.pugh.sockso.web.action;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.templates.api.TException;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.action.api.AlbumAction;
import com.pugh.sockso.web.action.api.AlbumTracksAction;
import com.pugh.sockso.web.action.api.AlbumsAction;
import com.pugh.sockso.web.action.api.ApiAction;
import com.pugh.sockso.web.action.api.ArtistAction;
import com.pugh.sockso.web.action.api.ArtistTracksAction;
import com.pugh.sockso.web.action.api.ArtistsAction;
import com.pugh.sockso.web.action.api.PlaylistAction;
import com.pugh.sockso.web.action.api.PlaylistsAction;
import com.pugh.sockso.web.action.api.RootAction;
import com.pugh.sockso.web.action.api.SessionAction;
import com.pugh.sockso.web.action.api.TrackAction;
import com.pugh.sockso.web.action.api.TracksAction;

import org.apache.log4j.Logger;

import java.io.IOException;

import java.sql.SQLException;

/**
 *  End point for Sockso Web API methods.
 * 
 */
public class Api extends BaseAction {

    private static final Logger log = Logger.getLogger( Api.class );

    /**
     *  Run through API actions looking for one to handle the request
     *
     *  @throws IOException
     * 
     */

    @Override
    public void handleRequest() throws IOException {

        try {
            processActions( getApiActions() );
        }
        
        catch ( final BadRequestException e ) {
            
            TException tpl = new TException();
            tpl.setMessage( e.getMessage() );
            
            getResponse().setStatus( e.getStatusCode() );
            getResponse().showJson( tpl.makeRenderer() );
            
        }
        
    }
    
    /**
     *  This action does not require a login, but it controls logged in/out control
     *  for its sub actions.
     * 
     *  @return 
     * 
     */
    
    @Override
    public boolean requiresLogin() {
        
        return false;
        
    }
    
    /**
     *  Processes the specified API actions until one handles the request
     * 
     *  @param actions 
     * 
     */
    
    protected void processActions( final ApiAction[] actions ) throws BadRequestException {
        
        final Request req = getRequest();
        
        for ( final ApiAction action : actions ) {

            if ( action.canHandle(req) && loginStatusOk(action) ) {
                
                log.debug( "Run API action: " +action.getClass().getName() );
            
                action.init( getRequest(), getResponse(), getUser(), getLocale() );
                action.setDatabase( getDatabase() );
                action.setProperties( getProperties() );
                
                try {
                    action.handleRequest();
                }
                
                catch ( final Exception e ) {
                    log.debug( "API Exception: " +e.getMessage() );
                    throw new BadRequestException( e.getMessage() );
                }
                
                return;
                
            }

        }

        throw new BadRequestException( "Unknown API Action" );

    }

    /**
     *  Indicates if the currently logged in status of the user is ok for
     *  running this action.
     * 
     *  @param action
     * 
     *  @return
     * 
     */
    
    private boolean loginStatusOk( final ApiAction action ) {

        if ( getProperties().get(Constants.WWW_USERS_REQUIRE_LOGIN).equals(Properties.YES) ) {
            return action.requiresLogin() && getUser() == null
                ? false
                : true;
        }

        return true;

    }

    /**
     *  Returns an array of all the api actions
     * 
     *  @return
     *
     */
    protected ApiAction[] getApiActions() {

        return new ApiAction[] {
            
            new RootAction(),
            new SessionAction(),
            
            // playlists
            
            new PlaylistAction(),
            new PlaylistsAction(),
            
            // tracks
            
            new TrackAction(),
            new TracksAction(),
            
            // artists
            
            new ArtistTracksAction(),
            new ArtistAction(),
            new ArtistsAction(),
            
            // albums
            
            new AlbumAction(),
            new AlbumsAction(),
            new AlbumTracksAction()

        };

    }

}
