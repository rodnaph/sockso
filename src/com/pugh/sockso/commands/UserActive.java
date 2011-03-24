
package com.pugh.sockso.commands;

import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.resources.Locale;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserActive extends BaseCommand {

    private final Locale locale;
    
    private final Database db;
    
    public UserActive( final Database db, final Locale locale ) {
        
        this.db = db;
        this.locale = locale;
        
    }

    /**
     *  Returns the command name
     *
     *  @return
     *
     */

    public String getName() {
        
        return "useractive";
        
    }

    /**
     *  Returns a description of the command
     *
     *  @return
     *
     */

    public String getDescription() {
        
        return "Toggles users between being active or not";
        
    }

    /**
     *  Executes the command to update a user as active or not
     *
     *  @param args
     *
     *  @return
     *
     *  @throws SQLException
     *
     */

    public String execute( final String[] args ) throws SQLException {
        
        PreparedStatement st = null;
        
        try {

            final String isActive = args[ 2 ].equals( "1" )
                ? "1"
                : "0";

            final String sql = " update users " +
                               " set is_active = ? " +
                               " where id = ? ";
            
            st = db.prepare( sql );
            st.setString( 1, isActive );
            st.setInt( 2, Integer.parseInt(args[1]) );

            final int affectedRows = st.executeUpdate();

            return ( affectedRows == 1 )
                ? locale.getString( "con.msg.userUpdated" )
                : locale.getString( "con.err.invalidUserId" );
            
        }
        
        finally {
            Utils.close( st );
        }
        
    }

    /**
     *  Returns the names of the command arguments
     *
     *  @return
     *
     */

    @Override
    public String[] getArguments() {
        
        return new String[] { "ID ISACTIVE (1/0)" };
        
    }

}
