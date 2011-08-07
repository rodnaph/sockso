
package com.pugh.sockso.auth;

import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;

import java.sql.ResultSet;
import java.sql.PreparedStatement;

import com.google.inject.Inject;

/**
 * This class authenticates users from the database
 *
 */
public class DBAuthenticator implements Authenticator {

    private final Database db;

    /**
     *  Constructor
     *
     *  @param db
     * 
     */
    
    @Inject
    public DBAuthenticator( final Database db ) {

        this.db = db;

    }

    /**
     * Authenticates a user from the database and returns a boolean
     *
     * @param name
     * @param pass
     *
     * @return true if valid, false otherwise
     *
     * @throws Exception
     */
    public boolean authenticate( final String name, final String pass ) throws Exception {
        
        ResultSet rs = null;
        PreparedStatement st = null;

        try {

            String sql = " select u.id as id, u.pass as pass " +
                         " from users u " +
                         " where u.name = ? " +
                             " and u.is_active = '1' " +
                         " limit 1 ";
            st = db.prepare( sql );
            st.setString( 1, name );
            rs = st.executeQuery();

            if ( !rs.next() || !Utils.md5(pass).equals(rs.getString("pass")) ) {
                return false;
            }

            return true;

        }

        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
    }

}
