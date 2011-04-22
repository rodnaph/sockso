
package com.pugh.sockso.tests;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.replay;

import java.util.Vector;

import com.pugh.sockso.Properties;
import com.pugh.sockso.music.Album;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.Server;
import com.pugh.sockso.web.User;
import com.pugh.sockso.web.action.playlist.PlaylistTemplate;

public class PlaylistTestCase extends SocksoTestCase {

    protected String renderPlaylist( final Class tplClass, final Properties p, final User user ) throws Exception {

        final Vector<Track> tracks = new Vector<Track>();
        final Track track = new Track( new Artist(1,""), new Album(null,1,"",""), 1, "", "", 1, null );
        final Server server = createNiceMock( Server.class );
        final Request req = createNiceMock( Request.class );

        replay( server );
        replay( req );

        tracks.add( track );

        final PlaylistTemplate tpl = (PlaylistTemplate) tplClass.newInstance();

        tpl.setProperties( p );
        tpl.setUser( user );
        tpl.setTracks( tracks );
        tpl.setRequest( req );
        tpl.setProtocol( "http" );

        return tpl.makeRenderer().asString();

    }

}
