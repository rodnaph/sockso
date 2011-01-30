
package com.pugh.sockso;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.CollectionManager;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.web.User;

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

    private final Locale locale;

    public CommandExecuter( final Database db, final Properties p, final CollectionManager cm, final Locale locale ) {

        this.db = db;
        this.p = p;
        this.cm = cm;
        this.locale = locale;

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
        else if (name.equals(CMD_USERADD)) { return cmdUserAdd(args); }

        return null;

    }

    /**
     *  Tries to add a new user
     *
     *  @param args
     *
     *  @return
     *
     *  @throws SQLException
     *
     */
    
    protected String cmdUserAdd( final String[] args ) throws SQLException {

        if ( args.length != 5 ) {
            return getCommands();
        }
        
        else {

            try {

                final Validater v = new Validater( db );
                final String name = args[ 1 ];
                final String pass = args[ 2 ];
                final String email = args[ 3 ];
                final String isAdmin = args[ 4 ];

                if ( v.usernameExists(name) ) {
                    throw new ValidationException( locale.getString("con.err.usernameExists") );
                }

                if ( v.emailExists(email) ) {
                    throw new ValidationException( locale.getString("con.err.emailExists") );
                }

                final User newUser = new User(
                    name,
                    pass,
                    email,
                    isAdmin.equals("1") ? true : false
                );

                newUser.save( db );

                return newUser.getId() != -1
                    ? locale.getString( "con.msg.userCreated")
                    : locale.getString( "con.err.errorCreatingUser");

            }

            catch ( final ValidationException e ) {
                return e.getMessage();
            }

        }

    }

    /**
     *  Lists current users
     *
     *  @return
     *
     *  @throws SQLException
     *
     */
    
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
                sb.append( rs.getString("id")  );
                sb.append( "\t" );
                sb.append( rs.getString("name") );
                sb.append( "\t" );
                sb.append( rs.getString("email") );
                sb.append( "\t" );
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

    protected String getCommands() {

        final StringBuffer sb = new StringBuffer();
        
        sb.append( locale.getString("con.desc.commands") );
        sb.append( CMD_COLADD + " PATH                            " + locale.getString("con.desc.addDirectory") );
        sb.append( CMD_COLDEL + " PATH                            " + locale.getString("con.desc.removeDirectory") );
        sb.append( CMD_COLLIST + "                                " + locale.getString("con.desc.listDirectories") );
        sb.append( CMD_COLSCAN + "                                " + locale.getString("con.desc.rescanCollection") );
        sb.append( CMD_EXIT + "                                   " + locale.getString("con.desc.exit") );
        sb.append( CMD_PROPLIST + " FILTER                        " + locale.getString("con.desc.listProperties") );
        sb.append( CMD_PROPSET + " NAME VALUE                     " + locale.getString("con.desc.setProperty") );
        sb.append( CMD_PROPDEL + " NAME                           " + locale.getString("con.desc.delProperty") );
        sb.append( CMD_USERLIST + "                               " + locale.getString("con.desc.listUsers") );
        sb.append( CMD_USERADD + " NAME PASS EMAIL ISADMIN (1/0)  " + locale.getString("con.desc.addUser") );
        sb.append( CMD_USERDEL + " ID                             " + locale.getString("con.desc.deleteUser") );
        sb.append( CMD_USERADMIN + " ID ISADMIN (1/0)             " + locale.getString("con.desc.adminUser") );
        sb.append( CMD_VERSION + "                                " + locale.getString("con.desc.version") );
        sb.append( "" );

        return sb.toString();

    }

}
