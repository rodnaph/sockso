/*
 * Validate.java
 * 
 * Created on Aug 19, 2007, 12:52:43 PM
 * 
 * A class for validating input.
 * 
 */

package com.pugh.sockso;

import com.pugh.sockso.db.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

public class Validater {

    private static final Logger log = Logger.getLogger( Validater.class );
    
    private final Database db;
    
    public Validater( Database db ) {
        this.db = db;
    }

    public boolean checkRequiredFields( final JTextComponent[] fields ) {
        
        final String[] values = new String[ fields.length ];
        
        for ( int i=0; i<fields.length; i++ )
            values[ i ] = fields[ i ].getText();
        
        return checkRequiredFields( values );
        
    }

    public boolean checkRequiredFields( final String[] fields ) {
        
        for ( int i=0; i<fields.length; i++ )
            if ( fields[i].trim().equals("") )
                return false;
        
        return true;
        
    }
    
    /**
     *  checks if the given email address seems to be valid
     * 
     *  @param email the email to check
     *  @return true if it looks ok, false otherwise
     * 
     */
    
    public boolean isValidEmail( final String email ) {

        final String pattern = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";

        return email.matches( pattern );

    }

    /**
     *  checks if a username exists in the database
     * 
     *  @param name the name to check
     *  @return true if it does, false otherwise
     * 
     */
    
    public boolean usernameExists( final String name ) {
       
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
            
            final String sql = " select 1 " +
                         " from users u " +
                         " where name like ? ";
            
            st = db.prepare( sql );
            st.setString( 1, name.trim() );
            rs = st.executeQuery();

            return rs.next();
        
        }

        catch ( final SQLException e ) {
            log.error( e );
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
        return false;
        
    }

    /**
     *  checks if an email exists in the database
     * 
     *  @param name the email to check
     *  @return true if it does, false otherwise
     * 
     */
    
    public boolean emailExists( final String email ) {
       
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
            
            final String sql = " select 1 " +
                               " from users u " +
                               " where email like ? ";
            
            st = db.prepare( sql );
            st.setString( 1, email.trim() );
            rs = st.executeQuery();

            return rs.next();
        
        }

        catch ( final SQLException e ) {
            log.error( e );
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
        return false;
        
    }

}
