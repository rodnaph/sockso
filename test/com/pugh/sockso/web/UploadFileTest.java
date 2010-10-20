/*
 * Tests for the UploadFile class
 * 
 */

package com.pugh.sockso.web;

import com.pugh.sockso.Main;

import java.io.File;

import junit.framework.TestCase;

public class UploadFileTest extends TestCase {

    static { Main.initTestLogger(); }

    public void testConstructor() {
        
        String name = "myFileName";
        String contentType = "text/plain";
        String data = "here is some data";
        String filename = "some file name.txt";
        File tempFile = new File( "asdasd" );
        
        UploadFile file = new UploadFile( name, contentType, data, filename, tempFile );
        
        assertNotNull( file );
        assertEquals( name, file.getName() );
        assertEquals( contentType, file.getContentType() );
        assertEquals( data, file.getData() );
        assertEquals( filename, file.getFilename() );

    }
    
}
