/*
 * MultipartSection.java
 * 
 * Created on Nov 18, 2007, 12:28:56 PM
 * 
 * Parses a multipart/form-data section and then allows access
 * to the data it contains.
 *
 */

package com.pugh.sockso.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.DataOutput;
import java.io.DataOutputStream;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

public class MultipartSection {

    private static final Logger log = Logger.getLogger( MultipartSection.class );
    
    private String name, filename, contentType, data;
    private File temporaryFile;

    /**
     *  creates a new MultipartSection
     * 
     *  @throws java.io.IOException
     * 
     */
    
    public MultipartSection() throws IOException {

        name = "";
        filename = "";
        contentType = "text/plain";
        temporaryFile = File.createTempFile( "sockso-upload-" +Math.random(), "dat" );

    }
    
    /**
     *  takes a string containing a multipart section and
     *  parses it for all it's info
     * 
     *  @param buffer
     *  @param boundary
     * 
     *  @throws IOException
     * 
     */
    
    public void process( final InputBuffer buffer, final String boundary ) throws IOException {

        // parse headers so we know what we're dealing with
        
        parseHeaders( buffer );

        final boolean writeToFile = !filename.equals( "" );
        final DataOutput out = writeToFile
                               ? new DataOutputStream( new FileOutputStream(temporaryFile) )
                               : new StringOutputStream();

        // parse data
        
        parseData( buffer, boundary, out );
        
        // transform data if needed
        
        if ( !writeToFile ) {
            data = ( (StringOutputStream) out ).toString();
        }

    }
    
    /**
     *  writes data from the input stream to a temporary file
     * 
     *  @param buffer
     *  @param boundary
     *  @param out
     * 
     *  @throws IOException
     * 
     */
    
    protected void parseData( final InputBuffer buffer, final String boundary, final DataOutput out ) throws IOException {
        
        final String terminator = HttpResponse.HTTP_EOL + "--" +boundary;

        // read through data
        
        final int length = terminator.length();
        final int firstChar = terminator.charAt( 0 );
        final int[] term = new int[ length ];

        for ( int i=0; i<length; i++ ) {
            term[ i ] = terminator.charAt( i );
        }

        // read through
        matchFound:
        while ( true ) {

            final int c = buffer.read();

            if ( c == -1 ) break;

            // if we've matched the first char, lets see if we can match
            // the whole of the terminator string
            if ( c == firstChar ) {

                for ( int i=1; i<length; i++ ) {

                    final int d = buffer.readDirectly();
                    
                    buffer.putBack( d );
                    
                    // if this char doesn't match, fail...
                    if ( (char) d != term[i] ) {
                        break;
                    }

                    // if we've gone along the whole string matching we've
                    // found what we're looking for!
                    if ( i == length - 1 ) {
                        break matchFound;
                    }

                }

            }

            out.writeByte( c );

        }
        
        buffer.skip( 1 );
        
    }
    
    /**
     *  parses the chunk of headers and extracts the info to
     *  populate ourself
     * 
     *  @param headerInfo multiline header string
     * 
     */
    
    protected void parseHeaders( final InputBuffer buffer ) throws IOException {

        while ( true ) {

            final String header = buffer.readLine();

            if ( header == null || header.equals("") ) {
                break;
            }

            // extract header name and data, we then have to get the data
            // out of each header, which is stored kinda ugly i think, so
            // it's a pain in the arse to get it out!!  too many literals...
            final Pattern p = Pattern.compile( "(.*?): (.*)" );
            final Matcher m = p.matcher( header );

            if ( m.matches() ) {

                final String headerName = m.group( 1 ).toLowerCase();
                final String headerData = m.group( 2 );

                // content-type
                if ( headerName.equals("content-type") ) {
                    contentType = headerData;
                }

                // content-disposition
                else if ( headerName.equals("content-disposition") ) {
                    processContentDisposition( headerData );
                }

            }

        }

    }

    /**
     *  processes a "Content-disposition" header
     * 
     *  @param headerData
     * 
     */
    
    protected void processContentDisposition( final String headerData ) {
        
        for ( final String pairData : headerData.split(";") ) {

            final String parts[] = pairData.split( "=" );
            if ( parts.length == 2 ) {

                final String pairName = parts[ 0 ].trim();
                // remove enclosing quotes from value
                final String pairValue = parts[ 1 ].substring( 1, parts[1].length()-1 );

                if ( pairName.equals("name") )
                    name = pairValue;
                else if ( pairName.equals("filename") )
                    filename = pairValue;

            }
            
        }

    }
    
    public String getData() { return data; }
    public String getName() { return name; }
    public String getFilename() { return filename; }
    public File getTemporaryFile() { return temporaryFile; }
    public String getContentType() { return contentType; }
    
    @Override
    public String toString() {
        return "(MultipartSection) " +
            "Name: '" +name+ "'; " +
            "Filename: '" +filename+ "'; " +
            "content-type: '" +contentType+ "';";
    }
    
}
