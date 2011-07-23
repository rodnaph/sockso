
package com.pugh.sockso.web;

import com.pugh.sockso.ObjectCache;
import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.auth.DBAuthenticator;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.CollectionManager;
import com.pugh.sockso.resources.Resources;

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

/**
 *  looks at the request to determine which web action to invoke
 *
 */

public class Dispatcher {

    private final String protocol;
    private final int port;
    private final Properties p;
    private final Resources r;
    private final CollectionManager cm;
    private final Database db;
    private final ObjectCache cache;
    
    public Dispatcher( final String protocol, final int port, final Properties p,
                       final Resources r, final CollectionManager cm, final Database db,
                       final ObjectCache cache ) {

        this.protocol = protocol;
        this.port = port;
        this.p = p;
        this.r = r;
        this.cm = cm;
        this.db = db;
        this.cache = cache;
        
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
            action = new FileServer( r );
        
        else if ( command.equals("browse") )
            action = getBrowseAction( req );
        
        else if ( command.equals("") )
            action = new Homer();
        
        else if ( command.equals("xspf") )
            action = new Xspfer( protocol );
        else if ( command.equals("m3u") )
            action = new M3uer( protocol );
        else if ( command.equals("pls") )
            action = new Plser( protocol );
        
        else if ( command.equals("stream") ) {
            action = new Streamer();
        }
        
        else if ( command.equals("api") ) {
            action = new Api();
        }

        else if ( command.equals("json") )
            action = new Jsoner( cm, cache );
        
        else if ( command.equals("user") ) {
            final Userer u = new Userer();
            u.addAuthenticator( new DBAuthenticator(db) );
            action = u;
        }
        
        else if ( command.equals("player") )
            action = new Player();
        
        else if ( command.equals("download") )
            action = new Downloader();
        
        else if ( command.equals("upload") )
            action = new Uploader( cm );
        
        else if ( command.equals("share") )
            action = new Sharer();
        
        else if ( command.equals("rss") )
            action = new Feeder( host );

        else if ( command.equals("admin") ) {
            action = getAdminAction( req );
        }
        
        else if ( command.equals("nat") ) {
            action = new Nater();
        }
                
        if ( action != null ) {
            action.setDatabase( db );
            action.setProperties( p );
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
            return new Folderer();
        
        else if ( command.equals("popular") )
            return new Popularer();
        
        else if ( command.equals("latest") )
            return new Latester();
        
        else if ( command.equals("letter") )
            return new ByLetterer();

        else if ( command.equals("artist") )
            return new Artister();

        else if ( command.equals("album"))
            return new Albumer();

        else if ( command.equals("playlists"))
            return new Playlistser();

        else if ( command.equals("playlist"))
            return new Playlister();

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
            return new Console( cm );
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
