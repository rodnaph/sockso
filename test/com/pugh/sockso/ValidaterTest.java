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

import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import static org.easymock.EasyMock.*;

public class ValidaterTest extends SocksoTestCase {

    public void testCheckRequiredFieldsTextCmps() {
        
        Validater v = new Validater( createMock(Database.class) );
        
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

        Validater v = new Validater( createMock(Database.class) );
        
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

        Database db = createNiceMock( Database.class );
        Validater v = new Validater( db );
        
        assertTrue( v.isValidEmail("foo@bar.com") );
        assertTrue( v.isValidEmail("foo.bar@baz.net") );
        assertTrue( v.isValidEmail("wikibar_me@mp.ar-pa.it") );
        
        assertFalse( v.isValidEmail("w p@bom.com") );
        assertFalse( v.isValidEmail("wwww@@halo.net") );
        assertFalse( v.isValidEmail("asd@asd. com") );
        
    }
    
    public void testUsernameExists() {
        
    }
    
    public void testEmailExists() {
        
    }

}
