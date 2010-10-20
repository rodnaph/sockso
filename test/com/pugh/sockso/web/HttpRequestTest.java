/*
 * HttpRequestTest.java
 * 
 * Created on Jul 23, 2007, 11:03:10 PM
 * 
 * Test the HTTP request processor class
 * 
 */

package com.pugh.sockso.web;

import com.pugh.sockso.tests.TestUtils;
import com.pugh.sockso.tests.SocksoTestCase;

import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;

import static org.easymock.EasyMock.*;

public class HttpRequestTest extends SocksoTestCase {

    public void testConstructor() throws Exception {
        
        InputStream in = TestUtils.getInputStream( "GET / HTTP/1.1\r\nasdasd\r\n\r\n" );
        Server server = createMock( Server.class );
        
        HttpRequest r = new HttpRequest( server );

    }

    public void testGetHost() throws Exception {
        
        String host = "jaksdhk";
        InputStream in = TestUtils.getInputStream( "GET / HTTP/1.1\r\nHost: " + host + "\r\n" );
        Server server = createMock( Server.class );
        
        HttpRequest r = new HttpRequest( server );
        r.process( in );
        assertEquals( host, r.getHost() );

    }

    public void testGetUrlParam() throws Exception {
        
        InputStream in = TestUtils.getInputStream( "GET /foo/bar/FOOBAR HTTP/1.1\r\n" );
        Server server = createMock( Server.class );
        
        HttpRequest r = new HttpRequest( server );
        r.process( in );
        assertEquals( r.getUrlParam(0), "foo" );
        assertEquals( r.getUrlParam(1), "bar" );
        assertEquals( r.getUrlParam(2), "FOOBAR" );

    }

    public void testGetParamCount() throws Exception {
        
        InputStream in = TestUtils.getInputStream( "GET /foo/bar/FOOBAR HTTP/1.1\r\n" );
        Server server = createMock( Server.class );
        
        HttpRequest r = new HttpRequest( server );
        r.process( in );
        assertEquals( r.getParamCount(), 3 );

    }

    public void testGetPlayParamsNoSkip() throws Exception {
        
        InputStream in = TestUtils.getInputStream( "GET /play/ar123/al456 HTTP/1.1\r\n" );
        Server server = createMock( Server.class );
        
        HttpRequest r = new HttpRequest( server );
        r.process( in );
        String[] params = r.getPlayParams( false );
        assertEquals( params.length, 2 );
        assertEquals( params[0], "ar123" );
        assertEquals( params[1], "al456" );

    }

    public void testGetPlayParamsWithSkip() throws Exception {
        
        InputStream in = TestUtils.getInputStream( "GET /play/music/ar123/al456 HTTP/1.1\r\n" );
        Server server = createMock( Server.class );
        
        HttpRequest r = new HttpRequest( server );
        r.process( in );
        String[] params = r.getPlayParams( true );
        assertEquals( params.length, 2 );
        assertEquals( params[0], "ar123" );
        assertEquals( params[1], "al456" );

    }

    public void testGetPlayParamsNumSkips() throws Exception {
        
        InputStream in = TestUtils.getInputStream( "GET /play/ar123/al456/pl253 HTTP/1.1\r\n" );
        Server server = createMock( Server.class );
        
        HttpRequest r = new HttpRequest( server );
        r.process( in );
        String[] params = r.getPlayParams( 2 );
        assertEquals( params.length, 1 );
        assertEquals( params[0], "pl253" );

    }

    public void testAddCookies() throws Exception {

        InputStream in = TestUtils.getInputStream( "GET / HTTP/1.1\r\n" );
        Server server = createMock( Server.class );
        
        HttpRequest r = new HttpRequest( server );
        r.process( in );
        int origCount = r.cookies.size();
        r.addCookies( " foo=bar; " );

        assertEquals( (origCount+1), r.cookies.size() );

    }

    public void testReadPostData() throws Exception {

        InputStream in = TestUtils.getInputStream(
            "GET / HTTP/1.1\r\n" +
            "Host: local\r\n\r\n" +
            "foo=bar&baz=foo+bar"
        );
        Server server = createMock( Server.class );
        HttpRequest r = new HttpRequest( server );
        r.process( in );

        assertEquals( "bar", r.getArgument("foo") );
        assertEquals( "foo bar", r.getArgument("baz") );

        assertEquals( "", r.getArgument("foobar") );

    }

    public void testGetCookie() throws Exception {
        
        InputStream in = TestUtils.getInputStream(
            "GET / HTTP/1.1\r\n" +
            "Cookie: foo=bar; baz=foobar;\r\n\r\n"
        );
        Server server = createMock( Server.class );
        HttpRequest r = new HttpRequest( server );
        r.process( in );

        assertEquals( "bar", r.getCookie("foo") );
        assertEquals( "foobar", r.getCookie("baz") );
        
    }
    
    public void testReadGetData() throws Exception {

        InputStream in = TestUtils.getInputStream(
            "GET /action?foo=rab&bar=oof HTTP/1.1\r\n" +
            "\r\n"
        );
        Server server = createMock( Server.class );
        HttpRequest r = new HttpRequest( server );
        r.process( in );

        assertEquals( "rab", r.getArgument("foo") );
        assertEquals( "oof", r.getArgument("bar") );
        assertEquals( "", r.getArgument("foooof") );

    }

    public void testHasArgument() throws Exception {

        InputStream in = TestUtils.getInputStream(
            "GET /action?foo=rab&bar=oof HTTP/1.1\r\n" +
            "\r\n"
        );
        Server server = createMock( Server.class );
        HttpRequest r = new HttpRequest( server );
        r.process( in );

        assertEquals( true, r.hasArgument("foo") );
        assertEquals( false, r.hasArgument("barbar") );

    }

    /**
     *  all lines in a HTTP request *should* end with \r\n, but we'll try and
     *  handle just \n aswell incase.
     * 
     *  @throws java.io.IOException
     *  @throws com.pugh.sockso.web.BadRequestException
     * 
     */

    public void testHandlesDodgyLineEndings() throws Exception {

        // mix of good and bad line endings
        InputStream in = TestUtils.getInputStream(
            "GET / HTTP/1.1\n" +
            "Host: local\n\r\n" +
            "foo=bar&baz=foo+bar"
        );
        Server server = createMock( Server.class );
        HttpRequest r = new HttpRequest( server );
        r.process( in );

        assertEquals( "bar", r.getArgument("foo") );
        assertEquals( "foo bar", r.getArgument("baz") );

        assertEquals( "", r.getArgument("foobar") );

    }

    public void testGetMultipartData() throws Exception {

        InputStream in = TestUtils.getInputStream(
            "GET /action?foo=rab&bar=oof HTTP/1.1\r\n" +
            "Content-Type: multipart/form-data; boundary=AaB03x\r\n" +
            "\r\n" +
            "--AaB03x\r\n" +
            "Content-Disposition: form-data; name=\"name\"\r\n" +
            "\r\n" +
            "Larry\r\n" +
            "--AaB03x\r\n" +
            "Content-Disposition: form-data; name=\"myFile\"; filename=\"file1.txt\"\r\n" +
            "Content-Type: text/plain\r\n" +
            "\r\n" +
            "contents of file\r\n" +
            "--AaB03x--\r\n"
        );
        Server server = createMock( Server.class );
        HttpRequest r = new HttpRequest( server );
        r.process( in );

        assertEquals( true, r.hasArgument("foo") );
        assertEquals( true, r.hasArgument("name") );
        assertEquals( "Larry", r.getArgument("name") );
        
        UploadFile file = r.getFile( "myFile" );

        assertNotNull( file );
        assertEquals( "file1.txt", file.getFilename() );
        assertNotNull( file.getTemporaryFile() );
        assertEquals( "text/plain", file.getContentType() );

    }
    
    /**
     *  uses the test request data for a particular browser and platform
     * 
     *  @param platform
     *  @param name
     * 
     */
    
    private void testRequest( final String platform, final String browser ) throws Exception {

        final String folder = "test-data/requests/" +platform+ "/" +browser+ "/";
        final File getFile = new File( folder + "get.txt" );
        final File postFile = new File( folder + "post.txt" );
        final File multipartFile = new File( folder + "multipart.txt" );

        // request objects
        
        final Request getReq = new HttpRequest( null );
        final Request postReq = new HttpRequest( null );
        final Request multipartReq = new HttpRequest( null );
        
        // get request
        
        getReq.process( new DataInputStream( new FileInputStream(getFile) ) );

        assertEquals( "bar", getReq.getArgument("foo") );
        
        // post request
        
        postReq.process( new DataInputStream( new FileInputStream(postFile) ) );

        assertEquals( "bar", postReq.getArgument("foo") );

        // multipart request
        
        multipartReq.process( new DataInputStream( new FileInputStream(multipartFile) ) );

        assertEquals( "bar", multipartReq.getArgument("foo") );
        
        final UploadFile uploadedFile = multipartReq.getFile( "myFile" );
        
        if ( !TestUtils.compareFiles( uploadedFile.getTemporaryFile(), new File("test-data/binary.bin") ) ) {
            fail( "Uploaded file not correct" );
        }

    }
    
    // OSX
    
    public void testOsxSafari() throws Exception {
        testRequest("osx","safari-3.2");
    }
    
    public void testOsxFirefox() throws Exception {
        testRequest("osx","firefox-3.0");
    }

    public void testOsxOpera() throws Exception {
        testRequest("osx","opera-9.6");
    }
    
    // WINDOWS
    
    public void testWindowsIE() throws Exception {
        testRequest( "windows", "ie-6.0" );
    }

    public void testWindowsFirefox() throws Exception {
        testRequest( "windows", "firefox-2.0" );
        testRequest( "windows", "firefox-3.0" );
    }

    // LINUX
    
    public void testLinuxKonqueror() throws Exception {
        testRequest( "linux", "konqueror-3.5" );
    }
    
    public void testLinuxFirefox() throws Exception {
        testRequest( "linux", "firefox-2.0" );
        testRequest( "linux", "firefox-3.0" );
    }
    
}
