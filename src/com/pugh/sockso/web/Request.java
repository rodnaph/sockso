/**
 * Request.java
 *
 * Created on May 8, 2007, 12:38 PM
 *
 * Handles processing and access to the request information
 * 
 */

package com.pugh.sockso.web;

import java.io.InputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

public interface Request {
    
    public static final Logger log = Logger.getLogger( Request.class );
    
    /**
     *  process the request data from the input stream
     * 
     *  @param stream
     * 
     */
    
    public void process( final InputStream stream ) throws IOException, BadRequestException, EmptyRequestException;
    
    /**
     *  returns a description of the requested resource, ie. the HTTP status line
     * 
     *  @return http status line
     * 
     */
    
    public String getResource();
    
    /**
     *  returns the value of the named HTTP header, or the
     *  empty string if it wasn't sent  (case-insensitive)
     * 
     *  @param name header name
     *  @return header value
     * 
     */
    
    public String getHeader( final String name );
    
    /**
     *  returns the value of the named cookie.  if the cookie
     *  doesn't exist then the empty string will be returned
     * 
     *  @return value of cookie, or empty string
     * 
     */
    
    public String getCookie( final String name );
    
    /**
     *  returns the host to use for this request when replying.  hopefully
     *  the user sent us something we can use, otherwise we'll hafta try
     *  and guess...
     * 
     *  @return the host to reply with
     * 
     */

    public String getHost();
    
    /**
     *  returns the parameter at the specified index (starting at 0)
     * 
     *  @param index the index of the parameter to fetch
     * 
     *  @return the parameter value
     * 
     */ 

    public String getUrlParam( final int index );
    
    /**
     *  returns the number of parameters in the request
     * 
     *  @return parameter count
     * 
     */
    
    public int getParamCount();
    
    /**
     *  strips the initial command arguments from a string that should
     *  then only contain custom url arguments (eg. "tr123/ar456")
     * 
     *  the skip argument can be used if the url is "/command/type/ARGS",
     *  rather than "/command/ARGS".
     * 
     *  @param skipFirstArg skip an argument in list
     *  @return the array with just the custom args
     * 
     */
    
    public String[] getPlayParams( final boolean skipFirstArg );

    /**
     *  returns an array of playlist args, but skips the specified
     *  number of arguments at the start of the URL
     * 
     *  @param skipNumArgs number of args to skip
     *  @return array of play args
     * 
     */
    
    public String[] getPlayParams( final int skipNumArgs );
    
    /**
     *  returns a named parameter from the arguments passed via http (GET and POST)
     *  POST params take precedence over any matching GET params)
     * 
     *  @param name name of the argument
     *  @return string value
     * 
     */
    
    public String getArgument( final String name );
    
    /**
     *  indicates if an argument was in the request, ie. it was present and has
     *  a non zero length value, it's not an empty string.
     * 
     *  @param name the parameter to check
     * 
     *  @return true if argument has a value, false otherwise
     * 
     */
    
    public boolean hasArgument( final String name );
    
    /**
     *  returns a named file that the user has uploaded (where the name
     *  is the files name from the HTML form.  returns null if it doesn't
     *  exist.
     * 
     *  @return file if found, null otherwise
     * 
     */
    
    public UploadFile getFile( final String name );
    
    /**
     *  returns the users preferred language as stated by the
     *  Accept-Language HTTP header.  this should be the 2 character
     *  language code, and not also the locale code (which isn't
     *  always supported like in IE6)
     * 
     *  @return 2 char lang code
     * 
     */
    
    public String getPreferredLangCode();
    
}
