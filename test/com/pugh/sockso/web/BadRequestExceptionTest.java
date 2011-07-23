
package com.pugh.sockso.web;

import junit.framework.TestCase;

public class BadRequestExceptionTest extends TestCase {
    
    public void testConstructor() {
        
        assertNotNull( new BadRequestException("my error message") );
        assertNotNull( new BadRequestException("my error message",500) );
        assertNotNull( new BadRequestException(new String[] { "my error message" } ) );
        assertNotNull( new BadRequestException(new String[] { "my error message" }, 500) );
        
    }

    public void testGetSingleMessage() {
        
        final String message = "foo bar";
        final BadRequestException e = new BadRequestException( message );
        
        assertEquals( e.getMessages().length, 1 );
        assertEquals( e.getMessages()[0], message );
        
    }
    
    public void testGetMessages() {
        
        final String[] messages = { "foo", "bar" };
        final BadRequestException e = new BadRequestException( messages );
        
        assertEquals( e.getMessages(), messages );
        
    }
    
    public void testGetStatusCode() {
        
        final int statusCode = 499;
        final BadRequestException e = new BadRequestException( "", statusCode );
        
        assertEquals( e.getStatusCode(), statusCode );
        
    }

    public void testGetMessageReturnsFirstMessage() {
        assertEquals( "foo far", new BadRequestException("foo far").getMessage() );
        assertEquals( "boo far", new BadRequestException("boo far",404).getMessage() );
        assertEquals( "foo", new BadRequestException(new String[] { "foo", "far" }).getMessage() );
    }

}
