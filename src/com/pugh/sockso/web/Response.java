/**
 * Response.java
 *
 * Created on May 9, 2007, 12:39 PM
 * 
 * handles sending the response to the client.  you can optionally
 * set that the data should be gzipped (aslong as the client
 * has told us they support this)
 *
 */

package com.pugh.sockso.web;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.OutputStream;

import java.sql.SQLException;

import org.jamon.Renderer;

public interface Response {
 
    /**
     *  sets the http status code to return
     * 
     *  @param status http status code
     * 
     */
    
    public void setStatus( final int status );
    
    /**
     *  sets the current user
     * 
     *  @param user
     * 
     */
    
    public void setUser( final User user );
    
    /**
     *  adds a cookie to the response
     * 
     *  @param cookie the cookie to add
     * 
     */
    
    public void addCookie( final HttpResponseCookie cookie );
    
    /**
     *  adds a header to be sent to the client (later)
     *
     *  @param name
     *  @param value
     *
     */
    
    public void addHeader( final String name, final String value );
    
    /**
     *  sends the response headers
     *
     */
    
    public void sendHeaders();

    /**
     *  returns the output stream to use to send data
     *  to the client
     * 
     *  @return the output stream to the client
     * 
     */
    
    public OutputStream getOutputStream();
    
    /**
     *  shows a html page
     *
     *  @param renderer the template renderer
     * 
     *  @throws IOException 
     * 
     */

    public void showHtml( final Renderer renderer ) throws IOException;

    /**
     *  show a html page for the web
     * 
     *  @param tpl
     * 
     *  @throws java.io.IOException
     * 
     */
    
    public void showHtml( final PageTemplate tpl ) throws IOException, SQLException;

    /**
     *  shows an rss feed
     * 
     *  @param renderer the feed template renderer
     * 
     *  @throws IOException
     * 
     */
    
    public void showRss( final Renderer renderer ) throws IOException;

    /**
     *  shows a JSON document
     * 
     *  @param renderer the feed template renderer
     * 
     *  @throws IOException
     * 
     */
    
    public void showJson( final Renderer renderer ) throws IOException;

    /**
     *  shows a plain text template to be downloaded by filename
     * 
     *  @param renderer
     * 
     *  @throws java.io.IOException
     * 
     */
    
    public void showText( final Renderer renderer ) throws IOException;
    
    /**
     *  shows some plain text
     *
     *  @param text
     * 
     *  @throws java.io.IOException
     *
     */

    public void showText( final String text ) throws IOException;

    /**
     *  sets the content type header and sends headers, then sends
     *  the template which this renderer is for to the client
     * 
     *  @param renderer the template renderer
     *  @param contentType the content type header
     * 
     *  @throws IOException
     * 
     */

    public void showTemplate( final Renderer renderer, final String contentType ) throws IOException;

    /**
     *  sends a particular template to the user, no headers
     *  are written, just the template
     * 
     *  @param renderer the template renderer
     * 
     *  @throws IOException
     * 
     */
    
    public void showTemplate( final Renderer renderer ) throws IOException;
    
    /**
     *  redirects the user to the given path
     * 
     *  @throws IOException
     * 
     */
    
    public void redirect( final String path ) throws IOException;

    /**
     *  indicates if the response has been sent
     * 
     *  @return boolean true if it has, false otherwise
     * 
     */
    
    public boolean responseSent();
 
    /**
     *  sends response data read from the input stream
     * 
     *  @param in stream to read data from
     *
     *  @throws IOException
     * 
     */
    
    public void sendData( final DataInputStream in ) throws IOException;
    
    /**
     *  indicates that gzip compression should be used if possible
     * 
     */
    
    public void enableGzip();

    /**
     *  Sets whether cookies are enabled or not (by default they should be)
     *
     *  @param cookiesEnabled
     *
     */
    public void setCookiesEnabled( final boolean cookiesEnabled );
    
}
