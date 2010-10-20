
package com.pugh.sockso.gui;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;

public class PlaylistPanelTest extends SocksoTestCase {

    public void testCheckingPlaylistExists() throws Exception {
        TestDatabase db = new TestDatabase();
        PlaylistPanel p = new PlaylistPanel( null, db, null );
        db.fixture( "singlePlaylist" );
        assertTrue( p.playlistExists("Foo Bar") );
        assertFalse( p.playlistExists("Bar Foo") );
    }
    
}
