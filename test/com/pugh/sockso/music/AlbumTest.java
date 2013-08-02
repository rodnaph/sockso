
package com.pugh.sockso.music;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;

import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class AlbumTest extends SocksoTestCase {
    
    private TestDatabase db;
    
    @Override
    protected void setUp() {
        db = new TestDatabase();
    }
    
    public void testGetArtist() {
        
        final int id = 123;
        final String name = "qwe rty";
        final Artist artist = new Artist.Builder().id(id).name(name).build();
        final Album album = new Album.Builder().artist(artist).build();
        
        assertEquals( artist, album.getArtist() );
        
    }
    
    public void testGetTrackCount() {

        final int trackCount = 148;
        final Album album = new Album.Builder().trackCount(trackCount).build();

        assertEquals( trackCount, album.getTrackCount() );
        
    }

    public void testGetPlayCount() {

        final int playCount = 148;
        final Album album = new Album.Builder().playCount(playCount).build();

        assertEquals( playCount, album.getPlayCount() );
        
    }

    public void testGetDateAdded() {

        final Date theDate = new Date();
        final Album album = new Album.Builder().dateAdded(theDate).build();

        assertEquals( theDate, album.getDateAdded() );
        
    }

    public void testGettingTheYearReturnsIt() {

        final String year = "2001";
        final Album album = new Album.Builder().year(year).build();

        assertEquals( "2001", album.getYear() );

    }

    public void testOnlyYearPartOfDateIsReturnedForYearIfItIncludesOtherInfo() {

        final String year = "2001-02-01";
        final Album album = new Album.Builder().year(year).build();

        assertEquals( "2001", album.getYear() );
    }

    public void testEmptyStringReturnedWhenYearIsNull() {

        final Album album = new Album.Builder().build();

        assertEquals( "", album.getYear() );
    }

    public void testFindbyartistidReturnsAllAlbumsForTheSpecifiedArtist() throws Exception {
        db.fixture( "artistsAlbumsAndTracks" );
        List<Album> albums = Album.findByArtistId( db, 1 );
        assertEquals( 2, albums.size() );
    }
    
    public void testFindbyartistidReturnsNoAlbumsOnInvalidArtistId() throws Exception {
        List<Album> albums = Album.findByArtistId( db, 999 );
        assertEquals( 0, albums.size() );
    }
    
    public void testFindReturnsAlbumRequestedById() throws Exception {
        db.fixture( "albumTracks" );
        Album album = Album.find( db, 1 );
        assertEquals( 1, album.getId() );
        assertEquals( "An Album", album.getName() );
    }
    
    public void testFindReturnsNullWhenAlbumNotFound() throws Exception {
        assertNull( Album.find( db, 1 ) );
    }
    
    public void testFindReturnsAlbumWithArtistInfo() throws Exception {
        db.fixture( "albums" );
        Album album = Album.find( db, 1 );
        assertEquals( "A Artist", album.getArtist().getName() );
    }
    
    public void testFindallReturnsAllAlbums() throws Exception {
        db.fixture( "albums" );
        List<Album> albums = Album.findAll( db, 100, 0 );
        assertEquals( 3, albums.size() );
    }
    
    public void testFindallCanBeLimited() throws Exception {
        db.fixture( "albums" );
        List<Album> albums = Album.findAll( db, 2, 0 );
        assertEquals( 2, albums.size() );
    }
    
    public void testFindallCanBeOffset() throws Exception {
        db.fixture( "albums" );
        List<Album> albums = Album.findAll( db, 3, 1 );
        assertEquals( 2, albums.size() );
    }
    
    public void testLimitOfMinusOneToFindallMeansNoLimit() throws Exception {
        db.fixture( "albums" );
        List<Album> albums = Album.findAll( db, -1, 0 );
        assertEquals( 3, albums.size() );
    }
    
    public void testFindallReturnsAlbumsLexicographically() throws Exception {
        db.fixture( "albums" );
        List<Album> albums = Album.findAll( db, -1, 0 );
        assertEquals( "Another Album", albums.get(0).getName() );
        assertEquals( "Beta Third", albums.get(1).getName() );
        assertEquals( "Zan Album", albums.get(2).getName() );
    }
    
    public void testFindallReturnsArtistsWithAlbums() throws Exception {
        db.fixture( "albums" );
        List<Album> albums = Album.findAll( db, -1, 0 );
        assertEquals( "A Artist", albums.get(0).getArtist().getName() );
    }
    
}
