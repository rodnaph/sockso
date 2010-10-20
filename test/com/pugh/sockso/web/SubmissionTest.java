
package com.pugh.sockso.web;

import com.pugh.sockso.resources.Locale;

import junit.framework.TestCase;

import static org.easymock.EasyMock.*;

public class SubmissionTest extends TestCase {

    private Locale locale;
    
    public void setUp() {
        
        locale = createNiceMock( Locale.class );
        
    }
    
    public void testConstructor() {
        
        final Request req = createMock( Request.class );
        replay( req );
        
        final Submission s = new Submission( req, locale );
        
        verify( req );
        
    }
    
    public void testAddField() {
        
        final Request req = createMock( Request.class );
        expect( req.getArgument("name") ).andReturn( "" ).times( 1 );
        replay( req );
        
        final Submission s = new Submission( req, locale );
        
        s.addField( "name", Submission.FIELD_EMAIL );
        
        verify( req );
        
    }
    
    public void testAddFieldWithRequired() {
        
        final Request req = createMock( Request.class );
        expect( req.getArgument("name") ).andReturn( "" ).times( 1 );
        replay( req );
        
        final Submission s = new Submission( req, locale );
        
        s.addField( "name", Submission.FIELD_DATE, false );
        
        verify( req );
        
    }
    
    public void testAddFieldWithLocale() {
        
        final Request req = createMock( Request.class );
        expect( req.getArgument("name") ).andReturn( "" ).times( 1 );
        replay( req );
        
        final Submission s = new Submission( req, locale );
        
        s.addField( "name", Submission.FIELD_DATE, "errNoName" );
        
        verify( req );
        
    }
    
    public void testIsValid() {
        
        final Request req = createMock( Request.class );
        expect( req.getArgument("name") ).andReturn( "foo" ).times( 1 );
        expect( req.getArgument("myId") ).andReturn( "123" ).times( 1 );
        expect( req.getArgument("theDate") ).andReturn( "01/02/2004" ).times( 1 );
        expect( req.getArgument("email") ).andReturn( "foo@bar.com" ).times( 1 );
        replay( req );
        
        final Submission s = new Submission( req, locale );
        s.addField( "name", Submission.FIELD_TEXT );
        s.addField( "myId", Submission.FIELD_INTEGER );
        s.addField( "theDate", Submission.FIELD_DATE );
        s.addField( "email", Submission.FIELD_EMAIL );
        
        assertTrue( s.isValid() );
        
        verify( req );
        
    }
    
    public void testInvalidRequiredEmail() {
        
        final Request req = createMock( Request.class );
        expect( req.getArgument("email") ).andReturn( "A BAD EMAIL" ).times( 1 );
        replay( req );
        
        final Submission s = new Submission( req, locale );
        s.addField( "email", Submission.FIELD_EMAIL );
        
        assertFalse( s.isValid() );
        
        verify( req );
        
    }
 
    public void testMissingRequiredField() {
        
        final Request req = createMock( Request.class );
        expect( req.getArgument("name") ).andReturn( "" ).times( 1 );
        replay( req );
        
        final Submission s = new Submission( req, locale );
        s.addField( "name", Submission.FIELD_EMAIL );
        
        assertFalse( s.isValid() );
        
        verify( req );
        
    }

    public void testBlankOptionalFields() {
        
        final Request req = createMock( Request.class );
        expect( req.getArgument("email") ).andReturn( "" ).times( 1 );
        expect( req.getArgument("int") ).andReturn( "" ).times( 1 );
        expect( req.getArgument("text") ).andReturn( "" ).times( 1 );
        expect( req.getArgument("date") ).andReturn( "" ).times( 1 );
        replay( req );
        
        final Submission s = new Submission( req, locale );
        s.addField( "email", Submission.FIELD_EMAIL, false );
        s.addField( "int", Submission.FIELD_INTEGER, false );
        s.addField( "text", Submission.FIELD_TEXT, false );
        s.addField( "date", Submission.FIELD_DATE, false );
        
        assertTrue( s.isValid() );
        
        verify( req );
        
    }

    public void testInvalidIntegerField() {
        
        final Request req = createMock( Request.class );
        expect( req.getArgument("myId") ).andReturn( "NOT AN INT" ).times( 1 );
        replay( req );
        
        final Submission s = new Submission( req, locale );
        s.addField( "myId", Submission.FIELD_INTEGER );
        
        assertFalse( s.isValid() );
        
        verify( req );
        
    }

    public void testInvalidDateField() {
        
        final Request req = createMock( Request.class );
        expect( req.getArgument("theDate") ).andReturn( "NOT A DATE" ).times( 1 );
        replay( req );
        
        final Submission s = new Submission( req, locale );
        s.addField( "theDate", Submission.FIELD_DATE );
        
        assertFalse( s.isValid() );
        
        verify( req );
        
    }
    
    public void testGetErrors() {
        
        final Locale loc = createMock( Locale.class );
        expect( loc.getString("errMyKey") ).andReturn( "foo" ).times( 1 );
        replay( loc );
        
        final Request req = createMock( Request.class );
        expect( req.getArgument("theDate") ).andReturn( "NOT A DATE" ).times( 1 );
        replay( req );
        
        final Submission s = new Submission( req, loc );
        s.addField( "theDate", Submission.FIELD_DATE, "errMyKey" );
        
        final String[] errors = s.getErrors();
        
        assertEquals( errors.length, 1 );
        assertEquals( "foo", errors[0] );
        
        verify( req );
        verify( loc );
        
    }
    
    public void testValidate() {
        
        final Request req = createMock( Request.class );
        expect( req.getArgument("email") ).andReturn( "good@email.com" ).times( 1 );
        replay( req );
        
        final Submission s = new Submission( req, locale );
        s.addField( "email", Submission.FIELD_EMAIL );
        
        boolean gotException = false;
        
        try {
            s.validate();
        }
        catch ( final BadRequestException e ) {
            gotException = true;
        }
        
        assertFalse( gotException );
        
    }

    public void testValidateWithException() {
        
        final Request req = createMock( Request.class );
        expect( req.getArgument("email") ).andReturn( "BAD EMAIL" ).times( 1 );
        replay( req );
        
        final Submission s = new Submission( req, locale );
        s.addField( "email", Submission.FIELD_EMAIL );
        
        boolean gotException = false;
        
        try {
            s.validate();
        }
        catch ( final BadRequestException e ) {
            gotException = true;
        }
        
        assertTrue( gotException );
        
    }

    public void testValidatingThatTwoFieldsMatch() {
        final Request req = createMock( Request.class );
        expect( req.getArgument("pass1") ).andReturn( "one" ).times( 1 );
        expect( req.getArgument("pass2") ).andReturn( "one" ).times( 1 );
        replay( req );
        Submission s = new Submission( req, locale );
        boolean gotException = false;
        s.addMatchingFields( "pass1", "pass2", "" );
        try {
            s.validate();
        }
        catch ( BadRequestException e ) {
            gotException = true;
        }
        assertFalse( gotException );
    }

    public void testValidatingThatTwoFieldsDontMatch() {
        final Request req = createMock( Request.class );
        expect( req.getArgument("pass1") ).andReturn( "one" ).times( 1 );
        expect( req.getArgument("pass2") ).andReturn( "two" ).times( 1 );
        replay( req );
        Submission s = new Submission( req, locale );
        boolean gotException = false;
        s.addMatchingFields( "pass1", "pass2", "" );
        try {
            s.validate();
        }
        catch ( BadRequestException e ) {
            gotException = true;
        }
        assertTrue( gotException );
    }

}
