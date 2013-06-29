/*
 * HSQLDatabase.java
 * 
 * Created on Jul 21, 2007, 12:38:02 PM
 * 
 * An implementation of the database class using HSQLDB
 * 
 */

package com.pugh.sockso.db;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Utils;
import com.pugh.sockso.music.encoders.Encoders;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

import java.io.File;

import joptsimple.OptionSet;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;

@Singleton
public class HSQLDatabase extends JDBCDatabase {

    private static final Logger log = Logger.getLogger( AbstractDatabase.class );

    private final String dataPath;

    private Connection connection;
    private String connectionString;
    
    /**
     * 
     *  @throws com.pugh.sockso.db.DatabaseConnectionException
     * 
     */
    
    public HSQLDatabase() {
        
        this( getDefaultDatabasePath("") );
        
    }
    
    /**
     *  constructor
     * 
     *  @param dataPath the path to the data
     * 
     */
    
    public HSQLDatabase( final String dataPath ) {
        
        this( dataPath, "jdbc:hsqldb:file:" + dataPath );

    }

    /**
     *  Constructor that allows specifying the connection string
     * 
     *  @param dataPath
     *  @param connectionString
     * 
     */

    public HSQLDatabase( final String dataPath, final String connectionString ) {
        
        this.dataPath = dataPath;
        this.connectionString = connectionString;

    }
   
    /**
     *  connects to the database
     * 
     *  @param options
     * 
     */
    
    public void connect( final OptionSet options ) throws DatabaseConnectionException {

        // load the driver and establish a connection
        try {
            Class.forName( "org.hsqldb.jdbcDriver" );
            connection = DriverManager.getConnection(
                connectionString, "sa", ""
            );
        }

        catch ( final Exception e ) {
            if ( e.getMessage().contains("old version") ) {
                throw new DatabaseConnectionException( "You seem to have upgraded Sockso without " +
                                                       "exiting the old version cleanly.  This has left the database " +
                                                       "in an inconsistent state.  To run the new version of Sockso " +
                                                       "you will first need to run, and cleanly (ie. press exit) close " +
                                                       "the old version." );
            }
            else {
                throw new DatabaseConnectionException( e.getMessage() );
            }
        }

        checkDefaultStructure();
        setDefaultSettings();

        /**
         * Check database upgrades have been run.
         * 
         * Upgrades should be written in such a way that they can be re-run.
         */
        checkUserSessionsUpgrade();
        checkLogRequestsUpgrade();
        checkCollectionPathBackslashes();
        checkPlayLogTrackIndex();
        checkMultipleSlashesInCollection();
        checkRemoveTracksAddedByField();
        checkOldRequireLoginProperty();
        checkPlayLogUserId();
        checkScrobbledLogField();
        checkArtistsBrowseNameField();
        checkIndexerTableExists();
        checkUserAdminColumnExists();
        checkUserIsActiveColumnExists();
        checkAlbumYearColumnExists();
        checkGenreSchema();
        checkTrackGenreColumnExists();

    }

    /**
     *  sets the default properties for the database
     * 
     */
    
    private void setDefaultSettings() {
        
        try {

            update( " set write_delay 0 " );
            update( " set ignorecase true " );

        }

        catch ( final SQLException e ) {
            log.fatal( e );
        }

    }

    /**
     *  returns the db connection
     * 
     *  @return
     * 
     */
    
    public Connection getConnection() {
        
        return connection;
        
    }

    /**
     * Checks the users.is_admin column
     * 
     */
    
    private void checkUserAdminColumnExists() {

        final String sql = " alter table users " +
                           " add is_admin bit default 0 ";

        safeUpdate( sql );

    }

    /**
     *  Checks the file used to store indexing info exists
     *
     */

    private void checkIndexerTableExists() {

        final String sql = " create table indexer ( " +
                               " id integer not null,  " +
                               " last_modified timestamp not null, " +
                               " primary key ( id ) " +
                           " ) ";
        safeUpdate( sql );

    }

    /**
     *  Checks we've added and populated the artists prefix field
     * 
     */

    private void checkArtistsBrowseNameField() {

        try {

            String sql = " alter table artists " +
                         " add browse_name varchar_ignorecase(255) null ";
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
     *  checks the field to mark tracks as having been scrobbled exists
     * 
     */
    
    private void checkScrobbledLogField() {
        
        try {
            
            final String sql = " alter table play_log " +
                               " add scrobbled bit default 0 ";
            
            update( sql );
            
        }
        
        catch ( final SQLException e ) {
            log.debug( e );
        }
        
    }
    
    /**
     *  adds a user_id field to the play log table (we can then do stats
     *  per user)
     * 
     */
    
    private void checkPlayLogUserId() {

        try {
            
            final String sql = " alter table play_log " +
                               " add user_id int null ";
            
            update( sql );
            
        }
        
        catch ( final SQLException e ) {
            log.debug( e );
        }

    }
    
    /**
     *  for uploads, the property used to be called uploads.requireLogin, but it
     *  was actually being used with reverse meaning.  so this checks if it's
     *  set and if it is migrates it's setting over to the better named
     *  uploads.allowAnonymous property
     * 
     */
    
    private void checkOldRequireLoginProperty() {
       
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
            
            String sql = " select value " +
                         " from properties " +
                         " where name = ? ";
            
            st = prepare( sql );
            st.setString( 1, "uploads.requireLogin" );
            rs = st.executeQuery();
            
            if ( rs.next() ) {
                setProperty(
                    Constants.WWW_UPLOADS_ALLOW_ANONYMOUS,
                    rs.getString("value")
                );
                sql = " delete from properties " +
                      " where name = 'uploads.requireLogin' ";
                update( sql );
            }
            
        }
        
        catch ( final SQLException e ) {
            log.debug( e );
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
    }
    
    /**
     *  the tracks.addedBy field was added for the uploads upgrade, but turned
     *  out to never actually be used.
     * 
     */
    
    private void checkRemoveTracksAddedByField() {
        
        try {
            
            final String sql = " alter table tracks " +
                               " drop column addedBy ";
            update( sql );
            
        }
        
        catch ( final SQLException e ) {
            log.debug( e );
        }
        
    }
    
   /**
    *  there was a bug with a fix that was adding too many backslashes
    *  to collection paths.  the bug has been fixed, but this undoes the
    *  damage that it did
    *
    */

   private void checkMultipleSlashesInCollection() {

       PreparedStatement st = null;
       ResultSet rs = null;

       try {

           final String separator = System.getProperty( "file.separator" );
           String sql = " select id, path " +
                        " from collection ";
           
           st = prepare( sql );
           rs = st.executeQuery();

           while ( rs.next() ) {

               final String newPath = rs.getString("path").replaceAll( "^(.*?\\" +separator+ ")\\" +separator+ "*$", "$1" );

               sql = " update collection " +
                     " set path = '" +escape(newPath)+ "' " +
                     " where id = " + rs.getString("id");
               update( sql );

           }

       }

        catch ( final SQLException e ) {
            log.debug( e );
        }

       finally {
           Utils.close( rs );
           Utils.close( st );
       }

   }

    /**
     *  checks there's an index on the play_log table otherwise
     *  some queries (ie. popular) will be slooooooow.
     * 
     */
    
    private void checkPlayLogTrackIndex() {
        
        try {
        
            final String sql = " create index ix_play_log_track_id " +
                         " on play_log ( track_id ) ";
            update( sql );

        }
        
        catch ( final SQLException e ) {
            log.debug( e );
        }
        
    }
    
    /**
     *  checks all paths in the collection folder have a trailing slash
     * 
     */
    
    private void checkCollectionPathBackslashes() {
        
        final String separator = System.getProperty( "file.separator" );

        ResultSet rs = null;
        PreparedStatement st = null;

        try {
            
            String sql = " select id, path " +
                               " from collection ";
            st = prepare( sql );
            rs = st.executeQuery();
            
            while ( rs.next() )
                if ( !rs.getString("path").matches(".*\\" +separator+ "$") ) {
                    sql = " update collection " +
                          " set path = path + '" +separator+ "'" +
                          " where id = " +rs.getString("id");
                    update( sql );
                }

        }
        
        catch ( final SQLException e ) {
            log.debug( e );
        }
        finally {
            Utils.close( st );
            Utils.close( rs );
        }

    }
    
    /**
     *  checks the request logging schema is in place, and also that any old lame
     *  properties have been mapped across to the new encoding properties
     * 
     */
    
    private void checkLogRequestsUpgrade() {
        
        String sql = "";
        ResultSet rs = null;
        Statement st = null;
        
        try {

            // first try and transfer any old encoding properties
            
            sql = " select p.value as value " +
                        " from properties p " +
                        " where p.name = 'player.lame.use' ";
            st = getConnection().createStatement();
            rs = st.executeQuery( sql );

            if ( rs.next() && rs.getString("value").equals("yes") ) {
                Utils.close( rs );
                Utils.close( st );

                String bitrate = "128";
                sql = " select p.value as value " +
                        " from properties p " +
                        " where p.name = 'player.lame.bitrate' ";
                st = getConnection().createStatement();
                rs = st.executeQuery( sql );

                if ( rs.next() ) {
                    bitrate = rs.getString( "value" );
                }

                setProperty( "encoders.mp3", Encoders.Type.BUILTIN.name() );
                setProperty( "encoders.mp3.name", Encoders.Builtin.Lame.name() );
                setProperty( "encoders.mp3.bitrate", bitrate );
                setProperty( "player.lame.use", "" );
                setProperty( "player.lame.bitrate", "" );

            }
            
            // then create request_log table
            
            sql = " create table request_log ( " +
                            " id integer not null identity, " +
                            " user_id integer null," +
                            " ip_address char(16) not null, " +
                            " date_of_request timestamp not null, " +
                            " request_url varchar(255) not null, " +
                            " user_agent varchar(255) not null, " +
                            " referer varchar(255) not null, " +
                            " cookies varchar(255) not null, " +
                            " primary key ( id ) " +
                         " ) ";
            update( sql );
            
        }
        
        catch ( final SQLException e ) {
            log.debug( e );
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
    }

    /**
     *  checks to make sure the upgrade to having users
     *  and sessions has been made
     * 
     */
    
    protected void checkUserSessionsUpgrade() {
        
        String sql = "";
        
        try {

            sql = " alter table playlists " +
                  " add user_id integer null ";
            update( sql );

            sql = " create table users ( " +
                      " id integer not null identity, " +
                      " name varchar(50) not null, " +
                      " pass char(32) not null, " +
                      " email varchar(255) not null, " +
                      " date_created timestamp not null, " +
                      " unique ( name ), " +
                      " unique ( email ), " +
                      " primary key ( id ) " +
                  " ) ";
            update( sql );

            sql = " create table sessions ( " +
                      " id integer not null identity, " +
                      " code char(10) not null, " +
                      " user_id integer not null, " +
                      " date_created timestamp not null, " +
                      " unique ( id, code ), " +
                      " primary key ( id ) " +
                  " ) ";
            update( sql );

        }
        
        catch ( final SQLException e ) {
            log.debug( e );
        }
        
    }

    /**
     *  shuts down the database and closes the connection
     * 
     */
    
    public void close() {

        log.info( "Shutting Down" );

        try {
            update( "shutdown" );
        }
        
        catch ( final SQLException e ) {
            log.error( e );
        }
        
    }
    
    /**
     *  checks if the database structure exists, and if it doesn't then
     *  goes ahead and tries to create it
     * 
     */
    
    private void checkDefaultStructure() {
                
        log.debug( "Checking Database Existence" );
        
        // check if database exists already
        if ( new File(dataPath+".script").exists() ) return;

        try {

            log.debug( "Creating Database Structure" );

            update( "set ignorecase true" );

            // artists
            update(
                " create table artists ( " +
                    " id integer not null identity, " +
                    " name varchar(255) not null, " +
                    " date_added timestamp not null, " +
                    " unique ( name ), " +
                    " primary key ( id ) " +
                " ) "
            );
            log.debug( "Created 'artists' table" );

            // albums
            update(
                " create table albums ( " +
                    " id integer not null identity, " +
                    " artist_id integer not null, " +
                    " name varchar(255) not null, " +
                    " date_added timestamp not null, " +
                    " unique( artist_id, name ), " +
                    " primary key ( id ) " +
                " ) "
            );
            log.debug( "Created 'albums' table" );
            
            // tracks
            update(
                " create table tracks ( " +
                    " id integer not null identity, " +
                    " artist_id integer not null, " +
                    " album_id integer null, " +
                    " name varchar(255) not null, " +
                    " path varchar(500) not null, " +
                    " length integer not null, " +
                    " date_added timestamp not null, " +
                    " collection_id integer not null, " +
                    " track_no integer null, " +
                    " unique ( artist_id, album_id, name ), " +
                    " primary key ( id ) " +
                " ) "
            );
            log.debug( "Created 'tracks' table" );

            // properties
            update(
                " create table properties ( " +
                    " id integer not null identity, " +
                    " name varchar(100) not null, " +
                    " value varchar(255) not null, " +
                    " unique ( name ), " +
                    " primary key ( id ) " +
                " ) "
            );
            log.debug( "Created 'properties' table" );
            
            // collection
            update(
                " create table collection ( " +
                    " id integer not null identity, " +
                    " path varchar(500) not null, " +
                    " unique ( path ), " +
                    " primary key ( id ) " +
                " ) "
            );
            log.debug( "Created 'collection' table" );
            
            // play log
            update(
                " create table play_log ( " +
                    " id integer not null identity, " +
                    " track_id integer not null, " +
                    " date_played timestamp not null, " +
                    " primary key ( id ) " +
                " ) "
            );
            log.debug( "Created 'play_log' table" );
            
            // playlists
            update(
                " create table playlists ( " +
                    " id integer not null identity, " +
                    " name varchar(255) not null, " +
                    " date_created timestamp not null, " +
                    " date_modified timestamp not null, " +
                    " unique ( name ), " +
                    " primary key ( id ) " +
                " ) "
            );
            log.debug( "Created 'playlists' table" );
            
            // tracks for playlists
            update(
                " create table playlist_tracks ( " +
                    " id integer not null identity, " +
                    " playlist_id integer not null, " +
                    " track_id integer not null, " +
                    " primary key ( id ) " +
                " ) "
            );
            log.debug( "Created 'playlist_tracks' table" );
            
            setDefaultProperties();

            log.debug( "Created Default Properties" );
            log.debug( "Database Setup Complete" );

        }

        catch ( final SQLException e ) {
            log.fatal( "Error Creating Database: " + e.getMessage() );
        }

    }

    /**
     * Checks the genres table
     *
     */
    protected void checkGenreSchema() {

         safeUpdate(
            " create table genres ( " +
                " id integer not null identity, " +
                " name varchar(255) not null, " +
                " unique ( name ), " +
                " primary key ( id ) " +
            " ) "
        );
        log.debug( "Created 'genres' table" );
    }

    /**
     * Check the tracks genre_id column
     *
     */
    protected void checkTrackGenreColumnExists() {

        final String alterSql = " alter table tracks " +
                                " add genre_id integer null ";

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

                Utils.close(rs);
                Utils.close(st);

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
            Utils.close( rs );
            Utils.close( st );
        }
    }


    /**
     *  quotes a string for safe inclusion in sql
     * 
     *  @param str the string to quote
     *  @return the safely escaped string
     * 
     */
    
    public String escape( final String str ) {

        return str.replaceAll( "'", "''" );

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
