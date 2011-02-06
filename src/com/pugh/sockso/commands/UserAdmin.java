
package com.pugh.sockso.commands;

import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.resources.Locale;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class UserAdmin extends BaseCommand {

    private Database db;
    
    private Locale locale;

    private static final Logger log = Logger.getLogger( UserAdmin.class );
    
    public UserAdmin( final Database db, final Locale locale ) {
        
        this.db = db;
        this.locale = locale;
        
    }

    public String getName() {

        return "useradmin";

    }

    public String getDescription() {

        return "Sets a user to be admin/non-admin";
        
    }

    /**
     *  Changes a user between admin/non-admin
     *
     *  @param args
     *
     *  @return
     *
     */

    public String execute( final String[] args ) {

        PreparedStatement st = null;

        final int id = Integer.parseInt( args[1] );
        final int isAdmin = Integer.parseInt( args[2] );
        final String sql = " update users " +
                           " set is_admin = ? " +
                           " where id = ? ";

        try {
            st = db.prepare( sql );
            st.setInt( 1, isAdmin );
            st.setInt( 2, id );
            return st.executeUpdate() == 1
                ? locale.getString( "con.msg.userUpdated" )
                : locale.getString( "con.err.errorUpdatingUser" );
        }

        catch ( final SQLException e ) {
            log.error( e );
            return locale.getString( "com.err.errorUpdatingUser" );
        }

        finally {
            Utils.close( st );
        }

    }

    @Override
    public int getNumArgs() {

        return 2;

    }


}
