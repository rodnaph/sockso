
package com.pugh.sockso.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import joptsimple.OptionSet;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;

@Singleton
public class SQLiteDatabase extends JDBCDatabase {

    private static final Logger log = Logger.getLogger( SQLiteDatabase.class );
    
    private Connection cnn;

    /**
     *  connects to the database
     * 
     *  @param options
     * 
     */
    
    public void connect( final OptionSet options ) throws DatabaseConnectionException {
        
        try {

            Class.forName( "org.sqlite.JDBC" );
            cnn = DriverManager.getConnection(
                "jdbc:sqlite:" +getDefaultDatabasePath( ".sqlite" )
            );

            createStructure();

            checkUserIsActiveColumnExists();
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
                      " id integer not null primary key autoincrement, " +
                      " artist_id integer not null, " +
                      " album_id integer null, " +
                      " name text not null, " +
                      " path text not null, " +
                      " length integer not null, " +
                      " date_added datetime not null, " +
                      " collection_id integer not null, " +
                      " track_no integer null, " +
                      " unique ( artist_id, album_id, name ) " +
                  " ) ";
            update( sql );
            
            sql = " create table genres ( " +
                      " id integer not null primary key autoincrement, " +
                      " name text not null, " +
                      " unique ( name ) " +
                  " ) ";
            update( sql );

            sql = " create table play_log ( " +
                      " id integer not null primary key autoincrement, " +
                      " track_id integer null, " +
                      " date_played datetime not null, " +
                      " user_id integer null, " +
                      " scrobbled integer not null default 0 " +
                  " ) ";
            update( sql );

            sql = " create table playlist_tracks ( " +
                      " id integer not null primary key autoincrement, " +
                      " playlist_id integer null, " +
                      " track_id integer null " +
                  " ) ";
            update( sql );

            sql = " create table playlists ( " +
                      " id integer not null primary key autoincrement, " +
                      " name text not null, " +
                      " date_created datetime not null, " +
                      " date_modified datetime not null, " +
                      " user_id integer null, " +
                      " unique ( name ) " +
                  " ) ";
            update( sql );

            sql = " create table collection ( " +
                      " id integer not null primary key autoincrement, " +
                      " path text not null, " +
                      " unique ( path ) " +
                  " ) ";
            update( sql );
            
            sql = " create table artists ( " +
                      " id integer not null primary key autoincrement, " +
                      " name text not null, " +
                      " date_added datetime not null, " +
                      " unique ( name ) " +
                  " ) ";
            update( sql );
            
            sql = " create table albums ( " +
                      " id integer not null primary key autoincrement, " +
                      " artist_id integer not null, " +
                      " name text not null, " +
                      " year text null, " +
                      " date_added datetime not null, " +
                      " unique ( artist_id, name ) " +
                  " ) ";
            update( sql );
            
            sql = " create table request_log ( " +
                      " id integer not null primary key autoincrement, " +
                      " user_id integer null, " +
                      " ip_address text not null, " +
                      " date_of_request datetime not null, " +
                      " request_url text not null, " +
                      " user_agent text not null, " +
                      " referer text not null, " +
                      " cookies text not null " +
                  " ) ";
            update( sql );
            
            sql = " create table sessions ( " +
                      " id integer not null primary key autoincrement, " +
                      " code text not null, " +
                      " user_id integer not null, " +
                      " date_created datetime not null " +
                  " ) ";
            update( sql );
            
            sql = " create table users ( " +
                      " id integer not null primary key autoincrement," +
                      " name text not null unique, " +
                      " pass text not null, " +
                      " email text not null," +
                      " date_created datetime not null, " +
                      " is_admin integer not null default '0' " +
                  " ) ";
            update( sql );

            sql = " create table properties ( " +
                      " id integer not null primary key autoincrement, " +
                      " name text not null, " +
                      " value text not null," +
                      " unique ( name ) " +
                  " ) ";
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
     *  returns the jdbc connection handle
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
        
        return str.replaceAll( "'", "''" );
        
    }

    /**
     *  closes the db connection
     * 
     */
    
    public void close() {

        try {
            cnn.close();
        }
        catch ( final Exception e ) {}
        
    }
    
    /**
     *  returns the random function
     * 
     *  @return
     * 
     */
    
    public String getRandomFunction() {

        return "random";

    }

    @Override
    protected void checkTrackGenreColumnExists() {

        final String sql = " alter table tracks " +
                           " add column genre_id integer null";

        safeUpdate ( sql );
    }
    
}
