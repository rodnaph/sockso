
package com.pugh.sockso.web.action.playlist;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.Utils;
import com.pugh.sockso.templates.TXspf;
import com.pugh.sockso.templates.TPls;
import com.pugh.sockso.templates.TM3u;
import com.pugh.sockso.tests.PlaylistTestCase;
import com.pugh.sockso.web.User;

public class PlaylisterTest extends PlaylistTestCase {

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

}
