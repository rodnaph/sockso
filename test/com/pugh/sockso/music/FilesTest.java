
package com.pugh.sockso.music;

import junit.framework.TestCase;

public class FilesTest extends TestCase {

    public void testGetMimeTypeReturnsTypeForKnownFiles() {
        assertEquals( "text/css", Files.getMimeType("default.css") );
        assertEquals( "audio/mpeg", Files.getMimeType("/home/me/default.mp3") );
        assertEquals( "audio/mpegurl", Files.getMimeType("c:\\Users\\Me\\file.m3u") );
    }

    public void testGetmimetypeReturnsNullDefaultForUnknownTypes() {
        assertEquals( Files.DEFAULT_MIME_TYPE, Files.getMimeType("file.asdjkas") );
    }

    public void testAcceptableMimeTypesAreAllowed() {
        assertTrue( Files.isValidMimeType("audio/mpg") );        
    }

    public void testUnacceptableMimeTypesAreNotAllowed() {
        assertFalse( Files.isValidMimeType("text/plain") );        
    }

    public void testValidFilesExtensionsAreAllowed() {
        assertTrue( Files.isValidFileExtension("mp3") );
        assertTrue( Files.isValidFileExtension("MP3") );
    }

    public void testInvalidFileExtensionsAreNotAllowed() {
        assertFalse( Files.isValidFileExtension("txt") );
    }
     
}
