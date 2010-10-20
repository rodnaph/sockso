
package com.pugh.sockso.music.playlist;

import com.pugh.sockso.tests.SocksoTestCase;

import java.io.File;

public class M3uFileTest extends SocksoTestCase {
    
    public void testLoadStandardm3uFile() throws Exception {
        final StandardM3uFile f = new StandardM3uFile();
        f.load();
        final String[] paths = f.getPaths();
        assertEquals( 2, paths.length );
        assertEquals( "c:\\music\\track.mp3", paths[0] );
    }

    public void testLoadExtendedm3uFile() throws Exception {
        final ExtendedM3uFile f = new ExtendedM3uFile();
        f.load();
        final String[] paths = f.getPaths();
        assertEquals( 2, paths.length );
        assertEquals( "http://domain/track.mp3", paths[1] );
    }
 
    public void testGetLines() throws Exception {
        final File testFile = new File( "test-data/text.txt" );
        final M3uFile f = new M3uFile( testFile );
        f.load();
        final String[] paths = f.getPaths();
        assertEquals( 4, paths.length );
        assertEquals( "first line", paths[0] );
    }
    
    class StandardM3uFile extends M3uFile {
        public StandardM3uFile() { super( null ); }
        @Override
        protected String[] getLines( final File file ) {
            return new String[] {
                "c:\\music\\track.mp3",
                "http://domain/track.mp3"
            };
        }
    }

    class ExtendedM3uFile extends M3uFile {
        public ExtendedM3uFile() { super( null ); }
        @Override
        protected String[] getLines( final File file ) {
            return new String[] {
                "#EXTM3U",
                "#EXTINF:123,Some Track Name",
                "c:\\music\\track.mp3",
                "#EXTINF:456,Another Track Name",
                "http://domain/track.mp3"
            };
        }
    }

}