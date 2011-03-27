/*
 * UserTest.java
 * 
 * Created on Aug 5, 2007, 7:35:47 PM
 * 
 * Tests the User class
 * 
 */

package com.pugh.sockso.web;

import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;

public class UserTest extends SocksoTestCase {
    
    private Database db;

    private User user;

    @Override
    public void setUp() throws Exception {
        db = new TestDatabase();
        user = new User( "name", "pass", "name@domain.com", true );
        user.save( db );
    }

    public void testConstructor() {
        
        String name = "ashdjkas dhjaks dhkj ";
        int id = 123123123;
        
        User user = new User( id, name );
        
        assertNotNull( user );
        assertEquals( id, user.getId() );
        assertEquals( name, user.getName() );
        assertEquals( "", user.getEmail() );
        
    }
    
    public void testNewUserObjectIsActiveByDefault() {
        assertTrue( new User(1,"").isActive() );
    }
    
    public void testUserCanBeMadeInactiveAndActive() {
        User u = new User( 1, "" );
        u.setActive( true );
        assertTrue( u.isActive() );
        u.setActive( false );
        assertFalse( u.isActive() );
    }

    public void testNewUserCanBeSavedAsBeingActive() throws Exception {
        Database db = new TestDatabase();
        User u1 = new User( 1, "" );
        u1.save( db );
        User u2 = User.find( db, u1.getId() );
        assertTrue( u2.isActive() );
    }

    public void testNewUserCanBeSavedAsBeingInactive() throws Exception {
        Database db = new TestDatabase();
        User u1 = new User( 1, "" );
        u1.setActive( false );
        u1.save( db );
        User u2 = User.find( db, u1.getId() );
        assertFalse( u2.isActive() );
    }

    public void testConstructorWithPassAndEmail() {
        
        String name = "ashdjkas dhjaks dhkj ", email = "asdaas";
        int id = 123123123;
        
        User user = new User( id, name, "", email );
        
        assertNotNull( user );
        assertEquals( id, user.getId() );
        assertEquals( name, user.getName() );
        assertEquals( email, user.getEmail() );
        
    }

    public void testConstructorWithSessionInfo() {

        final String sessionCode = "ashdjkas dhjaks dhkj ";
        final int sessionId = 123123123;

        User user = new User( -1, "", "", "", sessionId, sessionCode, false );
        
        assertNotNull( user );
        assertEquals( sessionId, user.getSessionId() );
        assertEquals( sessionCode, user.getSessionCode() );

    }

    public void testSaveNewUser() throws Exception {
        
        final TestDatabase db = new TestDatabase();
        final String name = Utils.getRandomString(20),
                     pass = Utils.getRandomString(20),
                     email = Utils.getRandomString(20);
        final User newUser = new User( name, pass, email, true );
        
        assertTableSize( db, "users", 0 );
        assertEquals( -1, newUser.getId() );

        newUser.save( db );

        assertTableSize( db, "users", 1 );
        assertEquals( 0, newUser.getId() );

    }

    public void testFindReturnsAUserByTheirIdWhenTheyExist() throws Exception {
        User u2 = User.find( db, user.getId() );
        assertEquals( user.getId(), u2.getId() );
    }

    public void testFindReturnsAUserWithAllItsFieldsSet() {
        User u2 = User.find( db, user.getId() );
        assertEquals( user.getId(), u2.getId() );
        assertEquals( user.getName(), u2.getName() );
        assertEquals( user.getEmail(), u2.getEmail() );
        assertEquals( user.isAdmin(), u2.isAdmin() );
    }
    
    public void testFindReturnsNullWhenAUserDoesntExist() {
        assertNull( User.find(db,99999) );
    }

    public void testUserIsCreatedAsAdminWhenIsAdminIsTrue() throws Exception {
        User u1 = new User( "foo", "bar", "foo@bar.com", true );
        u1.save( db );
        User u2 = User.find( db, u1.getId() );
        assertTrue( u2.isAdmin() );
    }

    public void testUserIsNotCreatedAsAdminWhenIsAdminIsFalse() throws Exception {
        User u1 = new User( "foo", "bar", "foo@bar.com", false );
        u1.save( db );
        User u2 = User.find( db, u1.getId() );
        assertFalse( u2.isAdmin() );
    }

    public void testAUserCanBeDeletedById() throws Exception {
        User.delete( db, user.getId() );
        assertNull( User.find(db,user.getId()) );
    }

    public void testAUsersPlaylistsAndItsTracksAreDeletedWhenTheyAreDeleted() throws Exception {
        db.update( " insert into playlists ( name, user_id, date_created, date_modified ) values ( 'test', '" +user.getId()+ "', '2011-01-01 00:00:00', '2011-01-01 00:00:00' )" );
        db.update( " insert into playlist_tracks ( playlist_id, track_id ) values ( 0, 1 )" );
        db.update( " insert into playlist_tracks ( playlist_id, track_id ) values ( 0, 2 )" );
        assertTableSize( db, "playlists", 1 );
        assertTableSize( db, "playlist_tracks", 2 );
        User.delete( db, user.getId() );
        assertTableSize( db, "playlists", 0 );
        assertTableSize( db, "playlist_tracks", 0 );
    }

    public void testTrueIsReturnedWhenAUserIsDeleted() throws Exception {
        assertTrue( User.delete( db, user.getId() ) );
    }

    public void testFalseIsReturnedWhenNonExistantUserIdIsGivenToDelete() throws Exception {
        assertFalse( User.delete( db, 9999 ) );
    }

    public void testUpdateWillSaveTheUserDetailsToTheDatabase() throws Exception {
        User u2 = new User( user.getId(), "another", "foo@bar.com", false );
        u2.setActive( false );
        u2.update( db );
        u2 = User.find( db, user.getId() );
        assertEquals( "another", u2.getName() );
        assertEquals( "foo@bar.com", u2.getEmail() );
        assertFalse( u2.isAdmin() );
        assertFalse( u2.isActive() );
    }
    
}
