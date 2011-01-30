
package com.pugh.sockso;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.CollectionManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *  Executes console commands
 *
 *  @todo:
 *    coladd
 *    coldel
 *    collist
 *    colscan
 *    exit
 *    propset
 *    proplist
 *    propdel
 *    useradd
 *    userdel
 *    useradmin
 *    version
 *
 */

public class CommandExecuter {

    protected static final String CMD_COLADD = "coladd";
    protected static final String CMD_COLDEL = "coldel";
    protected static final String CMD_COLLIST = "collist";
    protected static final String CMD_COLSCAN = "colscan";
    protected static final String CMD_EXIT = "exit";
    protected static final String CMD_PROPSET = "propset";
    protected static final String CMD_PROPLIST = "proplist";
    protected static final String CMD_PROPDEL = "propdel";
    protected static final String CMD_USERLIST = "userlist";
    protected static final String CMD_USERADD = "useradd";
    protected static final String CMD_USERDEL = "userdel";
    protected static final String CMD_USERADMIN = "useradmin";
    protected static final String CMD_VERSION = "version";

    private final Database db;
    
    private final Properties p;
    
    private final CollectionManager cm;

    public CommandExecuter( final Database db, final Properties p, final CollectionManager cm ) {

        this.db = db;
        this.p = p;
        this.cm = cm;

    }

    /**
     *  Executes a command and returns the results
     *
     *  @param command
     *
     *  @return
     *
     */

    public String execute( final String command ) throws SQLException {

        final String[] args = getArgs( command );
        final String name = args.length > 0 ? args[0] : "";

        if ( name.equals(CMD_USERLIST) ) { return cmdUserList(); }
        else if (name.equals(CMD_USERLIST)) { return cmdUserAdd(args); }

        return null;

    }

    protected String cmdUserAdd( final String[] args ) {

        return "";

    }
    
    protected String cmdUserList() throws SQLException {

        final StringBuffer sb = new StringBuffer();

        PreparedStatement st = null;
        ResultSet rs = null;

        try {

            final String sql = " select id, name, email, is_admin " +
                               " from users " +
                               " order by name asc ";

            st = db.prepare( sql );
            rs = st.executeQuery();

            while ( rs.next() ) {
                sb.append( rs.getString("id") + "\t" );
                sb.append( rs.getString("name") + "\t" );
                sb.append( rs.getString("email") + "\t" );
                sb.append( rs.getBoolean("is_admin") ? "ADMIN" : "" );
                sb.append( "\n" );
            }

            return sb.toString();

        }

        finally {
            Utils.close( rs );
            Utils.close( st );
        }

    }

    /**
     *  Returns the arguments to use for the command
     *
     *  @param command
     *
     *  @return
     *
     */

    protected String[] getArgs( final String command ) {

        return command.split( " " );

    }

}
