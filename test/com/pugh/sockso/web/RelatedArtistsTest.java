
package com.pugh.sockso.web;

import com.pugh.sockso.music.Artist;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.web.action.AudioScrobbler;
import java.util.List;

import static org.easymock.classextension.EasyMock.*;

public class RelatedArtistsTest extends SocksoTestCase {

    private RelatedArtists related;
    
    @Override
    protected void setUp() throws Exception {
        String[] artists = new String[] { "Beep Artist", "Xylophone" };
        TestDatabase db = new TestDatabase();
        db.fixture( "artists" );
        AudioScrobbler scrobbler = createMock( AudioScrobbler.class );
        expect( scrobbler.getSimilarArtists(1) ).andReturn( artists );
        replay( scrobbler );
        related = new RelatedArtists( db, scrobbler );
    }
    
    public void testOnlyRelatedArtistsInCollectionAreReturned() throws Exception {
        List<Artist> artists = related.getRelatedArtistsFor( 1 );
        assertEquals( 2, artists.size() );
    }
    
    public void testArtistAddedDateIsReturnedWithArtistObjects() throws Exception {
        List<Artist> artists = related.getRelatedArtistsFor( 1 );
        assertNotNull( artists.get(0).getDateAdded() );
    }
}
