
package com.pugh.sockso.db;

import com.pugh.sockso.Options;
import com.pugh.sockso.Utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

import joptsimple.OptionSet;

import com.google.inject.Singleton;

/**
 *  provides an interface to a mysql sockso database
 * 
 */

@Singleton
public class MySQLDatabase extends JDBCDatabase implements Database {

    private static final Logger log = Logger.getLogger( MySQLDatabase.class );
    
    private Connection cnn;
    
    /**
     *  connects to the database, throws exception on error
     * 
     *  @param options
     * 
     *  @throws com.pugh.sockso.db.DatabaseConnectionException
     * 
     */
    
    public void connect( final OptionSet options ) throws DatabaseConnectionException {
        
        final String dbHost = options.has(Options.OPT_DBHOST) ? options.valueOf(Options.OPT_DBHOST).toString() : "localhost";
        final String dbUser = options.has(Options.OPT_DBUSER) ? options.valueOf(Options.OPT_DBUSER).toString() : "root";
        final String dbPass = options.has(Options.OPT_DBPASS) ? options.valueOf(Options.OPT_DBPASS).toString() : "";
        final String dbName = options.has(Options.OPT_DBNAME) ? options.valueOf(Options.OPT_DBNAME).toString() : "sockso";
        
        connect( dbHost, dbUser, dbPass, dbName );
        
    }
    
    /**
     *  Connect using the specified credentials
     * 
     *  @param dbHost
     *  @param dbUser
     *  @param dbPass
     *  @param dbName
     * 
     */
    
    public void connect( final String dbHost, final String dbUser, final String dbPass, final String dbName ) throws DatabaseConnectionException {

        try {

            // connect to server
            
            Class.forName( "com.mysql.jdbc.Driver" );
            cnn = DriverManager.getConnection(
                "jdbc:mysql://" + dbHost, dbUser, dbPass
            );
            
            // select database and make sure schema is ok

            update( " use " + dbName );
            update( " set names utf8 " );

            createStructure();

            // schema updates
            checkArtistsBrowseNameField();
            checkIndexerTableExists();
            checkUserAdminColumnExists();
            checkUserIsActiveColumnExists();
            checkAlbumYearColumnExists();
            checkGenreSchema();
            checkTrackGenreColumnExists();

        }

        catch ( final Exception e ) {
            throw new DatabaseConnectionException( e.getMessage() );
        }
        
    }

    /**
     *  ensures that the database structure is present
     * 
     */
    
    protected void createStructure() {
       
        String sql = "";
        
        try {
            
            sql = " create table tracks ( " +
                      " id int unsigned not null auto_increment, " +
                      " artist_id int unsigned not null, " +
                      " album_id int unsigned null, " +
                      " name varchar(255) not null, " +
                      " path varchar(500) not null, " +
                      " length int unsigned not null, " +
                      " date_added datetime not null, " +
                      " collection_id int unsigned not null, " +
                      " track_no smallint null, " +
                      " primary key ( id ), " +
                      " unique ( artist_id, album_id, name ) " +
                  " ) character set utf8 ";
            update( sql );

            sql = " create table play_log ( " +
                      " id int unsigned not null auto_increment, " +
                      " track_id int unsigned null, " +
                      " date_played datetime not null, " +
                      " user_id int unsigned null, " +
                      " scrobbled tinyint(1) not null default 0, " +
                      " primary key ( id ) " +
                  " ) character set utf8 ";
            update( sql );

            sql = " create table playlist_tracks ( " +
                      " id int unsigned not null auto_increment, " +
                      " playlist_id int unsigned null, " +
                      " track_id int unsigned null, " +
                      " primary key ( id ) " +
                  " ) character set utf8 ";
            update( sql );

            sql = " create table playlists ( " +
                      " id int unsigned not null auto_increment, " +
                      " name varchar(255) not null, " +
                      " date_created datetime not null, " +
                      " date_modified datetime not null, " +
                      " user_id int unsigned null, " +
                      " primary key ( id ), " +
                      " unique ( name ) " +
                  " ) character set utf8 ";
            update( sql );

            sql = " create table collection ( " +
                      " id int unsigned not null auto_increment, " +
                      " path varchar(500) not null, " +
                      " primary key ( id ) " +
                  " ) character set utf8 ";
            update( sql );
            
            sql = " create table artists ( " +
                      " id int unsigned not null auto_increment, " +
                      " name varchar(255) not null, " +
                      " date_added datetime not null, " +
                      " primary key ( id ), " +
                      " unique ( name ) " +
                  " ) character set utf8 ";
            update( sql );
            
            sql = " create table albums ( " +
                      " id int unsigned not null auto_increment, " +
                      " artist_id int unsigned not null, " +
                      " name varchar(255) not null, " +
                      " date_added datetime not null, " +
                      " primary key ( id ), " +
                      " unique ( artist_id, name ) " +
                  " ) character set utf8 ";
            update( sql );
            
            sql = " create table request_log ( " +
                      " id int unsigned not null auto_increment, " +
                      " user_id int unsigned null, " +
                      " ip_address char(16) not null, " +
                      " date_of_request datetime not null, " +
                      " request_url varchar(255) not null, " +
                      " user_agent varchar(255) not null, " +
                      " referer varchar(255) not null, " +
                      " cookies varchar(255) not null, " +
                      " primary key ( id ) " +
                  " ) character set utf8 ";
            update( sql );
            
            sql = " create table sessions ( " +
                      " id int unsigned not null auto_increment, " +
                      " code char(10) not null, " +
                      " user_id int unsigned not null, " +
                      " date_created datetime not null, " +
                      " primary key ( id ) " +
                  " ) character set utf8 ";
            update( sql );
            
            sql = " create table users ( " +
                      " id int unsigned not null auto_increment," +
                      " name varchar(50) not null unique, " +
                      " pass char(32) not null, " +
                      " email varchar(255) not null," +
                      " date_created datetime not null," +
                      " primary key ( id )" +
                  " ) character set utf8 ";
            update( sql );

            sql = " create table properties ( " +
                      " id int unsigned not null auto_increment, " +
                      " name varchar(100) not null, " +
                      " value varchar(255) not null," +
                      " primary key ( id ), " +
                      " unique ( name ) " +
                  " ) character set utf8 ";
            update( sql );

            sql = " create index ix_play_log_track_id " +
                  " on play_log ( track_id ) ";
            update( sql );

            setDefaultProperties();

        }
        
        catch ( final SQLException e ) {
            log.error( e );
        }
        
    }

    /**
     *  Checks we've added and populated the artists prefix field
     *
     */

    private void checkArtistsBrowseNameField() {

        try {

            String sql = " alter table artists " +
                         " add browse_name varchar(255) null ";
            update( sql );

            sql = " update artists " +
                  " set browse_name = name ";
            update( sql );

        }

        catch ( final SQLException e ) {
            log.debug( e );
        }

    }

    /**
     *  Checks the file used to store indexing info exists
     * 
     */

    private void checkIndexerTableExists() {

        try {
            final String sql = " create table indexer ( " +
                                   " id int unsigned not null,  " +
                                   " last_modified datetime not null, " +
                                   " primary key ( id ) " +
                               " ) ";
            update( sql );
        }

        catch ( final SQLException e ) {
            log.debug( e );
        }

    }

    /**
     * Checks the users.is_admin column
     *
     */

    private void checkUserAdminColumnExists() {

        final String sql = " alter table users " +
                           " add is_admin tinyint(1) not null default '0' ";

        safeUpdate( sql );

    }


    protected void checkGenreSchema() {

        safeUpdate(
                " create table genres ( "
                   + " id int unsigned not null auto_increment, "
                   + " name varchar(255) not null, "
                   + " primary key ( id ), "
                   + " unique ( name ) "
                   + " ) character set utf8 "
                );

    }

    /**
     * Check the tracks genre_id column
     *
     */
    protected void checkTrackGenreColumnExists() {

        final String alterSql = " alter table tracks " +
                                " add genre_id int unsigned null";

        safeUpdate ( alterSql );

        final String name = "Unknown Genre";

        final String insertDefaultGenreSql = " insert into genres ( name ) " +
                                             " values ( '" + name + "' )";

        safeUpdate( insertDefaultGenreSql );

        final String selectIdQuery = " select id " +
                                     " from genres " +
                                     " where name = ?";

        ResultSet rs = null;
        PreparedStatement st = null;

        try {

            st = prepare( selectIdQuery );
            st.setString( 1, name );

            rs = st.executeQuery();

            if ( rs.next() ) {

                int unknownGenreId = rs.getInt( "id" );

                Utils.close( rs );
                Utils.close( st );

                final String defaultSql = " update tracks" +
                                          " set genre_id = " + unknownGenreId +
                                          " where genre_id is null";
                safeUpdate( defaultSql );

            }
            else {
                log.error("Unable to retrieve default genre id!");
            }

        }
        catch (SQLException e) {
            log.error("Unable to set default genre id: " + e.getMessage());
        }
        finally {
            Utils.close(rs);
            Utils.close(st);
        }
    }


    /**
     *  returns the raw jdbc connection
     * 
     *  @return
     * 
     */
    
    public Connection getConnection() {
        return cnn;
    }

    /**
     *  escapes a string for use in a query
     * 
     *  @param str
     * 
     *  @return
     * 
     */
    
    public String escape( final String str ) {

        return str.replaceAll( "'", "\\\\'" );

    }
    
    /**
     *  closes the database connection
     * 
     */
    
    public void close() {

        try {
            cnn.close();
        }

        catch ( final Exception e ) {
            log.error( e );
        }

    }

    /**
     *  returns the random function
     * 
     *  @return
     * 
     */
    
    public String getRandomFunction() {

        return "rand";

    }

}
