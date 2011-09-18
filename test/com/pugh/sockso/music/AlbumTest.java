
package com.pugh.sockso.music;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;

import java.util.Date;

public class AlbumTest extends SocksoTestCase {
    
    private TestDatabase db;
    
    @Override
    protected void setUp() {
        db = new TestDatabase();
    }
    
    public void testConstructor() {

        final int id = 123, artistId = 456, trackCount = 789, playCount = 159;
        final String name = "some name", artistName = "another", year = "1984";
        final Date theDate = new Date();

        assertNotNull( new Album( artistId, artistName, id, name, year ) );
        assertNotNull( new Album( new Artist(artistId,artistName), id, name, year ) );
        assertNotNull( new Album( new Artist(artistId,artistName), id, name, year ) );
        assertNotNull( new Album( new Artist(artistId,artistName), id, name, year, trackCount ) );
        assertNotNull( new Album( new Artist(artistId,artistName), id, name, year, theDate, trackCount, playCount ) );

    }

    public void testGetArtist() {
        
        final int id = 123;
        final String name = "qwe rty";
        final Artist artist = new Artist( id, name );
        final Album album = new Album( artist, -1, "", "");
        
        assertEquals( artist, album.getArtist() );
        
    }
    
    public void testGetTrackCount() {

        final int trackCount = 148;
        final Album album = new Album( new Artist(-1,""), -1, "", "", trackCount );

        assertEquals( trackCount, album.getTrackCount() );
        
    }

    public void testGetPlayCount() {

        final int playCount = 148;
        final Album album = new Album( new Artist(-1,""), -1, "", "", new Date(), -1, playCount );

        assertEquals( playCount, album.getPlayCount() );
        
    }

    public void testGetDateAdded() {

        final Date theDate = new Date();
        final Album album = new Album( new Artist(-1,""), -1, "", "", theDate, -1, -1 );

        assertEquals( theDate, album.getDateAdded() );
        
    }

    public void testGettingTheYearReturnsIt() {
        assertEquals( "2001", new Album(1,"",1,"","2001").getYear() );
    }

    public void testOnlyYearPartOfDateIsReturnedForYearIfItIncludesOtherInfo() {
        assertEquals( "2001", new Album(1,"",1,"","2001-02-01").getYear() );
    }

    public void testEmptyStringReturnedWhenDateIsNull() {
        assertEquals( "", new Album(1,"",1,"",null).getYear() );
    }
    
    public void testFindbyartistidReturnsAllAlbumsForTheSpecifiedArtist() throws Exception {
        db.fixture( "artistsAlbumsAndTracks" );
        Album[] albums = Album.findByArtistId( db, 1 );
        assertEquals( 2, albums.length );
    }
    
    public void testFindbyartistidReturnsNoAlbumsOnInvalidArtistId() throws Exception {
        Album[] albums = Album.findByArtistId( db, 999 );
        assertEquals( 0, albums.length );
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
        Album[] albums = Album.findAll( db, 100, 0 );
        assertEquals( 3, albums.length );
    }
    
    public void testFindallCanBeLimited() throws Exception {
        db.fixture( "albums" );
        Album[] albums = Album.findAll( db, 2, 0 );
        assertEquals( 2, albums.length );
    }
    
    public void testFindallCanBeOffset() throws Exception {
        db.fixture( "albums" );
        Album[] albums = Album.findAll( db, 3, 1 );
        assertEquals( 2, albums.length );
    }
    
    public void testLimitOfMinusOneToFindallMeansNoLimit() throws Exception {
        db.fixture( "albums" );
        Album[] albums = Album.findAll( db, -1, 0 );
        assertEquals( 3, albums.length );
    }
    
    public void testFindallReturnsAlbumsLexicographically() throws Exception {
        db.fixture( "albums" );
        Album[] albums = Album.findAll( db, -1, 0 );
        assertEquals( "Another Album", albums[0].getName() );
        assertEquals( "Beta Third", albums[1].getName() );
        assertEquals( "Zan Album", albums[2].getName() );
    }
    
    public void testFindallReturnsArtistsWithAlbums() throws Exception {
        db.fixture( "albums" );
        Album[] albums = Album.findAll( db, -1, 0 );
        assertEquals( "A Artist", albums[0].getArtist().getName() );
    }
    
}
