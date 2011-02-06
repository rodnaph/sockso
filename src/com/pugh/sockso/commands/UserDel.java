
package com.pugh.sockso.commands;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.resources.Locale;

import java.sql.SQLException;

public class UserDel extends BaseCommand {

    private final Database db;
    
    private final Locale locale;
    
    public UserDel( final Database db, final Locale locale ) {
        
        this.db = db;
        this.locale = locale;
        
    }

    public String getName() {
        
        return "userdel";
        
    }
    
    public String getDescription() {
        
        return "Deletes a user";
        
    }
    
    /**
     *  handles the CMD_USERDEL to delete a user
     *
     *  @param args command arguments
     *
     */

    public String execute( final String[] args ) {

        try {

            final String sql = " delete from users " +
                               " where id = " + db.escape(args[1]);
            final int affectedRows = db.update( sql );

            return affectedRows == 1
                ? locale.getString( "con.msg.userDeleted" )
                : locale.getString( "con.err.errorDeletingUser" );

        }

        catch ( final SQLException e ) {
            return locale.getString( "con.err.errorDeletingUser" );
        }

    }

    @Override
    public int getNumArgs() {

        return 1;

    }


}
