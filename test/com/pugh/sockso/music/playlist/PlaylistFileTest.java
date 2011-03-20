
package com.pugh.sockso.music.playlist;

import com.pugh.sockso.tests.SocksoTestCase;

import java.io.File;

public class PlaylistFileTest extends SocksoTestCase {

    public void testM3uFileSetUpProperlyWhenReturned() throws Exception {
        PlaylistFile file = PlaylistFile.getPlaylistFile( new File("test/data/test.m3u") );
        assertEquals( 4, file.getPaths().length );
    }

}
