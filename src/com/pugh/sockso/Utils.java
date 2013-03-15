/*
 *  Utils.java
 * 
 *  Created on May 19, 2007, 10:19:18 PM
 * 
 *  this class provides a number of static helper methods
 * 
 */

package com.pugh.sockso;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.web.BadRequestException;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final Logger log = Logger.getLogger( Utils.class );

    private static String APPLICATION_DIRECTORY;

    static {
         setApplicationDirectory( new File(System.getProperty("user.home") + File.separator + ".sockso") );
    }
    
    /**
     *  sets the application directory to use (this should probably ONLY be
     *  done on startup, doing so after this could be bad...)
     * 
     *  @param directory
     * 
     */
    
    public static void setApplicationDirectory( final File directory ) {
        APPLICATION_DIRECTORY = directory.getAbsolutePath();
    }
    
    /**
     *  returns the absolute path to the application directory
     * 
     *  @return
     * 
     */
    
    public static String getApplicationDirectory() {
        return APPLICATION_DIRECTORY;
    }
    
    /**
     *  returns the absolute path to the covers directory
     * 
     *  @return
     * 
     */
    
    public static String getCoversDirectory() {
        return APPLICATION_DIRECTORY + File.separator + Constants.COVERS_DIR;
    }
    
    /**
     *  returns the extension for a file in lowercase
     * 
     *  @param file the file to get the extension for
     *  @return the file's extension
     * 
     */
    
    public static String getExt( final File file ) {
        return getExt( file.getName() );
    }
    
    /**
     *  returns the extension for a filename in lowercase
     * 
     *  @param name the name/path of the file to get the extension for
     *  @return the file's extension
     * 
     */
    
    public static String getExt( final String name ) {
        return name.substring( name.lastIndexOf(".") + 1 ).toLowerCase();
    }

    /**
     *  returns a random string of characters (A-Z) of the specified length
     * 
     *  @param length the length of the string
     *  @return random string
     * 
     */
    
    public static String getRandomString( final int length ) {
        
        final StringBuffer sb = new StringBuffer();
        final Random rand = new Random();

        for ( int i=0; i<length; i++ )
            sb.append( (char) ((Math.abs(rand.nextInt()) % 26) + 65) );

        return sb.toString();

    }

    /**
     *  Close a Writer impl catching any errors
     *
     *  @param out
     *
     */
    
    public static void close( final OutputStream out ) {
        
        if ( out != null ) {
            try { out.close(); }
            catch ( final Exception e ) {
                log.error( "Error closing output stream: " +e.getMessage() );
            }
        }
        
    }

    /**
     *  closes an sql statement and handles any errors that may occur
     * 
     *  @param s the statement to close
     * 
     */
    
    public static void close( final Statement s ) {
        
        if ( s == null ) return;
        
        try { s.close(); }
        catch ( final Exception e ) {
            log.error( "Error closing statement: " + e.getMessage() );
        }
        
    }

    /**
     *  closes an sql result set and handles any errors that may occur
     * 
     *  @param rs the result set to close
     * 
     */
    
    public static void close( final ResultSet rs ) {

        if ( rs == null ) return;
        
        try { rs.close(); }
        catch ( final Exception e ) {
            e.printStackTrace();
            log.error( "Error closing result set: " + e.getMessage() );
        }
        
    }

    /**
     *  closes an input stream and handles any errors that may occur
     * 
     *  @param s the input stream to close
     * 
     */
    
    public static void close( final InputStream s ) {
        
        if ( s == null ) return;
        
        try { s.close(); }
        catch ( final Exception e ) {
            log.error( "Error closing input stream: " + e.getMessage() );
        }
        
    }

    /**
     *  closes an output stream and handles any errors that may occur
     * 
     *  @param s the input stream to close
     * 
     */
    
    public static void close( final Writer s ) {
        
        if ( s == null ) return;
        
        try { s.close(); }
        catch ( final Exception e ) {
            log.error( "Error closing output stream: " + e.getMessage() );
        }

    }

    /**
     *  closes a buffered reader and handles any errors that may occur
     * 
     *  @param s the buffered reader to close
     * 
     */
    
    public static void close( final BufferedReader s ) {
        
        if ( s == null ) return;
        
        try { s.close(); }
        catch ( final Exception e ) {
            log.error( "Error closing buffered reader: " + e.getMessage() );
        }

    }

    /**
     *  url encodes a string, if an error is encountered then
     *  you'll get the empty string back
     * 
     *  @TODO - refactor into WebObject class?
     * 
     *  @param string the string to encode
     *  @return the encoded string
     * 
     */
    
    public static String URLEncode( final String string ) {
        
        try {
            return URLEncoder.encode( string, Constants.URL_CHAR_ENCODING );
        }
        
        catch ( final UnsupportedEncodingException e ) {
            log.error( e.getMessage() );
        }
        
        return "";

    }

    /**
     *  decodes an url encoded string, if an error is encountered then
     *  you'll get the empty string back
     *
     *  @TODO - refactor into WebObject class?
     *
     *  @param string the string to decode
     *  @return the decoded string
     * 
     */
    
    public static String URLDecode( final String string ) {
        
        try {
            return URLDecoder.decode( string, Constants.URL_CHAR_ENCODING );
        }
        
        catch ( final UnsupportedEncodingException e ) {
            log.error( e.getMessage() );
        }
        
        return "";

    }

    /**
     *  tries to get the local ip address
     * 
     *  @TODO - static callout smells of badness...
     * 
     *  @return local ip
     * 
     *  @throws UnknownHostException
     * 
     */
    
    public static String getLocalIp() throws UnknownHostException {

        return InetAddress.getLocalHost().getHostAddress();

    }

    /**
     *  creates an md5 hash of a string
     *
     *  @param text the string to hash
     *  @return hash
     *
     */

    public static String md5( final String text ) {
        try {
            final MessageDigest md;
            md = MessageDigest.getInstance("MD5");
            byte[] md5hash = new byte[32];
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            md5hash = md.digest();
            return convertToHex(md5hash);
        }
        catch ( final Exception e ) {}
        return null;
    }

    private static String convertToHex(byte[] data) {
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }

    /**
     *  formats a date object to a nice format
     * 
     *  @param date the date to format
     *  @return a nicely formatted date
     * 
     */
    
    public static String formatDate( final Date date ) {

        final SimpleDateFormat formatter = new SimpleDateFormat( "EEE MMM dd, yyyy" ); 

        return formatter.format( date );

    }
    
    /**
     *  returns the path to the uploads directory if it is set, "" otherwise
     * 
     *  @return path to directory, "" if not set
     * 
     */
    
    public static String getUploadsPath( final Database db, final Properties p ) {
        
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
       
            final String strCollId = p.get( Constants.WWW_UPLOADS_COLLECTION_ID ).trim();
            
            if ( strCollId.matches("^\\d+$") ) {

                final int uploadsCollId = Integer.parseInt( strCollId );
                final String sql = " select path " +
                                   " from collection " +
                                   " where id = ? " +
                                   " limit 1 ";
                
                st = db.prepare( sql );
                st.setInt( 1, uploadsCollId );
                rs = st.executeQuery();

                if ( rs.next() )
                    return rs.getString( "path" );
                
            }

        }
        
        catch ( final SQLException e ) {
            log.error( e );
        }
        
        catch ( final NumberFormatException e ) {
            log.debug( "No valid uploads collection ID defined: " +e );
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
        return "";
        
    }
    
    /**
     *  encodes a value for safe use in XML
     * 
     *  @param value
     *  @return
     * 
     */
    
    public static String XMLEncode( final String value ) {
       
        String newValue = value.toString();
        final String[] reps = new String[] {
            "&", "&amp;",
            "<", "&lt;",
            ">", "&gt;",
            "\"", "&quot;",
            "'", "&apos;"
        };
        
        for ( int i=0; i<reps.length; i+=2 )
            newValue = newValue.replace( reps[i], reps[i+1] );
        
        return newValue;
        
    }
    
    /**
     *  returns a path with the current systems trailing slash at the end
     * 
     *  @param file
     * 
     *  @return path with trailing slash
     * 
     */
    
    public static String getPathWithSlash( final File file ) {
        
        return getPathWithSlash( file.getAbsolutePath() );
        
    }
    
    /**
     *  returns a path with the current systems trailing slash at the end
     * 
     *  @param path
     * 
     *  @return path with trailing slash
     * 
     */
    
    public static String getPathWithSlash( final String path ) {

        final String separator = System.getProperty( "file.separator" );

        return getPathWithSlash( path, separator );
        
    }
 
    /**
     *  returns a path with the current systems trailing slash at the end,
     *  using the separator as the one for this system.
     * 
     *  NB! This is here to aid unit testing, you should let the other functions
     *  work out the system path separator and stuff...
     * 
     *  @param path
     *  @param separator
     * 
     *  @return path with trailing slash
     * 
     */
    
    protected static String getPathWithSlash( final String path, final String separator ) {

        return ( !path.matches(".*\\" +separator+ "$") )
            ? path + separator
            : path;

    }

    /**
     *  escapes a javascript string for use in a literal (ie. escapes single
     *  quotes and backslashes in the string)
     * 
     *  @param str
     *  @return
     * 
     */
    
    public static String escapeJs( final String str ) {
        
        return str
            .replaceAll( "\\\\", "\\\\\\\\" )   // 1. backslashes
            .replaceAll("'","\\\\'");           // 2. single quotes

    }
    
    /**
     *  checks that a property is set, marking a feature as being enabled
     * 
     *  @param p Properties object
     *  @param property feature enabling property
     * 
     *  @throws BadRequestException
     * 
     */
    
    public static void checkFeatureEnabled( final Properties p, final String property ) throws BadRequestException {

        if ( !isFeatureEnabled(p,property) )
            throw new BadRequestException( "feature not enabled", 403 );

    }
 
    /**
     *  checks if a feature is enabled, returns true if it is, false otherwise
     * 
     *  @param p
     *  @param property
     * 
     *  @return boolean
     * 
     */
    
    public static boolean isFeatureEnabled( final Properties p, final String property ) {

        return p.get( property ).equals( Properties.YES );

    }
    
    /**
     *  a method to do a CASE INSENSITIVE regex search-replace
     * 
     *  @param regex
     *  @param replaceWith
     *  @param subject
     * 
     */
    
    public static String replaceAll( final String regex, final String replaceWith, final String subject ) {
        
        final Pattern p = Pattern.compile( regex, Pattern.CASE_INSENSITIVE );
        final Matcher m = p.matcher( subject );
        
        return m.replaceAll( replaceWith );
        
    }
    
    /**
     *  u2e = unicode to entities
     * 
     *  converts any unicode characters in a string to their numeric entity
     * 
     */
    
    public static String u2e( final String orig ) {
    
        final int length = orig.length();
        final StringBuffer buffer = new StringBuffer( length * 2 );
        
        for ( int i = 0; i < length; i++ ) {
            char c = orig.charAt(i);
            int code = c;
            buffer.append( code < 0x80
                ? c
                : "&#" +( (int) c )+';'
            );
        }

        return buffer.toString();
        
    }
    
    /**
     *  Takes an array of strings and joins them using the specified glue.
     * 
     *  @param array
     *  @param glue
     *  @param start
     *  @param end
     * 
     *  @return
     * 
     */

    public static String joinArray( final String[] array, final String glue, final int start, final int end ) {
        
        final StringBuffer sb = new StringBuffer();

        for ( int i=start; i<=end; i++ ) {
            if ( i > start ) {
                sb.append( glue );
            }
            sb.append( array[i] );
        }

        return sb.toString();
        
    }

}
