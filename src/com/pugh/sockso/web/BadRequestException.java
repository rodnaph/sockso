/**
 * BadRequestException.java
 *
 * Created on May 9, 2007, 12:33 AM
 *
 * indicates something has been found to be wrong with the request
 * 
 */

package com.pugh.sockso.web;

public class BadRequestException extends Exception {
    
    private static final int DEFAULT_STATUS_CODE = 200;
    
    private final int statusCode;
    
    private String[] messages;
    
    /**
     *  Creates a new instance of BadRequestException
     *
     *  @param message a description of the exception
     *
     */
    
    public BadRequestException( final String message ) {
        
        this( new String[] { message } );
        
    }
    
    /**
     *  creates an error with a bunch of messages
     * 
     *  @param messages
     * 
     */
    
    public BadRequestException( final String[] messages ) {
        
        this( messages, DEFAULT_STATUS_CODE );
        
    }
        
    /**
     *  creates class with message and a http status code for the exception
     * 
     *  @param message
     *  @param statusCode
     * 
     */
    
    public BadRequestException( final String message, final int statusCode ) {
        
        this( new String[] { message }, statusCode );
        
    }
    
    /**
     *  creates an exception with a bunch of messages and a status code
     * 
     *  @param messages
     *  @param statusCode
     * 
     */
    
    public BadRequestException( final String[] messages, final int statusCode ) {
        
        this.messages = messages;
        this.statusCode = statusCode;
        
    }

    /**
     *  returns the status code of the exception
     * 
     *  @return
     * 
     */
    
    public int getStatusCode() {
        
        return statusCode;
        
    }
    
    /**
     *  returns all the messages for this exception
     * 
     *  @return
     * 
     */
    
    public String[] getMessages() {
        
        return messages;
        
    }
    
    /**
     *  Returns the error message, or the first if multiple were set
     * 
     *  @return 
     * 
     */
    
    @Override
    public String getMessage() {
        
        return messages[ 0 ];
        
    }
    
}
