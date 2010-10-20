
package com.pugh.sockso.web.action;

import com.pugh.sockso.tests.SocksoTestCase;

import java.io.DataInputStream;

public class MusicStreamTest extends SocksoTestCase {
    
    public void testConstructor() {
        
        final MusicStream ms = new MusicStream( null, null );
        
        assertNotNull( ms );
        
    }
    
    public void testGetAudioStream() {
        
        final DataInputStream in = new DataInputStream( null );
        final MusicStream ms = new MusicStream( in, null );
        
        assertEquals( in, ms.getAudioStream() );
        
    }
    
    public void testGetMimeType() {

        final String mimeType = "foo/bar";
        final MusicStream ms = new MusicStream( null, mimeType );
        
        assertEquals( mimeType, ms.getMimeType() );

    }
    
}
