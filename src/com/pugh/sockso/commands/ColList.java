
package com.pugh.sockso.commands;

import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ColList extends BaseCommand {

    private final Database db;
    
    public ColList( final Database db ) {

        this.db = db;
        
    }

    public String getName() {
        
        return "collist";

    }
    
    public String getDescription() {
        
        return "Lists the folders in the collection";

    }

    /**
     *  handles the CMD_COLLIST command to list the
     *  directories that are in the collection
     *
     *  @throws SQLException
     *
     */

    public String execute( final String[] args ) throws SQLException {

        final StringBuffer sb = new StringBuffer();

        PreparedStatement st = null;
        ResultSet rs = null;

        try {

            final String sql = " select c.id, c.path " +
                               " from collection c " +
                               " order by c.path asc ";

            st = db.prepare( sql );
            rs = st.executeQuery();

            sb.append( " ID  PATH\n" );
            sb.append( "----------\n" );

            while ( rs.next() ) {
                sb.append( " [" +rs.getString("id")+ "] " +rs.getString("path") + "\n" );
            }

        }

        finally {
            Utils.close( rs );
            Utils.close( st );
            return sb.toString();
        }

    }

}
