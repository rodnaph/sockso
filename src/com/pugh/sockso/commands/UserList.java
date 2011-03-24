
package com.pugh.sockso.commands;

import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserList extends BaseCommand {

    private final Database db;
    
    public UserList( final Database db ) {
        
        this.db = db;
        
    }

    public String getName() {
        
        return "userlist";
        
    }
    
    public String getDescription() {
        
        return "Lists the users";
        
    }
    
    public String execute( final String[] args ) throws SQLException {
        
        final StringBuffer sb = new StringBuffer();

        PreparedStatement st = null;
        ResultSet rs = null;

        try {

            final String sql = " select id, name, email, is_admin, is_active " +
                               " from users " +
                               " order by name asc ";

            st = db.prepare( sql );
            rs = st.executeQuery();

            while ( rs.next() ) {
                sb.append( rs.getString("id")  );
                sb.append( "\t" );
                sb.append( rs.getString("name") );
                sb.append( "\t" );
                sb.append( rs.getString("email") );
                sb.append( "\t" );
                sb.append( rs.getBoolean("is_admin") ? "ADMIN" : "" );
                sb.append( "\t" );
                sb.append( rs.getBoolean("is_active") ? "" : "PENDING" );
                sb.append( "\n" );
            }

            return sb.toString();

        }

        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
    }

    @Override
    public int getNumArgs() {

        return 0;
        
    }

}
