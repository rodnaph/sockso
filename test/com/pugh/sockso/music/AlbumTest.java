
package com.pugh.sockso.music;

import java.util.Date;

import com.pugh.sockso.tests.SocksoTestCase;

public class AlbumTest extends SocksoTestCase {

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

}
