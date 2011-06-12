/*
 * HttpRequest.java
 * 
 * Created on Jul 23, 2007, 3:58:31 PM
 * 
 * A HTTP request
 * 
 */

package com.pugh.sockso.web;

import com.pugh.sockso.Utils;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

import java.util.Hashtable;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class HttpRequest implements Request {

    public static final int MAX_HEADERS = 100;

    private final Server server;
    protected final Hashtable<String,String> cookies;
    protected final Hashtable<String,String> arguments;
    private final Hashtable<String,String> headers;
    private final Hashtable<String,UploadFile> files;

    private String[] params = null;
    private String host = null, statusLine = null;

    /**
     *  Reads and processes a HTTP request.  Supports reading cookies,
     *  http headers, accessing the requested URL and reading 
     *  POST data and query strings
     *
     *  @param server the server the request is to
     *  @param stream the input stream for the request
     *
     *  @throws IOException
     *  @throws BadRequestException
     * 
     */
    
    public HttpRequest( final Server server ) {
        
        this.server = server;
        this.cookies = new Hashtable<String,String>();
        this.arguments = new Hashtable<String,String>();
        this.headers = new Hashtable<String,String>();
        this.files = new Hashtable<String,UploadFile>();

    }
    
    /**
     *  processes the request from the given input stream
     * 
     *  @param stream
     * 
     *  @throws java.io.IOException
     *  @throws com.pugh.sockso.web.BadRequestException
     * 
     */
    
    public void process( final InputStream stream ) throws IOException, BadRequestException, EmptyRequestException {
        
        final InputBuffer buffer = new InputBuffer( stream, 100 );

        readStatusLine( buffer.readLine() );
        readHeaders( buffer );
        readBody( buffer );

    }

    /**
     *  reads the status line of the http request
     * 
     *  @param stream input stream
     * 
     *  @throws IOException
     *  @throws BadRequestException
     * 
     */
    
    protected void readStatusLine( final String rawStatusLine ) throws IOException, BadRequestException, EmptyRequestException {

        statusLine = rawStatusLine;

        // look for IE6 "strangeness"
        if ( statusLine.equals("") ) {
            throw new EmptyRequestException();
        }
        
        final Pattern p = Pattern.compile( "(GET|POST) (.+?) HTTP/\\d\\.\\d" );
        final Matcher m = p.matcher( statusLine );

        log.debug( "Status Line: '" + statusLine + "'" );
        
        if ( !m.matches() ) {
            throw new BadRequestException( "Invalid HTTP status line", 400 );
        }

        String resource = m.group( 2 );
        final int queryStringIndex = resource.indexOf( "?" );

        // extract query string from resource if it exists
        if ( queryStringIndex != -1 ) {
            processUrlEncodedData( resource.substring(queryStringIndex+1) );
            resource = resource.substring( 0, queryStringIndex );
        }

        // work out params
        params = resource
                .substring(1)
                .split( "/" );

    }

    /**
     *  read the http headers from the request
     * 
     *  @param stream input stream
     * 
     *  @throws IOException
     * 
     */
    
    private void readHeaders( final InputBuffer buffer ) throws IOException {

        // set limit on how many headers we'll read incase the request is malformed
        int headerCount = 0;
        while ( headerCount++ < MAX_HEADERS ) {

            final String line = buffer.readLine();
            
            if ( line == null || line.equals("") ) break;

            //log.debug( "HTTP Header: " + line );

            final Pattern p2 = Pattern.compile( "(.*?): (.*)" );
            final Matcher m2 = p2.matcher( line );

            if ( !m2.matches() ) continue; // invalid header format, ignore
            
            final String name = m2.group( 1 ).toLowerCase();
            final String value = m2.group( 2 );

            headers.put( name.toLowerCase(), value );

            if ( name.equals("host") )
                host = value;
            else if ( name.equals("cookie") )
                addCookies( value );

        }

    }

    /**
     *  reads the body of the http request for any post
     *  data that may have been sent
     * 
     *  @param in input stream
     * 
     */
    
    private void readBody( final InputBuffer buffer ) throws IOException {

        final int contentLength = getContentLength();

        // work out what type of data we've received, it needs to be processed
        // differently depending on the content type header we got.
        
        if ( getHeader("content-type").contains("multipart/form-data") ) {

            //log.debug( "Multipart Form Data: ~" +contentLength+ " bytes" );

            processMultipartData( buffer );

        }

        else {

            final String postData = buffer.readString( contentLength );
            
            //log.debug( "URL Encoded Post Data: '" +postData+ "'" );

            processUrlEncodedData( postData );

        }
       
    }

    /**
     *  tries to return the content length header provided by the client.  if
     *  it's not present or malformed (not a number) then return -1
     * 
     *  @return content length or -1
     * 
     */
    
    private int getContentLength() {

        try {
            return Integer.parseInt( getHeader("content-length") );
        }
        
        catch ( NumberFormatException e ) {
            // ignore badness, we'll just return -1 next...
        }
        
        return -1;
        
    }
    
    /**
     *  processes data that has been submitted via the multipart/form-data
     *  type, could be uploaded files and stuff...
     * 
     *  @param urlEncData the post data
     * 
     */
    
    private void processMultipartData( final InputBuffer buffer ) throws IOException {

        final String boundary = getMultipartBoundary();
        final String startMarker = "--" +boundary;
        final String endMarker = startMarker+ "--";

        while ( true ) {

            final String marker = buffer.readLine();
            
            // have we reached the end?
            if ( marker.equals(endMarker) ) {
                return;
            }
            
            else if ( marker.equals(startMarker) ) {
                
                final MultipartSection ms = new MultipartSection();
                
                ms.process( buffer, boundary );
                
                // if we've managed to extract a filename then we'll treat
                // this section as a file upload...
                if ( !ms.getFilename().equals("") ) {
                    //log.debug( "Multipart File: " + ms.getFilename() );
                    files.put( ms.getName(), new UploadFile(
                        ms.getFilename(), ms.getContentType(), ms.getData(),
                        ms.getFilename(), ms.getTemporaryFile()
                    ));
                }
                
                // else treat as normal argument
                else {
                    //log.debug( "Multipart Arg: " + ms.getName() + " = " + ms.getData() );
                    arguments.put( ms.getName(), ms.getData() );
                }
                
            }
            
            // in case no more data...
            else if ( marker.equals("") ) {
                break;
            }
            
        }
        
    }
    
    /**
     *  extracts the boundary being used to seperate data in multipart
     *  type, returns null if it can't be found
     * 
     *  @return boundary if found, null otherwise
     * 
     */
    
    protected String getMultipartBoundary() {
        
        final String contentType = getHeader( "content-type" );
        final Pattern pattern = Pattern.compile( ".*boundary=(.*)" );
        final Matcher matcher = pattern.matcher( contentType );
                
        return ( matcher.matches() )
                ? matcher.group( 1 )
                : null;
        
    }
    
    /**
     *  process request data that is in standard url encoded form
     * 
     *  @param urlEncData the post data
     * 
     */
    
    private void processUrlEncodedData( final String data ) {           
        
        // extract arguments from post data
        final String[] pairs = data.split( "&" );

        for ( final String pairData : pairs ) {
            
            final String[] pair = pairData.split( "=" );
            
            if ( pair.length == 2 ) {
                final String name = Utils.URLDecode( pair[0] );
                final String value = Utils.URLDecode( pair[1] );
                //log.debug( "URL Encoded Argument: " + name + "=" + value );
                arguments.put( name, value );
            }
            
        }

    }
    
    /**
     *  returns the http status line
     * 
     *  @return status line
     * 
     */
    
    public String getResource() {
        
        return statusLine;
        
    }
    
    /**
     *  returns the value of a named HTTP header, if the header
     *  was not in the request the empty string is returned
     * 
     *  NB: case-insensitive
     * 
     *  @param name name of the http header
     *  @param header value, or null
     * 
     */
    
    public String getHeader( final String name ) {
        
        final String value = headers.get( name.toLowerCase() );
        
        return value == null ? "" : value;
        
    }
    
    /**
     *  tries to return a named argument that was received from
     *  the request data (POST)
     * 
     *  @param name argument name
     *  @return string value
     * 
     */
    
    public String getArgument( final String name ) {
        
        final String value = arguments.get( name );
        
        return value == null ? "" : value;
        
    }
    
    /**
     *  indicates if an arugment was present
     * 
     */
    
    public boolean hasArgument( final String name ) {
        
        return !getArgument(name).equals("");

    }
    
    /**
     *  returns a file that has been uploaded in the request by the
     *  name it was given in the form.  if the file isn't found then
     *  returns null
     * 
     *  @param name file parameter name
     *  @return uploaded file, or null if not found
     * 
     */
    
    public UploadFile getFile( final String name ) {

       return files.get( name );

    }
    
    /**
     *  adds some cookies to the cookies in the request.  the cookie
     *  data is URL decoded (this isn't a standard, just a reccomendation,
     *  i read the RFC and couldn't find any mention for how they should be encoded)
     * 
     *  @param cookie the cookie to add
     * 
     */
    
    protected void addCookies( final String cookieData ) {
        
        final String[] pairs = cookieData.split( ";" );
        
        for ( final String pairData : pairs ) {
            
            final String[] pair = pairData.split( "=" );
            
            if ( pair.length == 2 ) {
                final String name = Utils.URLDecode( pair[0] ).trim();
                final String value = Utils.URLDecode( pair[1] ).trim();
                cookies.put( name, value );
                //log.debug( "HttpRequestCookie: " + name + "=" + value );
            }
            
        }

    }
    
    /**
     *  return a named cookie
     * 
     *  @return value of cookie
     * 
     */
    
    public String getCookie( final String name ) {
        
        final String value = cookies.get( name );
        
        return value == null ? "" : value;
        
    }
    
    /**
     *  returns the host to use for this request when replying.  hopefully
     *  the user sent us something we can use, otherwise we'll hafta try
     *  and guess...
     * 
     *  @return the host to reply with
     * 
     */

    public String getHost() {

        return host != null
            ? host
            : server.getHost();

    }
    
    /**
     *  returns the parameter at the specified index
     * 
     *  @param index the index of the parameter to fetch
     *  @return the parameter value
     * 
     */ 

    public String getUrlParam( final int index ) {
        return ( index < params.length )
            ? Utils.URLDecode(params[ index ])
            : "";
    }
    
    /**
     *  returns the number of parameters in the request
     * 
     *  @return parameter count
     * 
     */
    
    public int getParamCount() {
       return params.length; 
    }
    
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
    
    public String[] getPlayParams( final boolean skipFirstArg ) {
        return getPlayParams( skipFirstArg ? 1 : 0 );
    }
        
    public String[] getPlayParams( final int skipNumArgs ) {
        
        final int offset = 1 + skipNumArgs;
        
        final String[] custArgs = new String[ params.length - offset ];
        System.arraycopy( params, offset, custArgs, 0, params.length - offset );

        return custArgs;
        
    }

    /**
     *  returns users preferred 2 char lang code from "Accept-Language", if
     *  nothing is found then the empty string is returned.
     * 
     *  @todo give respect to weightings
     *
     *  @return 2 char lang code or ""
     * 
     */

    public String getPreferredLangCode() {
        
        final String langs = getHeader( "Accept-Language" );
        final String[] locales = langs.split( "," ); // split apart lang/locale and weightings

        String lang = "";
        
        if ( locales.length > 0 ) {
            final String[] weightingPair = locales[0].split( ";" ); // split off possible weighting "q=0.5"
            if ( weightingPair.length > 0 ) {
                final String[] langPair = weightingPair[0].split( "-" ); // split off locale "eg-GB"
                if ( langPair.length > 0 )
                    lang = langPair[ 0 ];
            }
        }

        return lang;
        
    }

    /**
     *  when we're done with the request we should delete any temporary files
     *  that may have been created.
     * 
     *  @throws java.lang.Throwable
     * 
     */
    
    @Override
    protected void finalize() throws Throwable {
        
        for ( final String key : files.keySet() ) {
            
            final UploadFile file = getFile( key );
            final File tempFile = file.getTemporaryFile();
            
            if ( tempFile != null ) {
                tempFile.delete();
            }
            
        }
        
    }

}
