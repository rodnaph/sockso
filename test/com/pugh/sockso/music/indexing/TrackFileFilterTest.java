
package com.pugh.sockso.music.indexing;

import com.pugh.sockso.tests.SocksoTestCase;

import java.io.File;
import java.io.FileFilter;

public class TrackFileFilterTest extends SocksoTestCase {

    public void testHiddenFilesIgnored() {
        FileFilter filter = new TrackFileFilter();
        assertFalse( filter.accept(new File(".hiddenFile.mp3")) );
        assertFalse( filter.accept(new File(".hiddenFile.txt")) );
        assertFalse( filter.accept(new File("/some/dir/.hiddenFile.mp3")) );
        assertFalse( filter.accept(new File("/some/dir/.hiddenFile.pdf")) );
    }

    public void testHiddenFoldersAreIgnored() {
        FileFilter filter = new TrackFileFilter();
        assertFalse( filter.accept(new Directory( "/some/folder/.hidden" )) );
    }

    public void testTrackFilesAreAccepted() {
        FileFilter filter = new TrackFileFilter();
        assertTrue( filter.accept(new File("file.mp3")) );
        assertTrue( filter.accept(new File("file.wma")) );
        assertTrue( filter.accept(new File("file.ogg")) );
        assertTrue( filter.accept(new File("file.asf")) );
        assertTrue( filter.accept(new File("file.flac")) );
        assertTrue( filter.accept(new File("file.m4a")) );
        assertTrue( filter.accept(new File("file.MP3")) );
        assertTrue( filter.accept(new File("/folder/file.MP3")) );
    }

}

class Directory extends File {

    public Directory( final String name ) {
        super( name );
    }

    public boolean isDirectory() {
        return true;
    }
    
}
