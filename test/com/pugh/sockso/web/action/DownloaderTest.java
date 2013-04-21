
package com.pugh.sockso.web.action;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.music.Album;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestLocale;
import com.pugh.sockso.web.BadRequestException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DownloaderTest extends SocksoTestCase {

    public void testGetTrackZipPath() {
        
        final Properties p = new StringProperties();
        final Downloader d = new Downloader();
        
        d.setProperties( p );
        
        final Artist artist = new Artist( 1, "artist" );
        final Album album = new Album( artist, 2, "album", "year" );
        final Track track = new Track( artist, album, 3, "track", "", 4, new Date() );
        final String path = d.getTrackZipPath( track );
        
        assertTrue( path.contains("artist") );
        assertTrue( path.contains("album") );
        assertTrue( path.contains("track") );
        assertTrue( path.contains("04") ); // tens should be padded
        
    }

    private Track getTrack( final String artistName ) {
        return getTrack( artistName, "album", "year" );
    }
    
    private Track getTrack( final String artistName, final String albumName, final String albumYear ) {
        final Artist artist = new Artist( 1, artistName );
        final Album album = new Album( artist, 1, albumName, albumYear);
        return new Track( artist, album, 1, "track", "", 1, null );
    }

    public void testGettingTheFileNameWhenAllArtistsAreTheSame() {
        List<Track> tracks = new ArrayList<Track>();
        tracks.add( getTrack("artist") );
        tracks.add( getTrack("artist") );
        Downloader d = new Downloader();
        assertEquals( "artist-album.zip", d.getFileName(tracks) );
    }
    
    public void testGettingFilenameWhenDifferentArtists() {
        List<Track> tracks = new ArrayList<Track>();
        tracks.add( getTrack("artist1") );
        tracks.add( getTrack("artist2") );
        Downloader d = new Downloader();
        assertEquals( "various_artists-album.zip", d.getFileName(tracks) );
    }
    
    public void testGettingFilenameWhenDifferentAlbumsUsesMultipleAlbums() {
        List<Track> tracks = new ArrayList<Track>();
        tracks.add( getTrack("artist","album1","year") );
        tracks.add( getTrack("artist","album2","year") );
        Downloader d = new Downloader();
        assertEquals( "artist-various_albums.zip", d.getFileName(tracks) );
    }

    public void testExceptionThrownWhenDownloadsAreDisabled() throws Exception {
        Downloader d = new Downloader();
        Properties p = new StringProperties();
        p.set( Constants.WWW_DOWNLOADS_DISABLE, Properties.YES );
        boolean gotException = false;
        d.setProperties( p );
        d.setLocale( new TestLocale() );
        try { d.handleRequest(); }
        catch ( BadRequestException e ) { gotException = true; }
        assertTrue( gotException );
    }
    
}
