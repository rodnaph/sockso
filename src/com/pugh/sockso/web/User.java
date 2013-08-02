/*
 * User.java
 * 
 * Created on Aug 4, 2007, 10:15:20 AM
 * 
 * A user of the web interface
 * 
 */

package com.pugh.sockso.web;

import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

public class User {

    private static final Logger log = Logger.getLogger( User.class );
    
    private final int sessionId;
    private final String name, pass, email, sessionCode;
    private final boolean isAdmin;

    private boolean isActive;
    private int id;

    public User( final int id, final String name ) {

        this( id, name, "", "" );

    }
    
    public User( final String name, final String pass, final String email, final boolean isAdmin ) {

        this( -1, name, pass, email, -1, "", isAdmin );

    }
    
    public User( final int id, final String name, final String email, final boolean isAdmin ) {

        this( id, name, "", email, -1, "", isAdmin );

    }

    public User( final int id, final String name, final String pass, final String email ) {

        this( id, name, pass, email, -1, "", false );

    }

    public User( final int id, final String name, final String pass, final String email, final int sessionId, final String sessionCode, final boolean isAdmin ) {

        this.id = id;
        this.name = name;
        this.pass = pass;
        this.email = email;
        this.sessionId = sessionId;
        this.sessionCode = sessionCode;
        this.isAdmin = isAdmin;

        isActive = true;

    }

    /**
     *  Sets user as being active/inactive
     *
     *  @param isActive
     *
     */
    
    public void setActive( final boolean isActive ) {

        this.isActive = isActive;
        
    }

    /**
     *  Indicates if the user is active or not
     *
     *  @return
     *
     */

    public boolean isActive() {

        return isActive;

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getSessionId() {
        return sessionId;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     *  Saves a new user to the database
     *
     *  @param db
     *
     */

    public void save( final Database db ) throws SQLException {

        PreparedStatement st = null;
        ResultSet rs = null;

        try {

            String sql = " insert into users ( name, pass, email, date_created, is_admin, is_active ) " +
                         " values ( ?, ?, ?, current_timestamp, ?, ? ) ";

            st = db.prepare( sql );
            st.setString( 1, name );
            st.setString( 2, Utils.md5(pass) );
            st.setString( 3, email );
            st.setInt( 4, isAdmin ? 1 : 0 );
            st.setString( 5, isActive ? "1" : "0" );
            st.executeUpdate();
            
            Utils.close( st );

            sql = " select max(id) as new_id " +
                  " from users ";
            st = db.prepare( sql );
            rs = st.executeQuery();
            
            if ( !rs.next() ) {
                throw new SQLException( "No results returned from aggregate query" );
            }
            
            id = rs.getInt( "new_id" );

        }

        finally {
            Utils.close( st );
        }
        
    }

    /**
     *  Update this users in the database
     *
     *  @param db
     *
     *  @throws SQLException
     *
     */

    public void update( final Database db ) throws SQLException {

        PreparedStatement st = null;
        
        try {
            
            final String sql = " update users " +
                               " set name = ?, " +
                                   " email = ?, " +
                                   " is_admin = ?, " +
                                   " is_active = ? " +
                               " where id = ? ";
            
            st = db.prepare( sql );
            st.setString( 1, getName() );
            st.setString( 2, getEmail() );
            st.setInt( 3, isAdmin() ? 1 : 0 );
            st.setString( 4, isActive() ? "1" : "0" );
            st.setInt( 5, getId() );

            log.debug( "Update user [" +getId()+ "]: " + sql );
            log.debug( "Name: " +getName() );
            log.debug( "Email: " +getEmail() );
            log.debug( "Admin: " +(isAdmin() ? "1" : "0") );
            log.debug( "Active: " +(isActive() ? "1" : "0") );
            
            st.executeUpdate();
            
        }
        
        finally {
            Utils.close( st );
        }

    }

    /**
     * Finds a user db their ID
     *
     * @param db
     * @param id
     *
     * @return
     */
    public static User find( final Database db, final int id ) {

        final String sql = " select id, name, email, is_admin, is_active " +
                           " from users " +
                           " where id = ? ";

        PreparedStatement st = null;
        ResultSet rs = null;

        try {

            st = db.prepare( sql );
            st.setInt( 1, id );
            rs = st.executeQuery();

            if ( rs.next() ) {
                final User user = new User(
                    id,
                    rs.getString( "name" ),
                    rs.getString( "email" ),
                    rs.getBoolean( "is_admin" )
                );
                user.setActive( rs.getString("is_active").equals("1") );
                return user;
            }

        }

        catch ( final Exception e ) {
            log.error( e );
        }
        finally {
            Utils.close(rs);
            Utils.close(st);
        }
        return null;

    }

    /**
     *  Delete a user (and their associated data) by ID, return true on success,
     *  or false on failure (ie. invalid user id)
     *
     *  @param db
     *  @param id
     *
     *  @return
     *
     *  @throws SQLException
     *
     */

    public static boolean delete( final Database db, final int id ) throws SQLException {
        
        PreparedStatement st = null;

        try {
            
            String sql = " delete from playlist_tracks " +
                         " where playlist_id in ( " +
                            " select id " +
                            " from playlists " +
                            " where user_id = ? " +
                         " ) ";
            st = db.prepare( sql );
            st.setInt( 1, id );
            st.execute();
            st.close();

            sql = " delete from playlists " +
                  " where user_id = ? ";
            st = db.prepare( sql );
            st.setInt( 1, id );
            st.execute();
            st.close();

            sql = " delete from users " +
                         " where id = ? ";
            st = db.prepare( sql );
            st.setInt( 1, id );
            int affectedRows = st.executeUpdate();
            st.close();

            return ( affectedRows == 1 );

        }

        finally {
            Utils.close( st );
        }

    }

}
