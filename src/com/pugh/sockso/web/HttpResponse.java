/*
 * HttpResponse.java
 * 
 * Created on Jul 23, 2007, 4:09:51 PM
 * 
 * Handles sending data via a HTTP response
 * 
 * eg.
 * 
 * HttpResponse res = new HttpResponse( System.out, true );
 * res.enableGzip();
 * res.addHeader( "Content-type", "text/plain" );
 * res.setStatus( 404 );
 * res.sendTemplate( tpl.makeRenderer() );
 * 
 */

package com.pugh.sockso.web;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Utils;
import com.pugh.sockso.Properties;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.resources.Locale;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;

import java.sql.Timestamp;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;

import org.jamon.Renderer;

import org.apache.log4j.Logger;

public class HttpResponse implements Response {
    
    private static final Logger log = Logger.getLogger( HttpResponse.class );
    
    protected static final String HTTP_EOL = "\r\n";
    
    private final Properties p;
    private final Locale locale;
    private final boolean canGzip;
    private final Hashtable<String,String> headers;
    protected final Vector<HttpResponseCookie> cookies;

    private Database db;
    private User user;
    private OutputStream stream;
    private boolean responseSent, useGzip, cookiesEnabled;
    protected int status;

    /**
     *  Creates a new instance of Response.  the canGzip parameter
     *  indicates that the client "can" receive gzipped data, but
     *  this is disabled by default, and needs to be explicitly
     *  enabled via enableGzip() (cause we don't wanna just gzip
     *  everything, images don't need it, so only enable this for
     *  things that'll benefit)
     *
     *  @param stream the output stream
     *  @param canGzip indicates if the client can receive gzip'd data
     * 
     */
    
    public HttpResponse( final OutputStream stream, final Database db, final Properties p, final Locale locale, final User user, final boolean canGzip ) {
        
        this.stream = stream;
        this.db = db;
        this.p = p;
        this.user = user;
        this.locale = locale;
        this.canGzip = canGzip;

        useGzip = false;
        cookiesEnabled = true;
        status = 200;
        headers = new Hashtable<String,String>( 10 );
        cookies = new Vector<HttpResponseCookie>();
        responseSent = false;

        // default headers
        addHeader( "Server", "Sockso" );
        addHeader( "Connection", "close" ); // we don't support keep-alive connections at the moment
        addHeader( "Accept-Ranges", "none" );

    }
 
    /**
     *  Sets a boolean determining if we send cookies with the response
     * 
     *  @param cookiesEnabled
     * 
     */
    
    public void setCookiesEnabled( final boolean cookiesEnabled ) {
        
        this.cookiesEnabled = cookiesEnabled;
        
    }

    /**
     *  enables gzip'ing of response data, this will only be used
     *  though if the client has told us they can handle it
     * 
     */
    
    public void enableGzip() {
        
        useGzip = true;
        
    }
    
    /**
     *  sets the HTTP status code
     * 
     *  @param status status code
     * 
     */
    
    public void setStatus( final int status ) {
        
        this.status = status;
        
    }
    
    /**
     *  sets the current user
     * 
     *  @param user
     * 
     */
    
    public void setUser( final User user ) {
        
        this.user = user;
        
    }
    
    /**
     *  sets the output stream to use
     * 
     *  @param stream
     * 
     */
    
    public void setOutputStream( final OutputStream stream ) {
        
        this.stream = stream;
        
    }
    
    /**
     *  adds a cookie to be sent with the response.  if a cookie
     *  with the same name has already been set then the old
     *  cookie will be replaced
     * 
     *  @param cookie the cookie to add
     * 
     */
    
    public void addCookie( final HttpResponseCookie cookie ) {
        
        cookies.remove( cookie );
        cookies.addElement( cookie );
        
    }
    
    /**
     *  adds a header to be sent to the client (later)
     *
     *  @param name
     *  @param value
     *
     */
    
    public void addHeader( final String name, final String value ) {
        headers.put( name, value );
    }
    
    /**
     *  sends the response headers
     *
     */
    
    public void sendHeaders() {

        final PrintWriter out = new PrintWriter( stream, true );

        out.print( "HTTP/1.0 " +status+ " " +getStatusText(status) + HTTP_EOL );

        checkGzip();
        checkCors();

        for ( final String name : headers.keySet() ) {
            writeHeader( out, name, headers.get(name) );
        }

        if ( cookiesEnabled ) {
            for ( final HttpResponseCookie cookie : cookies ) {
                writeHeader( out, "Set-Cookie", cookie.toString() );
            }
        }

        out.print( HTTP_EOL );
        out.flush();

    }

    /**
     *  Adds gzip header if we are using gzip
     * 
     */

    protected void checkGzip() {

        if ( canGzip && useGzip ) {
            addHeader( "Content-Encoding", "gzip" );
        }

    }

    /**
     *  Adds cross-domain header if it is specified
     * 
     */

    protected void checkCors() {
       
        final String cors = p.get( Constants.WWW_CORS, null );

        if ( cors != null ) {
            addHeader( "Access-Control-Allow-Origin", cors );
        }

    }

    /**
     *  writes a single HTTP header with the specifed name and value
     * 
     *  @param out the stream to write to
     *  @param name the header name
     *  @param value the header value
     * 
     */
    
    private void writeHeader( final PrintWriter out, final String name, final String value ) {
        
        out.print( name + ": " + value + HTTP_EOL );
        
        //log.debug( "Sent HTTP Header: " + name + " = '" + value + "'" );

    }
    
    /**
     *  returns the output stream to use to send data
     *  to the client
     * 
     *  @return the output stream to the client
     * 
     */
    
    public OutputStream getOutputStream() {
        return stream;
    }
    
    /**
     *  shows a html page
     *
     *  @param renderer the template renderer
     * 
     *  @throws IOException 
     * 
     */

    public void showHtml( final Renderer renderer ) throws IOException {
        
        showTemplate( renderer, "text/html; charset=\"UTF-8\"" );
        
    }

    /**
     *  shows a web page
     * 
     *  @param tpl
     * 
     *  @throws java.io.IOException
     *  @throws SQLException
     * 
     */
    
    public void showHtml( final PageTemplate tpl ) throws IOException, SQLException {

        tpl.setRecentUsers( getRecentUsers() );
        tpl.setProperties( p );
        tpl.setLocale( locale );
        tpl.setUser( user );
        
        showHtml( tpl.makeRenderer() );

    }
    
    /**
     *  returns the recent users who have played tracks
     * 
     *  @return
     * 
     */
    
    protected Vector<User> getRecentUsers() throws SQLException {
        
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {

            final Vector<User> users = new Vector<User>();
            final String sql = " select distinct u.id, u.name " +
                               " from play_log l " +
                                   " inner join users u " +
                                   " on u.id = l.user_id " +
                               " where date_played > ? ";
            final long fiveMinutesInMilliseconds = 60 * 5 * 1000;
            final Timestamp fiveMinutesAgo = new Timestamp( new Date().getTime() - fiveMinutesInMilliseconds );

            st = db.prepare( sql );
            st.setTimestamp( 1, fiveMinutesAgo );
            rs = st.executeQuery();

            while ( rs.next() ) {
                users.add( new User(rs.getInt("id"),rs.getString("name")) );
            }
            
            return users;

        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }

    }
    
    /**
     *  sets the database to use
     * 
     *  @param db
     * 
     */
    
    protected void setDatabase( final Database db ) {
        
        this.db = db;
        
    }
    
    /**
     *  shows an rss feed
     * 
     *  @param renderer the feed template renderer
     * 
     *  @throws IOException
     * 
     */
    
    public void showRss( final Renderer renderer ) throws IOException {

        showTemplate( renderer, "text/xml; charset=\"UTF-8\"" );
        
    }

    /**
     *  shows a JSON document
     * 
     *  @param renderer the feed template renderer
     * 
     *  @throws IOException
     * 
     */
    
    public void showJson( final Renderer renderer ) throws IOException {

        showTemplate( 
            new JsonRenderer( renderer ), 
            "application/json; charset=\"UTF-8\"" 
        );
        
    }

    /**
     *  shows a plain text template
     * 
     *  @param renderer
     * 
     *  @throws java.io.IOException
     * 
     */
    
    public void showText( final Renderer renderer ) throws IOException {
       
        showTemplate( renderer, "text/plain" );
        
    }

    /**
     *  Shows some text
     *
     *  @param text
     *
     *  @throws IOException
     *
     */
    
    public void showText( final String text ) throws IOException {

        addHeader( "Content-type", "text/plain" );
        sendHeaders();

        final OutputStreamWriter out = new OutputStreamWriter( stream, "UTF8" );
        
        out.write( text );
        out.flush();

    }
    
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

    public void showTemplate( final Renderer renderer, final String contentType ) throws IOException {

        addHeader( "Content-type", contentType );

        showTemplate( renderer );

    }

    /**
     *  sends a particular template to the user
     * 
     *  @param renderer the template renderer
     * 
     *  @throws IOException
     * 
     */
    
    public void showTemplate( final Renderer renderer ) throws IOException {

        addHeader( "Cache-Control", "private" );
        enableGzip(); // templates are good gzip candidates!
        sendHeaders();

        final boolean doGzip = canGzip && useGzip;
        OutputStream out = doGzip
            ? new GZIPOutputStream(stream)
            : stream;
        
        renderer.renderTo( new OutputStreamWriter(out,"UTF8") );

        if ( doGzip )
            ( (GZIPOutputStream) out ).finish();
        stream.flush();

        responseSent = true;

    }
    
    /**
     *  writes data to the client, no headers are written, you need to make a
     *  call to sendHeaders() yourself.
     * 
     *  @param in stream to read data from
     * 
     *  @throws IOException
     * 
     */
    
    public void sendData( final DataInputStream in ) throws IOException {
        
        DataOutputStream out = null;

        try {
            out = new DataOutputStream( canGzip && useGzip
                ? new GZIPOutputStream(stream)
                : stream
            );
            while ( true )
                out.writeByte( in.readByte() );
        }
        
        catch ( final EOFException e ) {}

        finally {
            Utils.close( out );
        }


    }
    
    /**
     *  redirects the user.  we try and do the redirect with the HTTP Location
     *  header, but incase this fails we also send html with a meta refresh,
     *  and a clickable redirect link.  just incase.
     * 
     *  @param path redirect to
     * 
     */
    
    public void redirect( final String path ) throws IOException {

        setStatus( 302 );
        addHeader( "Location", path );
        sendHeaders();
        
        stream.flush();

        responseSent = true;
        
    }
    
    /**
     *  indicates if the response has been sent
     * 
     *  @return boolean
     * 
     */
    
    public boolean responseSent() {
        
        return responseSent;
        
    }

    /**
     *  returns the correct text for the http status code, or
     *  returns "" if status code is not known
     *
     *  @param code the http status code
     *  @return status code text
     *
     */

    protected String getStatusText( final int code ) {

        final String sCode = "" + code;
        final String[] texts = {
            "200", "Ok",
            "201", "Created",
            "202", "Accepted",
            "203", "Non-Authoritative Information",
            "204", "No Content",
            "205", "Reset Content",
            "206", "Partial Content",
            "300", "Multiple Choices",
            "301", "Moved Permanently",
            "302", "Not Found",
            "303", "See Other",
            "304", "Not Modified",
            "305", "Use Proxy",
            "307", "Temporary Redirect",
            "400", "Bad Request",
            "401", "Unauthorised",
            "402", "Payment Required",
            "403", "Forbidden",
            "404", "Not Found",
            "405", "Method Not Found",
            "406", "Not Acceptable",
            "407", "Proxy Authentication Required",
            "408", "Request Timeout",
            "409", "Conflict",
            "410", "Gone",
            "411", "Length Required",
            "412", "Precondition Failed",
            "413", "Request Entity Too Large",
            "414", "Request URI Too Long",
            "415", "Unsupported Media Type",
            "416", "Requested Range Not Satisfiable",
            "417", "Expectation Failed",
            "500", "Internal Server Error",
            "501", "Not Implemented",
            "502", "Bad Gateway",
            "503", "Service Unavailable",
            "504", "Gateway Timeout",
            "505", "HTTP Version Not Supported"
        };

        for ( int i=0; i<texts.length; i+=2 )
            if ( sCode.equals(texts[i]) )
                return texts[ i + 1 ];

        return "";

    }

}
