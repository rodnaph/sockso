/*
 * UtilsTest.java
 * JUnit 4.x based test
 *
 * Created on June 9, 2007, 10:52 AM
 */

package com.pugh.sockso;

import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.tests.SocksoTestCase;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import java.sql.Statement;
import java.sql.ResultSet;

import java.util.Date;

import static org.easymock.EasyMock.*;

public class UtilsTest extends SocksoTestCase {
    
    public void testGetExtString() {
        String file = "some.test.Mp3";
        String expResult = "mp3";
        String result = Utils.getExt(file);
        assertEquals(expResult, result);
    }
    
    public void testGetExtFile() {
        String expResult = "mp4";
        String result = Utils.getExt( new File("some/file/names/myfile.sss.mp4") );
        assertEquals( expResult,result );
    }

    public void testGetRandomString() {
        int length = 10;
        String result = Utils.getRandomString(length);
        assertEquals( length, result.length() );
    }

    public void testUrlEncode() {
        String exp = "MY+STRING";
        String res = Utils.URLEncode( "MY STRING" );
        assertEquals( exp, res );
    }
    
    public void testUrlDecode() {
        String exp = "MY STRING";
        assertEquals( exp, Utils.URLDecode("MY+STRING") );
        assertEquals( exp, Utils.URLDecode("MY%20STRING") );
    }
    
    public void testCloseStatement() {
        Statement s = createNiceMock( Statement.class );
        replay( s );
        Utils.close( s );
    }
    
    public void testCloseResultSet() {
        ResultSet rs = createNiceMock( ResultSet.class );
        replay( rs );
        Utils.close( rs );
    }
    
    public void testCloseInputStream() {
        Utils.close( new InputStream() {
            public int read() { return -1; }
            @Override
            public void close() {}
        });
    }

    public void testCloseOutputStream() {
        Utils.close( new OutputStream() {
            public void write( int i ) {}
            @Override
            public void close() {}
        });
    }
    
    public void testMD5() {
        String hash = Utils.md5( "FOO" );
        assertEquals( 32, hash.length() );
    }

    public void testFormatDate() {
        String date = Utils.formatDate( new Date() );
        assertTrue( date.length() > 0 );
    }
    
    public void testXmlEncode() {
        
        String badChars = "&<>\"";
        String changed = Utils.XMLEncode( badChars );
        
        assertEquals( "&amp;&lt;&gt;&quot;", changed );
        
    }
    
    public void testGetPathWithSlash() {
        
        final String noSlash = "/test/dir/no/slash";
        final String withSlash = "/test/dir/with/slash/";
        final String noBackSlash = "c:\\music\\folder";
        final String withBackSlash = "c:\\music\\folder\\";

        assertEquals( noSlash + "/", Utils.getPathWithSlash(noSlash,"/") );
        assertEquals( withSlash, Utils.getPathWithSlash(withSlash,"/") );
        assertEquals( noBackSlash + "\\", Utils.getPathWithSlash(noBackSlash,"\\") );
        assertEquals( withBackSlash, Utils.getPathWithSlash(withBackSlash,"\\") );

    }
    
    public void testEscapeJs() {
        
        final String expected = "qwe\\'rty";
        
        assertEquals( expected, Utils.escapeJs("qwe'rty") );
        
    }
    
    public void testCheckFeatureEnabled() {
        
        Properties p = createMock( Properties.class );
        expect( p.get("foo.bar") ).andReturn( "" );
        expect( p.get("foo.bar") ).andReturn( "yes" );
        replay( p );
        
        boolean isEnabled = false;
        try {
            // should throw error
            Utils.checkFeatureEnabled( p, "foo.bar" );
            isEnabled = true;
        }
        catch ( BadRequestException e ) {}
        
        if ( isEnabled ) fail( "Error, feature should not be enabled" );

        isEnabled = false;
        try {
            // should throw error
            Utils.checkFeatureEnabled( p, "foo.bar" );
            isEnabled = true;
        }
        catch ( BadRequestException e ) {}
        
        if ( !isEnabled ) fail( "Error, feature should be enabled" );

        verify( p );
        
    }
    
    public void testIsFeatureEnabled() {

        Properties p = createMock( Properties.class );
        expect( p.get("foo.bar") ).andReturn( "" );
        expect( p.get("foo.bar") ).andReturn( "yes" );
        replay( p );

        assertFalse( Utils.isFeatureEnabled(p,"foo.bar") );
        assertTrue( Utils.isFeatureEnabled(p,"foo.bar") );
        
    }
    
    public void testReplaceAll() {
        
        assertEquals( "acc", Utils.replaceAll("b", "c", "abc") );
        assertEquals( "aCCc", Utils.replaceAll("b", "CC", "aBc") );
        
    }
 
    public void testu2e() {
        
        assertEquals( "foo &#8730;&#235;", Utils.u2e("foo \u221a\u00eb") );
        
    }
    
    public void testJoinArray() {
     
        final String[] array = { "a", "b", "c", "d" };

        assertEquals( "a b", Utils.joinArray(array," ",0,1) );
        assertEquals( "aabacad", Utils.joinArray(array,"a",0,3) );
        
    }

}
