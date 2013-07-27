
package com.pugh.sockso.web.action.playlist;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.music.Album;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.music.Genre;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.templates.TXspf;
import com.pugh.sockso.tests.PlaylistTestCase;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.Server;
import com.pugh.sockso.tests.TestResponse;

import static org.easymock.EasyMock.*;

public class XspferTest extends PlaylistTestCase {
    
    public void testShowXspf() throws Exception {
        
        final Request req = createNiceMock( Request.class );
        
        final Server s = createNiceMock( Server.class );
        
        final TestResponse res = new TestResponse();
        final TXspf tpl = new TXspf();
        final Artist artist = new Artist( 1, "artist" );
        final Album album = new Album( artist, 1, "album", "year" );
        final Genre genre = new Genre( 1, "genre" );
        Track.Builder builder = new Track.Builder();
        builder.artist(artist)
                .album(album)
                .genre(genre)
                .id(1)
                .name("track")
                .number(2)
                .path("")
                .dateAdded(null);
        final Track track = builder.build();
        final String protocol = "hTTppTT";
        
        tpl.setRequest( req );
        tpl.setProtocol( protocol );
        tpl.setTracks( new Track[] {track} );
        tpl.setProperties( new StringProperties() );
        
        res.showTemplate( tpl.makeRenderer() );
        
        final String data = res.getOutput();
        
        assertTrue( data.length() > 0 );
        assertTrue( data.contains(track.getName()) );
        assertTrue( data.contains(protocol) );
        
    }

    public void testDisableXspfCovers() throws Exception {

        final Properties p = new StringProperties();

        p.set( Constants.COVERS_DISABLED, Properties.NO );
        assertTrue( renderPlaylist(TXspf.class,p,null).contains("/file/cover/") );

        p.set( Constants.COVERS_DISABLED, Properties.YES );
        assertTrue( !renderPlaylist(TXspf.class,p,null).contains("/file/cover/") );

    }

    public void testShowAlbumOrArtistCovers() throws Exception {

        final Properties p = new StringProperties();

        p.set( Constants.COVERS_DISABLED, Properties.NO );
        p.set( Constants.COVERS_XSPF_DISPLAY, "artist" );
        assertTrue( renderPlaylist(TXspf.class,p,null).contains("/file/cover/ar") );

        p.set( Constants.COVERS_XSPF_DISPLAY, "" );
        assertTrue( renderPlaylist(TXspf.class,p,null).contains("/file/cover/al") );

    }

}
