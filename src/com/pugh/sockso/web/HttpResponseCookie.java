/*
 * HttpResponseCookie.java
 * 
 * Created on Aug 3, 2007, 10:46:48 PM
 * 
 * A cookie that can be sent via a HTTP response
 * 
 */

package com.pugh.sockso.web;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Utils;

import java.util.Date;

import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

public class HttpResponseCookie {

    protected static final int MAX_AGE = 60 * 60 * 24 * 7; // a week in seconds
    
    private static final Logger log = Logger.getLogger( HttpResponseCookie.class );
    
    private final String name, value, path;
    private final Date expires;
    
    /**
     *  creates a cookie with the name and value that is for the root
     *  path and expires after a week
     * 
     *  @param name the cookie name
     *  @param value the cookie value
     * 
     */
    
    public HttpResponseCookie( String name, String value ) {
        this(
            name, value,
            new Date( new Date().getTime() + Constants.ONE_WEEK_IN_MILLIS ),
            "/"
        );
    }
    
    /**
     *  creates a new response cookie object
     * 
     *  @param name the cookie name
     *  @param value the cookie's value
     *  @param expires the date the cookie will expire
     *  @param path the path it's valid for
     * 
     */
    
    public HttpResponseCookie( String name, String value, Date expires, String path ) {
        this.name = name;
        this.value = value;
        this.expires = expires;
        this.path = path;
    }
    
    /**
     *  converts this cookie to it's string representation, which
     *  is the correct format for being sent in a HTTP header
     * 
     *  @return the cookie as a string
     * 
     */

    @Override
    public String toString() {
        
        final SimpleDateFormat formatter = new SimpleDateFormat( Constants.HTTP_COOKIE_DATE_FORMAT );
        
        return Utils.URLEncode(name) + "=" + Utils.URLEncode(value) + "; " +
            "Path=" + path + "; " +
            "Expires=" + formatter.format( expires ) + "; " +
            "Version=1; " +
            "Max-Age=" + MAX_AGE + "; ";
        
    }
    
    /**
     *  returns the cookies name
     * 
     *  @return the name
     * 
     */
    
    public String getName() {
        
        return name;
        
    }

    /**
     *  returns the cookies value
     * 
     *  @return the value
     * 
     */
    
    public String getValue() {
        
        return value;

    }
    
    /**
     *  tests if another cookie is equal to this one.  two cookies are
     *  considered equal if they have the same name
     * 
     *  @param cookie cookie to test
     *  @return true if equal, false otherwise
     * 
     */
    
    public boolean equals( final HttpResponseCookie cookie ) {
        
        return cookie.getName().equals( name );
        
    }
    
}
