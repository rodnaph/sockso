
package com.pugh.sockso.tests;

import com.pugh.sockso.Utils;
import com.pugh.sockso.db.DatabaseConnectionException;
import com.pugh.sockso.db.HSQLDatabase;

import java.sql.SQLException;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

/**
 *  An in-memory only database that can be used for running real queries during
 *  testing.
 * 
 */

public class TestDatabase extends HSQLDatabase {

    private static final Logger log = Logger.getLogger( TestDatabase.class );

    public TestDatabase() {
        
        super( "", "jdbc:hsqldb:mem:" +Utils.getRandomString(10) );
        
        try { connect( null ); }
        catch ( final DatabaseConnectionException e ) {}

    }

    /**
     *  Applies a named fixture to the database
     *
     *  Fixtures need to be stored in the "test-data/fixtures" folder, and have
     *  the ".fix" extension.  The format of the file is...
     *
     *  table:field value,another field value
     *
     *  Lines start with the table name of the data, then a commer seperated
     *  list of field values to insert (needs to match column order of table)
     *
     *  Field values are usually just strings, but you can use the following
     *  special functions...
     *
     *  now() - current_timestamp
     * 
     *  @param name
     *
     *  @throws IOException
     *  @throws SQLException
     * 
     */

    public void fixture( final String name ) throws IOException, SQLException {

        final File file = new File( "test-data/fixtures/" +name+ ".fix" );

        if ( !file.exists() ) {
            throw new IOException( "Fixture file '" +file.getName()+ "' does not exist" );
        }
        
        final BufferedReader in = new BufferedReader( new FileReader(file) );
        
        String line = null;
        
        while ( (line = in.readLine()) != null ) {

            final Pattern p = Pattern.compile( "(\\w+):(.*)" );
            final Matcher m = p.matcher( line );

            if ( m.matches() ) {

                final String table = m.group( 1 );
                final String[] fields = m.group( 2 ).split( "," );
                final StringBuffer sql = new StringBuffer();

                for ( final String field : fields ) {

                    if ( !sql.toString().equals("") ) {
                        sql.append( "," );
                    }

                    if ( field.equals("now()") ) {
                        sql.append( "current_timestamp" );
                    }
                    else {
                        sql.append( "'" +escape(field)+ "'" );
                    }

                }

                update( "insert into " +table+ " values ( " +sql.toString()+ " ) " );

            }

        }

        Utils.close( in );

    }

}
