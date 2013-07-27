
package com.pugh.sockso.tests;

import com.pugh.sockso.Main;
import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

import junit.framework.TestCase;

public class SocksoTestCase extends TestCase {

    static { Main.initTestLogger(); }

    /**
     *  Asserts that the size of a table is as expected
     * 
     *  @param db
     *  @param table
     *  @param size
     * 
     *  @throws java.sql.SQLException
     * 
     */

    public void assertTableSize( final Database db, final String table, final int size ) throws SQLException {

        ResultSet rs = null;
        Statement st = null;

        try {
            st = db.getConnection().createStatement();
            rs = st.executeQuery(" select count(*) as total from " +table );

            assertTrue( rs.next() );
            assertEquals( size, rs.getInt("total") );

        }

        finally {
            Utils.close( rs );
            Utils.close( st );
        }

    }

    /**
     *  Asserts a row exists in the table with the specified value
     * 
     *  @param db
     *  @param table
     *  @param column
     *  @param value
     * 
     */

    public void assertRowExists( final Database db, final String table, final String column, final String value ) throws SQLException {

        assertRowCount( db, table, column, value, 1 );

    }

    /**
     *  Asserts that a row doesn't exist with the specified value
     *
     *  @param db
     *  @param table
     *  @param column
     *  @param value
     *
     *  @throws java.sql.SQLException
     *
     */

    public void assertRowDoesntExist( final Database db, final String table, final String column, final String value ) throws SQLException {

        assertRowCount( db, table, column, value, 0 );

    }

    /**
     *  Asserts that a specified number of rows exist for a specific value
     * 
     *  @param db
     *  @param table
     *  @param column
     *  @param value
     *  @param rowCount
     * 
     *  @throws java.sql.SQLException
     * 
     */

    private void assertRowCount( final Database db, final String table, final String column, final String value, final int rowCount ) throws SQLException {

        final int total = getRowCount( db, table, column, value );

        if ( total < rowCount ) {
            fail( "Not enough rows found for " +table+ "." +column+ " = '" +value+ "' " );
        }

        if ( total > rowCount ) {
            fail( "Too many rows found for " +table+ "." +column+ " = '" +value+ "' " );
        }

    }

    /**
     *  Returns the number of rows that match a specific value
     * 
     *  @param db
     *  @param table
     *  @param column
     *  @param value
     * 
     *  @return
     * 
     *  @throws java.sql.SQLException
     * 
     */

    private int getRowCount( final Database db, final String table, final String column, final String value ) throws SQLException {

        ResultSet rs = null;
        Statement st = null;

        try {

            final String columnValue = value.matches( "^\\d+$" )
                                                ? value
                                                : "'" +value+ "'";
            final String sql = " select count(*) as total " +
                               " from " +table+ " " +
                               " where " +column+ " = " +columnValue+ " ";
            st = db.getConnection().createStatement();
            rs = st.executeQuery(sql );
            
            if ( !rs.next() ) {
                fail( "Query failed: " + sql );
            }

            return rs.getInt( "total" );

        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
    }

    /**
     *  Asserts that one string contains another
     *
     *  @param haystack
     *  @param needle
     *
     */

    protected void assertContains( final String haystack, final String needle ) {

        if ( !haystack.contains(needle) ) {
            fail( "'" +haystack+ "' does not contain the string '" +needle+ "'" );
        }

    }

    /**
     *  Asserts that one string does not contain another
     *
     *  @param haystack
     *  @param needle
     *
     */
    
    protected void assertNotContains( final String haystack, final String needle ) {

        if ( haystack.contains(needle) ) {
            fail( "'" +haystack+ "' contains the string '" +needle+ "'" );
        }

    }
    
    /**
     *  Asserts the specified file contains the specified string
     * 
     *  @param file
     *  @param needle 
     * 
     */
    
    protected void assertFileContains( final File file, final String needle ) throws IOException {

        final String data = readFile( file );
        
        if ( !data.contains(needle) ) {
            fail( "File '" +file.getName()+ "' does not contain '" +needle+ "'" );
        }
        
    }
    
    /**
     *  Read a file and return its data as a String
     * 
     *  @param file
     * 
     *  @return
     * 
     *  @throws IOException 
     * 
     */
    
    protected String readFile( final File file ) throws IOException {
        
        final StringBuffer data = new StringBuffer();
            
        BufferedReader in = null;
        String line = null;
        
        try {
        
            in = new BufferedReader(
                new InputStreamReader( new FileInputStream(file) )
            );

            while ( (line = in.readLine()) != null ) {
                data.append( line );
            }
            
            return data.toString();
            
        }
        
        finally {
            Utils.close( in );
        }

    }
    
    /**
     *  Asserts that two files are not identical
     * 
     *  @todo it would be quicker to read through both files byte by byte and
     *  stop and the first mismatch, but this will do for now unless speed
     *  issues crop up.
     * 
     *  @param file1
     *  @param file2
     * 
     *  @throws IOException 
     * 
     */
    
    protected void assertFilesNotEqual( final File file1, final File file2 ) throws IOException {
        
        final String data1 = readFile( file1 );
        final String data2 = readFile( file2 );
                
        if ( data1.equals(data2) ) {
            fail( "File '" +file1.getName()+ "' is the same as '" +file2.getName()+ "'" );
        }
        
    }
    
    /**
     *  Creates and returns a new request object to the specified url as a GET
     * 
     *  @param url
     * 
     *  @return
     * 
     */
    
    protected TestRequest getRequest( final String url ) {
        
        return new TestRequest( "GET " +url+ " HTTP/1.1" );

    }
    
    /**
     *  Asserts a string ends with the specified string
     * 
     *  @param str 
     *  @param substring
     * 
     */
    
    protected void assertEndsWith( final String str, final String substring ) {
        
        final String endString = str.substring( str.length() - substring.length() );
        
        if ( !endString.equals(substring) ) {
            fail( "Expected string to end with '" +substring+ "'" );
        }
        
    }
    
    /**
     *  Asserts a string contains the specified number of occurances of the substring
     * 
     *  @param expected
     *  @param str
     *  @param substr 
     * 
     */
    
    protected void assertSubstringCount( final int expected, final String str, final String substr ) {
        
        int position = 0;
        int total = 0;
        
        while ( (position = str.indexOf( substr, position )) != -1 ) {
            position += substr.length();
            total++;
        }
        
        if ( total != expected ) {
            fail( "Expctected to find " +expected+ " occurances of '" +substr+ "', but found " +total );
        }
        
    }

}
