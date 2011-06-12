
package com.pugh.sockso.web.action.playlist;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.Utils;
import com.pugh.sockso.templates.TXspf;
import com.pugh.sockso.templates.TPls;
import com.pugh.sockso.templates.TM3u;
import com.pugh.sockso.tests.PlaylistTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestRequest;
import com.pugh.sockso.tests.TestResponse;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.User;

public class PlaylisterTest extends PlaylistTestCase {

    private Playlister pl;
    private TestResponse res;
    private Properties p;
    
    @Override
    protected void setUp() throws Exception {
        
        TestDatabase db = new TestDatabase();
        
        p = new StringProperties();
        res = new TestResponse();
        
        db.fixture( "artistsAlbumsAndTracks" );
        
        pl = new Xspfer( "xspf" );
        pl.setDatabase( db );
        pl.setResponse( res );
        pl.setProperties( p );
        

    }
    
    public void testRenderPlaylists() throws Exception {
        
        final Properties p = new StringProperties();
        final String name = Utils.getRandomString( 20 );
        final String email = Utils.getRandomString( 20 );
        final int sessionId = 23123;
        final String sessionCode = Utils.getRandomString( 20 );
        final User user = new User( 1, name, "", email, sessionId, sessionCode, true );

        String data = "";

        final Class[] classes = new Class[] {
            TXspf.class,
            TPls.class,
            TM3u.class
        };

        for ( final Class tplClass : classes ) {

            // stream requires login

            p.set( Constants.STREAM_REQUIRE_LOGIN, Properties.YES );
            p.set( Constants.WWW_USERS_REQUIRE_LOGIN, Properties.YES );

            data = renderPlaylist( tplClass, p, user );

            assertTrue( data.contains(sessionCode) );
            assertTrue( data.contains(Integer.toString(sessionId)) );

            // no login required

            p.set( Constants.STREAM_REQUIRE_LOGIN, Properties.YES );
            p.set( Constants.WWW_USERS_REQUIRE_LOGIN, Properties.NO );

            data = renderPlaylist( tplClass, p, user );

            assertTrue( !data.contains(sessionCode) );
            assertTrue( !data.contains(Integer.toString(sessionId)) );

            // login, but no user

            p.set( Constants.STREAM_REQUIRE_LOGIN, Properties.YES );
            p.set( Constants.WWW_USERS_REQUIRE_LOGIN, Properties.YES );

            data = renderPlaylist( tplClass, p, null );

            assertTrue( !data.contains(sessionCode) );
            assertTrue( !data.contains(Integer.toString(sessionId)) );

        }

    }
    
    public void testTracksSpecifedByIdAreIncludedInPlaylist() throws Exception {
        
        Request req = new TestRequest( "GET /xspf/tr1/tr3 HTTP/1.1" );
        pl.setRequest( req );
        
        pl.handleRequest();
        
        assertContains( res.getOutput(), "First Track" );
        assertContains( res.getOutput(), "Third Track" );
        assertNotContains( res.getOutput(), "Second Track" );
        
    }

    public void testAllTracksFromAlbumsSpecifedByIdAreIncludedInPlaylist() throws Exception {
        
        Request req = new TestRequest( "GET /xspf/al1 HTTP/1.1" );
        pl.setRequest( req );
        
        pl.handleRequest();
        
        assertContains( res.getOutput(), "First Track" );
        assertContains( res.getOutput(), "Second Track" );
        assertNotContains( res.getOutput(), "Third Track" );
        
    }

    public void testTracksThatMatchPathSpecifiedAreIncludedInPlaylist() throws Exception {
        
        p.set( Constants.WWW_BROWSE_FOLDERS_ENABLED, p.YES );
        
        Request req = new TestRequest( "GET /xspf/?path=/music/folder HTTP/1.1" );
        pl.setRequest( req );
        
        pl.handleRequest();
        
        assertContains( res.getOutput(), "First Track" );
        assertContains( res.getOutput(), "Third Track" );
        assertNotContains( res.getOutput(), "Second Track" );

    }
    
    public void testTracksNotReturnedByPathWhenFolderBrowsingNotEnabled() throws Exception {
        
        Request req = new TestRequest( "GET /xspf/?path=/music/folder HTTP/1.1" );
        pl.setRequest( req );
        
        pl.handleRequest();
        
        assertNotContains( res.getOutput(), "First Track" );
        assertNotContains( res.getOutput(), "Third Track" );
    }

}
