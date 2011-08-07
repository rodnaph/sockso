
package com.pugh.sockso.web;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;

import com.pugh.sockso.auth.DBAuthenticator;

import com.pugh.sockso.web.action.AdminAction;
import com.pugh.sockso.web.action.Api;
import com.pugh.sockso.web.action.Downloader;
import com.pugh.sockso.web.action.Feeder;
import com.pugh.sockso.web.action.FileServer;
import com.pugh.sockso.web.action.Homer;
import com.pugh.sockso.web.action.Jsoner;
import com.pugh.sockso.web.action.Nater;
import com.pugh.sockso.web.action.Player;
import com.pugh.sockso.web.action.Sharer;
import com.pugh.sockso.web.action.Streamer;
import com.pugh.sockso.web.action.Uploader;
import com.pugh.sockso.web.action.Userer;
import com.pugh.sockso.web.action.BaseAction;

import com.pugh.sockso.web.action.admin.Console;

import com.pugh.sockso.web.action.browse.Albumer;
import com.pugh.sockso.web.action.browse.Artister;
import com.pugh.sockso.web.action.browse.ByLetterer;
import com.pugh.sockso.web.action.browse.Folderer;
import com.pugh.sockso.web.action.browse.Latester;
import com.pugh.sockso.web.action.browse.Playlister;
import com.pugh.sockso.web.action.browse.Playlistser;
import com.pugh.sockso.web.action.browse.Popularer;

import com.pugh.sockso.web.action.playlist.M3uer;
import com.pugh.sockso.web.action.playlist.Plser;
import com.pugh.sockso.web.action.playlist.Xspfer;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 *  looks at the request to determine which web action to invoke
 *
 */

@Singleton
public class Dispatcher {

    private final Properties p;
    private final Injector injector;
    
    private String protocol;
    private int port;
    
    @Inject
    public Dispatcher( final Injector injector, final Properties p ) {

        this.injector = injector;
        this.p = p;
        
    }
    
    /**
     *  Initialise the dispatcher with non-injectables
     * 
     *  @param protocol
     *  @param port 
     * 
     */
    
    public void init( final String protocol, final int port ) {
        
        this.protocol = protocol;
        this.port = port;
        
    }
    
    /**
     *  action =s the web action specified by the request
     * 
     *  @param req
     * 
     *  @action =
     * 
     */
    
    public BaseAction getAction( final Request req ) {

        final String command = req.getUrlParam( 0 );
        final String host = getHost();

        BaseAction action = null;
        
        if ( command.equals("file") )
            action = injector.getInstance( FileServer.class );
        
        else if ( command.equals("browse") )
            action = getBrowseAction( req );
        
        else if ( command.equals("") )
            action = injector.getInstance( Homer.class );
        
        else if ( command.equals("xspf") ) {
            Xspfer xspf = injector.getInstance( Xspfer.class );
            xspf.init( protocol );
            action = xspf;
        }
                
        else if ( command.equals("m3u") ) {
            M3uer m3u = injector.getInstance( M3uer.class );
            m3u.init( protocol );
            action = m3u;
        }
                
        else if ( command.equals("pls") ) {
            Plser pls = injector.getInstance( Plser.class );
            pls.init( protocol );
            action = pls;
        }
        
        else if ( command.equals("stream") ) {
            action = injector.getInstance( Streamer.class );
        }
        
        else if ( command.equals("api") ) {
            action = injector.getInstance( Api.class );
        }

        else if ( command.equals("json") )
            action = injector.getInstance( Jsoner.class );
        
        else if ( command.equals("user") ) {
            final Userer u = injector.getInstance( Userer.class );
            u.addAuthenticator( injector.getInstance(DBAuthenticator.class) );
            action = u;
        }
        
        else if ( command.equals("player") )
            action = injector.getInstance( Player.class );
        
        else if ( command.equals("download") )
            action = injector.getInstance( Downloader.class );
        
        else if ( command.equals("upload") )
            action = injector.getInstance( Uploader.class );
        
        else if ( command.equals("share") )
            action = injector.getInstance( Sharer.class );
        
        else if ( command.equals("rss") ) {
            final Feeder feeder = injector.getInstance( Feeder.class );
            feeder.init( host );
            action = feeder;
        }

        else if ( command.equals("admin") ) {
            action = getAdminAction( req );
        }
        
        else if ( command.equals("nat") ) {
            action = injector.getInstance( Nater.class );
        }
                
        return action;
        
    }
    
    /**
     *  action =s the handler for a browse action
     * 
     *  @param req
     * 
     *  @action =
     * 
     */
    
    protected BaseAction getBrowseAction( final Request req ) {
        
        final String command = req.getUrlParam( 1 );
        
        if ( command.equals("folders") )
            return injector.getInstance( Folderer.class );
        
        else if ( command.equals("popular") )
            return injector.getInstance( Popularer.class );
        
        else if ( command.equals("latest") )
            return injector.getInstance( Latester.class );
        
        else if ( command.equals("letter") )
            return injector.getInstance( ByLetterer.class );

        else if ( command.equals("artist") )
            return injector.getInstance( Artister.class );

        else if ( command.equals("album"))
            return injector.getInstance( Albumer.class );

        else if ( command.equals("playlists"))
            return injector.getInstance( Playlistser.class );

        else if ( command.equals("playlist"))
            return injector.getInstance( Playlister.class );

        else return null;
        
    }

    /**
     *  Returns an action from the admin namespace
     *
     *  @param req
     *
     *  @return
     *
     */
    
    protected AdminAction getAdminAction( final Request req ) {

        final String command = req.getUrlParam( 1 );

        if ( command.equals("console") ) {
            return injector.getInstance( Console.class  );
        }

        return null;

    }

    /**
     * action =s the ip address the server is bound to and the port that we're
     * listening on
     *
     * @action = ip:port combo
     *
     */

    public String getHost() {

        return p.get( Constants.SERVER_HOST ) + ":" + port;

    }


}
