
package com.pugh.sockso.music.tag;

import java.io.File;

import com.pugh.sockso.tests.SocksoTestCase;

public class TagTest extends SocksoTestCase {
    
    public void testGuessArtist() {
        File f = getTestMp3File();
        assertEquals( "artist", AudioTag.guessArtist(f) );
    }

    public void testGuessAlbum() {
        File f = getTestMp3File();
        assertEquals( "album", AudioTag.guessAlbum(f) );
    }

    public void testGuessTrack() {
        File f = getTestMp3File();
        assertEquals( "song", AudioTag.guessTrack(f) );
    }

    public void testGuessTrackNumber() {
        File f = getTestMp3File();
        assertEquals( "01", AudioTag.guessTrackNumber(f) );
    }

    private File getTestMp3File() {
        return new File( "/home/user/downloads/artist - album/01 - song.mp3" );
    }
    
    public void testCheckTrackNumberForTotal() {
        
        assertEquals( "1", AudioTag.checkTrackNumberForTotal("1") );
        assertEquals( "4", AudioTag.checkTrackNumberForTotal("4/10") );
        
    }
    
    public void testSetTrackNumberFromTotalTracks() throws Exception {
        
        final AudioTag tag = new Mp3Tag();
        
        assertEquals( tag.getTrackNumber(), 0 );

        tag.setTrackNumber( "03/12" );
        assertEquals( tag.getTrackNumber(), 3 );

        tag.setTrackNumber( "7" );
        assertEquals( tag.getTrackNumber(), 7 );

        tag.setTrackNumber( "036" );
        assertEquals( tag.getTrackNumber(), 36 );

        tag.setTrackNumber( "nothing" ); // should fail, so expect no change
        assertEquals( tag.getTrackNumber(), 36 );

    }

}
