/*
 * HttpResponseCookieTest.java
 * 
 * Created on Aug 3, 2007, 10:52:24 PM
 * 
 * Tests the HttpResponseCookie class
 * 
 */

package com.pugh.sockso.web;

import com.pugh.sockso.Constants;

import java.util.Date;

import java.text.SimpleDateFormat;

import junit.framework.TestCase;

public class HttpResponseCookieTest extends TestCase {

    public void testConstructorBasic() {
        
        HttpResponseCookie cookie = new HttpResponseCookie( "foo", "bar" );
        
        assertEquals( "foo", cookie.getName() );
        assertEquals( "bar", cookie.getValue() );

    }
    
    public void testConstructor() {
        
        SimpleDateFormat formatter = new SimpleDateFormat( Constants.HTTP_COOKIE_DATE_FORMAT );
        Date now = new Date();
        
        HttpResponseCookie cookie = new HttpResponseCookie(
            "foo", "bar", now, "/"
        );
        
        assertEquals(
            "foo=bar; Path=/; Expires=" + formatter.format(now) + "; " +
            "Version=1; Max-Age=" + HttpResponseCookie.MAX_AGE + "; ",
            cookie.toString()
        );
        
    }

    public void testGetName() {
        
        HttpResponseCookie cookie = new HttpResponseCookie( "foo", "bar", new Date(), "/" );
        
        assertEquals( "foo", cookie.getName() );

    }

    public void testGetValue() {
        
        HttpResponseCookie cookie = new HttpResponseCookie( "foo", "bar", new Date(), "/" );
        
        assertEquals( "bar", cookie.getValue() );

    }

    public void testEquals() {
        
        HttpResponseCookie c1 = new HttpResponseCookie( "foo", "bar", new Date(), "/" );
        HttpResponseCookie c2 = new HttpResponseCookie( "foo", "bar", new Date(), "/" );
        HttpResponseCookie c3 = new HttpResponseCookie( "baz", "bar", new Date(), "/" );
        
        assertTrue( c1.equals(c2) );
        assertTrue( c2.equals(c1) );
        assertFalse( c1.equals(c3) );
        assertFalse( c3.equals(c1) );

    }
    
}
