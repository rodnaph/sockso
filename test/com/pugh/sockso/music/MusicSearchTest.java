
package com.pugh.sockso.music;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;

import java.util.Vector;

public class MusicSearchTest extends SocksoTestCase {
    
    private TestDatabase db;
    
    private MusicSearch search;
    
    @Override
    protected void setUp() throws Exception {
        db = new TestDatabase();
        db.fixture( "singleTrack" );
        db.fixture( "singlePlaylist" );
        search = new MusicSearch( db );
    }

    public void testSearchingForATrackReturnsIt() throws Exception {
        Vector<MusicItem> items = search.search( "My Track" );
        assertEquals( 1, items.size() );
        assertEquals( "My Track", items.get(0).getName() );
    }
    
    public void testSearchingForAnAlbumReturnsIt() throws Exception {
        Vector<MusicItem> items = search.search( "My Album" );
        assertEquals( 1, items.size() );
        assertEquals( "My Album", items.get(0).getName() );
    }
    
    public void testSearchingForAnArtistReturnsIt() throws Exception {
        Vector<MusicItem> items = search.search( "My Artist" );
        assertEquals( 1, items.size() );
        assertEquals( "My Artist", items.get(0).getName() );
    }
    
    public void testSearchingForAPlaylistReturnsIt() throws Exception {
        Vector<MusicItem> items = search.search( "Foo Bar" );
        assertEquals( 1, items.size() );
        assertEquals( "Foo Bar", items.get(0).getName() );
    }
    
    public void testTracksReturnedAsTrackObjects() throws Exception {
        Vector<MusicItem> items = search.search( "My Track" );
        assertEquals( Track.class, items.get(0).getClass() );
    }
    
    public void testTracksReturnedHaveArtistAndAlbum() throws Exception {
        Vector<MusicItem> items = search.search( "My Track" );
        Track track = (Track) items.get( 0 );
        assertEquals( "My Artist", track.getArtist().getName() );
        assertEquals( "My Album", track.getAlbum().getName() );
    }
    
    public void testAlbumsReturnedAsAlbumObjects() throws Exception {
        Vector<MusicItem> items = search.search( "My Album" );
        assertEquals( Album.class, items.get(0).getClass() );
    }
    
    public void testAlbumsReturnedHaveArtist() throws Exception {
        Vector<MusicItem> items = search.search( "My Album" );
        Album album = (Album) items.get( 0 );
        assertEquals( "My Artist", album.getArtist().getName() );
    }
    
}
