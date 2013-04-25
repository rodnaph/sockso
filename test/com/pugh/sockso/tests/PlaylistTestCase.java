
package com.pugh.sockso.tests;

import com.pugh.sockso.Properties;
import com.pugh.sockso.music.Album;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.music.Genre;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.Server;
import com.pugh.sockso.web.User;
import com.pugh.sockso.web.action.playlist.PlaylistTemplate;

import static org.easymock.EasyMock.*;

public class PlaylistTestCase extends SocksoTestCase {

    protected String renderPlaylist( final Class tplClass, final Properties p, final User user ) throws Exception {

        Track.Builder builder = new Track.Builder();
        builder.artist(new Artist(1,""))
                .album(new Album(null,1,"",""))
                .genre(new Genre(1,""))
                .id(1)
                .name("")
                .number(1)
                .path("")
                .dateAdded(null);
        final Track track = builder.build();

        final Server server = createNiceMock( Server.class );
        final Request req = createNiceMock( Request.class );

        replay( server );
        replay( req );

        final PlaylistTemplate tpl = (PlaylistTemplate) tplClass.newInstance();

        tpl.setProperties( p );
        tpl.setUser( user );
        tpl.setTracks( new Track[] {track} );
        tpl.setRequest( req );
        tpl.setProtocol( "http" );

        return tpl.makeRenderer().asString();

    }

}
