/*
 * ValidaterTest.java
 * 
 * Created on Aug 19, 2007, 1:14:32 PM
 * 
 * Tests the validation methods
 * 
 */

package com.pugh.sockso;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;

import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.easymock.EasyMock.*;

public class ValidaterTest extends SocksoTestCase {
    
    private Validater v;
    
    @Override
    protected void setUp() {
        this.v = new Validater( createMock(Database.class) );
    }

    public void testCheckRequiredFieldsTextCmps() {
        
        JTextComponent[] valid = {
            new JTextField( "data" )
        };
        JTextComponent[] invalid = {
            new JTextField( "" )
        };
        
        assertTrue( v.checkRequiredFields(valid) );
        assertFalse( v.checkRequiredFields(invalid) );

    }
    
    public void testCheckRequiredFieldsStrings() {

        String[] valid = {
            "data"
        };
        String[] invalid = {
            "data", ""
        };
        
        assertTrue( v.checkRequiredFields(valid) );
        assertFalse( v.checkRequiredFields(invalid) );

    }
    
    public void testIsValidEmail() {

        assertTrue( v.isValidEmail("foo@bar.com") );
        assertTrue( v.isValidEmail("foo.bar@baz.net") );
        assertTrue( v.isValidEmail("wikibar_me@mp.ar-pa.it") );
        
        assertFalse( v.isValidEmail("w p@bom.com") );
        assertFalse( v.isValidEmail("wwww@@halo.net") );
        assertFalse( v.isValidEmail("asd@asd. com") );
        
    }
    
    public void testEmailAddressCanStartWithANumber() {
        
        assertTrue( v.isValidEmail("13cats@gmail.com") );
        
    }
    
    public void testUsernameExists() throws Exception {

        TestDatabase db = new TestDatabase();

        Validater v = new Validater( db );
        final String email = "rod@pu-gh.com";

        assertFalse( v.emailExists(email) );

        db.fixture( "singleUser" );

        assertTrue( v.emailExists(email) );
        
    }
    
    public void testEmailExists() throws Exception {

        TestDatabase db = new TestDatabase();

        Validater v = new Validater( db );
        final String username = "foo";

        assertFalse( v.usernameExists(username) );

        db.fixture( "singleUser" );

        assertTrue( v.usernameExists(username) );
        
    }

}
