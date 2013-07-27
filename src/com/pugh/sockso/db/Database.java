/*
 * SocksoDatabase.java
 * 
 * Created on Jul 22, 2007, 10:58:04 AM
 * 
 * The interface to the database.
 * 
 */

package com.pugh.sockso.db;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import joptsimple.OptionSet;

public interface Database {

    /**
     *  connects to the database
     * 
     *  @param options
     * 
     */
    
    public void connect( final OptionSet options ) throws DatabaseConnectionException;
    
    /**
     *  performs an update on the database, and returns a boolean
     *  indicating if it worked or not
     * 
     *  @param sql the sql to execute
     *  @return boolean indicating success
     * 
     *  @throws SQLException
     * 
     */
    
    public int update( final String sql ) throws SQLException;
    
    /**
     *  shuts down the database and closes the connection
     * 
     */
    
    public void close();
    
    /**
     *  quotes a string for safe inclusion in sql
     * 
     *  @param str the string to quote
     *  @return the safely escaped string
     * 
     *  @TODO this needs to be removed in favour of prepared statements
     * 
     */
    
    public String escape( final String str );

    /**
     *  returns the database connection being used to this database
     * 
     *  @return
     * 
     */
    
    public Connection getConnection();
    
    /**
     *  returns a prepared statement for the specified sql
     * 
     *  @param sql
     * 
     *  @return Statement
     * 
     */
    
    public PreparedStatement prepare( final String sql ) throws SQLException;
    
    /**
     *  returns the name of the databases random function
     * 
     *  @return
     * 
     */
    
    public String getRandomFunction();

}
