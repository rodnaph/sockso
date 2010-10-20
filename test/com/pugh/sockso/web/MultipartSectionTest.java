
package com.pugh.sockso.web;

import com.pugh.sockso.Main;
import com.pugh.sockso.tests.TestUtils;

import java.io.IOException;
import java.io.DataInputStream;

import junit.framework.TestCase;

public class MultipartSectionTest extends TestCase {

    static { Main.initTestLogger(); }
    
    public void testConstructor() throws IOException {
        
        final MultipartSection ms = new MultipartSection();
        
        assertNotNull( ms );
        
    }
    
    public void testProcess() throws IOException {
        
        final String boundary = "a5hsgs";
        final String filename = "myFile.txt";
        final String name = "myFileName";
        final String contentType = "text/plain";
        final String data = "some text data in a file";
        
        final String sectionData =
            "Content-Disposition: form-data; name=\"" +name+ "\"; filename=\"" +filename+ "\"\r\n" +
            "Content-Type: " +contentType+ "\r\n" +
            "\r\n" +
            data+ "\r\n--" +boundary+ "\r\n" +
            "Content-Disposition: form-data; name=\"foo\"\r\n\r\ndata\r\n--" +boundary+ "--";
        final InputBuffer buffer = new InputBuffer( getData(sectionData) );
        final MultipartSection section = new MultipartSection();
        
        section.process( buffer, boundary );
        
        assertEquals( filename, section.getFilename() );
        assertEquals( name, section.getName() );
        assertEquals( contentType, section.getContentType() );
        
        final MultipartSection section2 = new MultipartSection();
        
        section2.process( buffer, boundary );
        
        assertEquals( "data", section2.getData() );
        
    }
    
    private DataInputStream getData( final String data ) {
        return new DataInputStream(
            TestUtils.getInputStream( data )
        );
    }
    
    public void testParseData() throws IOException {
        
        final StringOutputStream out = new StringOutputStream();
        final MultipartSection ms = new MultipartSection();
        final String boundary = "asdasdasdasd";
        final InputBuffer buffer = new InputBuffer( getData("some data\r\n--" +boundary+ "") );
        
        ms.parseData( buffer, boundary, out );
        
        final String data = out.toString();
        
        assertEquals( "some data", data );
        
    }

    public void testParseDataNoBoundary() throws IOException {
        
        final StringOutputStream out = new StringOutputStream();
        final MultipartSection ms = new MultipartSection();
        final String boundary = "asdasdasdasd";
        final InputBuffer buffer = new InputBuffer( getData("some data") );
        
        ms.parseData( buffer, boundary, out );
        
        final String data = out.toString();
        
        assertEquals( "some data", data );
        
    }

    public void testParseHeaders() throws IOException {
        
        final String headerData = "Content-Disposition: form-data; name=\"myFile\"; filename=\"myFileName\"\r\n" +
                                  "Content-Type: text/xml\r\n";
        final InputBuffer buffer = new InputBuffer( getData(headerData) );
        final MultipartSection ms = new MultipartSection();
        
        ms.parseHeaders( buffer );

        assertEquals( "myFile", ms.getName() );
        assertEquals( "myFileName", ms.getFilename() );
        assertEquals( "text/xml", ms.getContentType() );
        
    }

    public void testParseDataRepeating() throws IOException {
        
        final StringOutputStream out = new StringOutputStream();
        final MultipartSection ms = new MultipartSection();
        final String boundary = "asd";
        final String expected = "asasasasasa\r\n--asasasasasasa\r\n--asasasasasasas\r\n--asasasasas\r\n--asasasas\r\n--asasasas\r\n--asasasas\r\n--asasasa\r\n--asas\r\n--asasasasasasa";
        final InputBuffer buffer = new InputBuffer( getData(expected+ "\r\n--" +boundary+ "") );
        
        ms.parseData( buffer, boundary, out );
        
        final String actual = out.toString();
        
        assertEquals( expected, actual );
        
    }

    // NB. PERFORMANCE TEST
    
    public void testParseLotsOfData() throws Exception {

        final String data = com.pugh.sockso.Utils.getRandomString( 1024 * 1024 * 5 ); // 5MB
        final String boundary = "aaaaaaaaaaaaaaaaaaaaaaaa";
        final InputBuffer buffer = new InputBuffer( getData(data+"\r\n--"+boundary) );
        final MultipartSection ms = new MultipartSection();
        final StringOutputStream out = new StringOutputStream();

        final long start = System.currentTimeMillis();

        ms.parseData( buffer, boundary, out );

        final long target = 2000;
        final long total = System.currentTimeMillis() - start;

        System.out.println( "Target: " +target+ ", Total: " +total );

        assertTrue( total < target );

    }

}
