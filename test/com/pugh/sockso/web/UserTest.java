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
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;

public class UserTest extends SocksoTestCase {
    
    public void testConstructor() {
        
        String name = "ashdjkas dhjaks dhkj ";
        int id = 123123123;
        
        User user = new User( id, name );
        
        assertNotNull( user );
        assertEquals( id, user.getId() );
        assertEquals( name, user.getName() );
        assertEquals( "", user.getEmail() );
        
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

        User user = new User( -1, "", "", "", sessionId, sessionCode );
        
        assertNotNull( user );
        assertEquals( sessionId, user.getSessionId() );
        assertEquals( sessionCode, user.getSessionCode() );

    }

    public void testSaveNewUser() throws Exception {
        
        final TestDatabase db = new TestDatabase();
        final String name = Utils.getRandomString(20),
                     pass = Utils.getRandomString(20),
                     email = Utils.getRandomString(20);
        final User newUser = new User( -1, name, pass, email );
        
        assertTableSize( db, "users", 0 );
        assertEquals( -1, newUser.getId() );

        newUser.save( db );

        assertTableSize( db, "users", 1 );
        assertEquals( 0, newUser.getId() );

    }
    
}
