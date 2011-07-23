
package com.pugh.sockso.web;

import com.pugh.sockso.Constants;
import com.pugh.sockso.StringProperties;

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

import java.util.Hashtable;

import junit.framework.TestCase;

import static org.easymock.EasyMock.*;

public class DispatcherTest extends TestCase {

    public void testConstructor() {

        final Dispatcher d = new Dispatcher( "http", 4444, null, null, null, null, null );
        
        assertNotNull( d );
        
    }

    public void testActionNotFound() {
        
        final Request req = createMock( Request.class );
        expect( req.getUrlParam(0) ).andReturn( "--DOES-NOT-EXIST--" );
        replay( req );

        final Dispatcher d = new Dispatcher( "http", 4444, new StringProperties(), null, null, null, null );
        final BaseAction a = d.getAction( req );
        
        assertNull( a );
        
        verify( req );
        
    }
    
    public void testGetWebAction() {
        
        final Hashtable<String,Class> dt = new Hashtable<String,Class>();
        
        dt.put( "/file/some/thing", FileServer.class );
        dt.put( "/xspf/some/thing", Xspfer.class );
        dt.put( "/m3u/some/thing", M3uer.class );
        dt.put( "/pls/some/thing", Plser.class );
        dt.put( "/stream/123", Streamer.class );
        dt.put( "/json/folders", Jsoner.class );
        dt.put( "/user/login", Userer.class );
        dt.put( "/player/xspf", Player.class );
        dt.put( "/download/tr123", Downloader.class );
        dt.put( "/upload/do", Uploader.class );
        dt.put( "/share/me", Sharer.class );
        dt.put( "/rss/latest", Feeder.class );
        dt.put( "/nat/ip", Nater.class );
        dt.put( "/", Homer.class );

        dt.put( "/browse/popular", Popularer.class );
        dt.put( "/browse/folders", Folderer.class );
        dt.put( "/browse/album/123", Albumer.class );
        dt.put( "/browse/artist/345", Artister.class );
        dt.put( "/browse/popular", Popularer.class );
        dt.put( "/browse/letter/a", ByLetterer.class );
        dt.put( "/browse/latest", Latester.class );
        dt.put( "/browse/playlists", Playlistser.class );
        dt.put( "/browse/playlist/123", Playlister.class );
        dt.put( "/admin/console", Console.class );
        dt.put( "/admin/console/send", Console.class );

        for ( final String url : dt.keySet() ) {
            dispatch( url, dt.get(url) );
        }
        
    }

    private void dispatch( final String url, final Class handler ) {
        
        final String[] parts = url.split( "/" );
        
        final Request req = createMock( Request.class );
        expect( req.getUrlParam(0) ).andReturn( parts.length > 0 ? parts[1] : "" );
        if ( parts.length > 1 )
            expect( req.getUrlParam(1) ).andReturn( parts[2] );
        replay( req );
        
        final Dispatcher d = new Dispatcher( "http", 4444, new StringProperties(), null, null, null, null );
        
        assertEquals( handler, d.getAction( req ).getClass() );

    }

    public void testGettingHost() {
        final StringProperties p = new StringProperties();
        final String host = "some.host.com";
        final int port = 1234;
        p.set( Constants.SERVER_HOST, host );
        final Dispatcher d = new Dispatcher( "http", port, p, null, null, null, null );
        assertEquals( host + ":" + port, d.getHost() );
    }
    
}
