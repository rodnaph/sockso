
package com.pugh.sockso.music;

import com.pugh.sockso.tests.SocksoTestCase;

import java.util.Date;

public class ArtistTest extends SocksoTestCase {
    
    public void testConstructors() {
        
        final int id = 123, albumCount = 456, trackCount = 789, playCount = 159;
        final String name = "some name";
        final Date dateAdded = new Date();
        
        assertNotNull( new Artist(id,name) );
        assertNotNull( new Artist(id,name,playCount) );
        assertNotNull( new Artist(id,name,dateAdded,albumCount,trackCount) );
        assertNotNull( new Artist(id,name,dateAdded,albumCount,trackCount,playCount) );

    }
    
    public void testGetters() {

        final int id = 123, albumCount = 456, trackCount = 789, playCount = 159;
        final String name = "some name";
        final Date dateAdded = new Date();
        final Artist artist = new Artist(id,name,dateAdded,albumCount,trackCount,playCount);

        assertEquals( dateAdded, artist.getDateAdded() );
        assertEquals( trackCount, artist.getTrackCount() );
        assertEquals( albumCount, artist.getAlbumCount() );
        assertEquals( playCount, artist.getPlayCount() );
        
    }
    
}
