/*
 *  An error has occurred trying to connect to the database.
 * 
 */

package com.pugh.sockso.db;

public class DatabaseConnectionException extends Exception {

    /**
     *  constructor
     * 
     *  @param message
     * 
     */
    
    public DatabaseConnectionException( final String message ) {
        super( message );
    }
    
}
