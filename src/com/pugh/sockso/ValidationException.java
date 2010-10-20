/*
 * ValidationError.java
 * 
 * Created on Aug 19, 2007, 1:18:12 PM
 * 
 * A validation error has occurred
 * 
 */

package com.pugh.sockso;

public class ValidationException extends Exception {

    /**
     *  constructor
     * 
     *  @param message
     * 
     */
    
    public ValidationException( final String message ) {
        super( message );
    }

}
