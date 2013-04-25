/*
 * DatabaseTest.java
 *
 * Created on June 6, 2007, 10:49 AM
 * 
 */

package com.pugh.sockso.db;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.Utils;
import com.pugh.sockso.tests.SocksoTestCase;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import java.io.File;
import java.io.FileInputStream;

import org.apache.log4j.Logger;

public class DatabaseTest extends SocksoTestCase {

    private static Logger log = Logger.getLogger( DatabaseTest.class );

    public void testHSQLDdatabase() {        
        File f = new File( "test-database" );
        HSQLDatabase db = null;
        try {
            db = new HSQLDatabase( f.getAbsolutePath() );
            db.connect( null );
            doTableTests( db );
            doPropertiesTests( db );
            doEscapeTests( db );
         }
        catch ( DatabaseConnectionException e ) {}
        finally {
            try { db.update( "shutdown" ); }
            catch ( Exception e ) {}
            new File( "test-database.properties" ).delete();
            new File( "test-database.script" ).delete();
            new File( "test-database.log" ).delete();
        }
    }

    /**
     * this test only runs if mysql is configured in sockso.properties
     * @throws Exception
     */
    public void testMySqlDatabase() throws Exception {
        java.util.Properties props = new java.util.Properties();
        props.load( new FileInputStream("sockso.properties") );
        if ( props.getProperty("mysql.user") != null ) {
            MySQLDatabase db = new MySQLDatabase();
            db.connect(
                props.getProperty("mysql.host"),
                props.getProperty("mysql.user"),
                props.getProperty("mysql.pass"),
                "sockso_test"
            );
            doTableTests( db );
            doPropertiesTests( db );
        }
    }

    public void testSqliteDatabase() throws Exception {
        // @todo
    }
    
    private void doTableTests( Database db ) {

        doTableTest( "artists", new String[] { "id", "name", "date_added", "browse_name" }, db );
        
        doTableTest( "albums", new String[] { "id", "artist_id", "name", "year", "date_added" }, db );

        doTableTest( "genres", new String[] { "id", "name" }, db );

        doTableTest( "tracks", new String[] { "id", "artist_id", "album_id", "name", "path", "length", "date_added", "collection_id", "track_no", "genre_id" }, db );

        doTableTest( "properties", new String[] { "id", "name", "value" }, db );

        doTableTest( "collection", new String[] { "id", "path" }, db );

        doTableTest( "play_log", new String[] { "id", "track_id", "date_played", "user_id", "scrobbled" }, db );

        doTableTest( "playlists", new String[] { "id", "name", "date_created", "date_modified", "user_id" }, db );

        doTableTest( "playlist_tracks", new String[] { "id", "playlist_id", "track_id" }, db );

        doTableTest( "users", new String[] { "id", "name", "pass", "email", "date_created", "is_admin", "is_active" }, db );

        doTableTest( "sessions", new String[] { "id", "code", "user_id", "date_created" }, db );
     
        doTableTest( "request_log", new String[] { "id", "user_id", "ip_address", "date_of_request", "request_url", "user_agent", "referer", "cookies" }, db );

        doTableTest( "indexer", new String[] { "id", "last_modified" }, db );

    }
    
    private void doTableTest( final String tableName, final String[] fields, final Database db ) {
        
        try {
            
            final String sql = " select * " +
                               " from " + tableName;
            final ResultSet rs = db.query( sql );
            final ResultSetMetaData meta = rs.getMetaData();
            
            assertEquals( meta.getColumnCount(), fields.length );
            
            for ( final String field : fields ) {
                boolean foundCol = false;
                for ( int i=0; i<meta.getColumnCount(); i++ ) {
                    if ( meta.getColumnName(i+1).toLowerCase().equals(field) )
                        foundCol = true;
                }
                if ( !foundCol )
                    fail( "could not find column " +tableName+ "." +field );
            }
            
        }
        
        catch ( final SQLException e ) {
            fail( e.getMessage() );
        }
                
    }
    
    public void doPropertiesTests( Database db ) {
        doPropertyTest( db, Constants.SERVER_PORT, "4444" );
        doPropertyTest( db, Constants.WWW_TITLE, "Sockso" );
        doPropertyTest( db, Constants.WWW_TAGLINE, "Personal Music Server" );
        doPropertyTest( db, Constants.COLLMAN_SCAN_INTERVAL, "5" );
        doPropertyTest( db, Constants.COLLMAN_SCAN_ONSTART, Properties.YES );
        doPropertyTest( db, Constants.APP_CONFIRM_EXIT, Properties.YES );
        doPropertyTest( db, Constants.APP_START_MINIMIZED, Properties.NO );
    }
    
    private void doPropertyTest( Database db, String name, String value ) {
        log.info( "testing property '" + name + "' = '" + value + "'" );
        String sql = " select p.value " +
                        " from properties p " +
                        " where p.name = '" + db.escape(name) + "' ";
        ResultSet rs = null;
        try {
            rs = db.query( sql );
            if ( !rs.next() )
                fail( "expected property '" + name + "' not found" );
            if ( !rs.getString("value").equals(value) )
                fail( "expected property value not found ('" + value + "' != '" + rs.getString("value") + "')" );
            return;
        }
        catch ( SQLException e ) {
            fail( e.getMessage() );
        }
        finally {
            Utils.close( rs );
        }
    }
    
    public void doEscapeTests( Database db ) {
        String str = "it's";
        String expResult = "it''s";
        String result = db.escape(str);
        assertEquals( expResult, result );
    }

}
