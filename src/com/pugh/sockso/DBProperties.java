
package com.pugh.sockso;

import com.pugh.sockso.db.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 *  Adds persistence by DB to the StringProperties class
 * 
 */

@Singleton
public class DBProperties extends StringProperties {
    
    private final Database db;
    
    /**
     *  creates the properties object.
     * 
     *  @param db the database connection
     * 
     */

    @Inject
    public DBProperties( final Database db ) {

        this.db = db;

    }
    
    /**
     *  inits the object
     * 
     *  @throws java.sql.SQLException
     * 
     */

    @Override
    public void init() throws SQLException {

        refresh();
        
        new Thread() {
            @Override
            public void run() {
                
                final int THIRTY_SECONDS = 30000;
                
                while ( true ) {

                    try { Thread.sleep(THIRTY_SECONDS); }
                        catch ( final InterruptedException e ) { /* ignore */ }

                    try { refresh(); }
                        catch ( final SQLException e ) {
                            log.debug( e );
                        }

                }

            }
        }.start();

    }

    /**
     *  refreshes the properties from the database
     * 
     *  @throws java.sql.SQLException
     * 
     */
    
    private void refresh() throws SQLException {

        ResultSet rs = null;
        PreparedStatement st = null;
        
        try {
        
            final String sql = " select p.name as name, p.value as value " +
                               " from properties p ";
            
            st = db.prepare( sql );
            rs = st.executeQuery();

            while ( rs.next() ) {
                set( rs.getString("name"), rs.getString("value") );
            }

        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }

    }

    /**
     *  saves any changes to the properties so they are written
     *  to the database.
     * 
     */

    @Override
    public void save() {
        
        PreparedStatement st = null;
        
        try {

            // @TODO use a batch transaction here?
            
            // delete old properties
            String sql = " delete from properties "; 
            
            st = db.prepare( sql );
            st.execute();
            
            Utils.close( st );

            // insert new properties
            for ( final String name : data.keySet() ) {
                
                sql = " insert into properties ( name, value ) " +
                      " values ( ?, ? ) ";
                
                st = db.prepare( sql );
                st.setString( 1, name );
                st.setString( 2, get(name) );
                st.execute();
                
                Utils.close( st );
                
            }

            firePropertiesSavedEvent();
            
        }
        
        catch ( final SQLException e ) {
            log.error( e );
        }
        
        finally {
            Utils.close( st );
        }

    }

}

