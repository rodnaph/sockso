
package com.pugh.sockso.gui;

import com.pugh.sockso.tests.SocksoTestCase;

import java.io.File;

public class PlaylistFileFilterTest extends SocksoTestCase {

    public void testFilterIncludesPlaylistFiles() {
        PlaylistFileFilter filter = new PlaylistFileFilter( null );
        for ( String type : PlaylistFileFilter.VALID_EXTENSIONS ) {
            assertTrue( filter.accept(new File("file."+type)) );
        }
    }

    public void testExtensionCheckingIsntCaseSensitive() {
        PlaylistFileFilter filter = new PlaylistFileFilter( null );
        for ( String type : PlaylistFileFilter.VALID_EXTENSIONS ) {
            assertTrue( filter.accept(new File("file."+type.toUpperCase())) );
            assertTrue( filter.accept(new File("file."+type.toLowerCase())) );
        }
    }

}
