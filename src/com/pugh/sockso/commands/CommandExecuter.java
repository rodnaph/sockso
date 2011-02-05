
package com.pugh.sockso.commands;

import com.pugh.sockso.Main;
import com.pugh.sockso.Properties;
import com.pugh.sockso.Sockso;
import com.pugh.sockso.Utils;
import com.pugh.sockso.Validater;
import com.pugh.sockso.ValidationException;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.CollectionManager;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.web.User;
import java.io.File;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Arrays;

import org.apache.log4j.Logger;

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

    private static final Logger log = Logger.getLogger( CommandExecuter.class );
    
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
        else if (name.equals(CMD_USERDEL)) { return cmdUserDel(args); }
        else if (name.equals(CMD_USERADMIN)) { return cmdUserAdmin(args); }

        else if (name.equals(CMD_COLADD)) { return cmdColAdd(args); }
        else if (name.equals(CMD_COLDEL)) { return cmdColDel(args); }
        else if (name.equals(CMD_COLLIST)) { return cmdColList(); }
        else if (name.equals(CMD_COLSCAN)) { return cmdColScan(); }

        else if (name.equals(CMD_PROPDEL)) { return cmdPropDel(args); }
        else if (name.equals(CMD_PROPLIST)) { return cmdPropList(args); }
        else if (name.equals(CMD_PROPSET)) { return cmdPropSet(args); }

        else if (name.equals(CMD_EXIT)) { return cmdExit(); }
        else if (name.equals(CMD_VERSION)) { return cmdVersion(); }

        return getCommands();

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
     *  handles the CMD_USERDEL to delete a user
     *
     *  @param args command arguments
     *
     */

    protected String cmdUserDel( final String[] args ) {

        if ( args.length != 2 ) {
            return getCommands();
        }

        else {

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

    }

    /**
     *  Changes a user between admin/non-admin
     *
     *  @param args
     *
     *  @return
     *
     */

    protected String cmdUserAdmin( final String[] args ) {

        if ( args.length != 3 ) {
            return getCommands();
        }

        else {

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

    }

    /**
     *  handles the CMD_COLDEL command to remove a
     *  directory from the collection
     *
     *  @param args command arguments
     *
     */

    protected String cmdColDel( final String[] args ) {

        if ( args.length < 2 ) {
            return getCommands();
        }
        
        else {

            final String path = args[ 1 ];

            return cm.removeDirectory( path )
                ? locale.getString( "con.msg.directoryDeleted" )
                : locale.getString( "con.err.directoryNotInColl" );

        }

    }

    /**
     *  handles the CMD_COLADD command, adds a directory
     *  to the collection
     *
     *  @param args command arguments
     *
     */

    protected String cmdColAdd( final String[] args ) {

        if ( args.length < 2 ) {
            return getCommands();
        }
        
        else {

            final String path = args[ 1 ];
            final File file = new File( path );

            if ( file.exists() ) {
                cm.addDirectory( file );
                return locale.getString("con.msg.directoryAdded");
            }
            else {
                return locale.getString("con.err.pathNotExist",new String[] {path});
            }

        }

    }

    /**
     *  handles the CMD_COLLIST command to list the
     *  directories that are in the collection
     *
     *  @throws SQLException
     *
     */

    protected String cmdColList() throws SQLException {

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

    /**
     *  handles command to scan the collection now
     *
     */

    protected String cmdColScan() {

        cm.checkCollection();

        return "Scanning collection...";

    }

    /**
     *  handles the CMD_PROPSET command to set a
     *  particular application property
     *
     *  @param args command arguments
     *
     */

    protected String cmdPropSet( final String[] args ) {

        if ( args.length < 3 ) {
            return getCommands();
        }

        else {

            final String name = args[ 1 ];
            final String value = Utils.joinArray( args, " ", 2, args.length - 1 );

            p.set( name, value );
            p.save();

            return locale.getString( "con.msg.propertySaved" );

        }

    }

    /**
     *  handles the CMD_PROPLIST command to list the
     *  applications properties
     *
     */

    protected String cmdPropList( String[] args ) {

        final StringBuffer sb = new StringBuffer();
        final String pattern = ( args.length > 1 ) ? args[1] : null;
        final String[] props = p.getProperties();
        final int longest = getLongestStringLength( props );

        Arrays.sort( props );

        // print header

        sb.append( " NAME" +getPadding(4,longest)+ "  VALUE\n" );
        if ( pattern != null ) {
            sb.append( "\n (containing '" +pattern+ "')\n" );
        }
        sb.append( "\n" );

        // print properties

        for ( final String prop : props ) {

            // if we have a pattern, check this property matches
            if ( pattern != null && !prop.contains(pattern) ) {
                continue;
            }

            sb.append( " " +prop + getPadding(prop.length(),longest)+ "  " + p.get(prop) );
            sb.append( "\n" );

        }

        return sb.toString();

    }

    /**
     *  returns a string of space characters enough to pad a string of the
     *  given length to be the same as a string of the longest length
     *
     *  eg.
     *  longest = "asdhasgdhjaghdj"
     *  shorter = "asd            " (with padding)
     *
     *  @param length
     *  @param longest
     *
     *  @return
     *
     */

    protected String getPadding( final int length, final int longest ) {

        String padding = "";

        for ( int i=length; i<longest; i++ )
            padding += " ";

        return padding;

    }

    /**
     *  returns the length of the longest string in the array.  if an empty array
     *  is passed in then will return 0.
     *
     *  @param strings
     *  @return
     *
     */

    protected int getLongestStringLength( final String[] strings ) {

        int longest = 0;

        for ( String string : strings ) {
            final int length = string.length();
            if ( length > longest ) {
                longest = length;
            }
        }

        return longest;

    }

    /**
     *  command to delete a property
     *
     *  @param args
     *
     */

    protected String cmdPropDel( final String[] args ) {

        if ( args.length < 2 ) {
            return getCommands();
        }

        else {

            final String propName = args[ 1 ];

            if ( p.exists(propName) ) {

                p.delete( propName );
                p.save();

                return locale.getString("con.msg.propertyDeleted");

            }

            else {
                return locale.getString("con.err.propertyDoesntExist");
            }

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

    /**
     *  Returns a description of the possible commands
     *
     *  @return
     *
     */

    protected String getCommands() {

        final StringBuffer sb = new StringBuffer();
        
        sb.append( locale.getString("con.desc.commands") + "\n" );
        sb.append( CMD_COLADD + " PATH                            " + locale.getString("con.desc.addDirectory") + "\n" );
        sb.append( CMD_COLDEL + " PATH                            " + locale.getString("con.desc.removeDirectory") + "\n" );
        sb.append( CMD_COLLIST + "                                " + locale.getString("con.desc.listDirectories") + "\n" );
        sb.append( CMD_COLSCAN + "                                " + locale.getString("con.desc.rescanCollection") + "\n" );
        sb.append( CMD_EXIT + "                                   " + locale.getString("con.desc.exit") + "\n" );
        sb.append( CMD_PROPLIST + " FILTER                        " + locale.getString("con.desc.listProperties") + "\n" );
        sb.append( CMD_PROPSET + " NAME VALUE                     " + locale.getString("con.desc.setProperty") + "\n" );
        sb.append( CMD_PROPDEL + " NAME                           " + locale.getString("con.desc.delProperty") + "\n" );
        sb.append( CMD_USERLIST + "                               " + locale.getString("con.desc.listUsers") + "\n" );
        sb.append( CMD_USERADD + " NAME PASS EMAIL ISADMIN (1/0)  " + locale.getString("con.desc.addUser") + "\n" );
        sb.append( CMD_USERDEL + " ID                             " + locale.getString("con.desc.deleteUser") + "\n" );
        sb.append( CMD_USERADMIN + " ID ISADMIN (1/0)             " + locale.getString("con.desc.adminUser") + "\n" );
        sb.append( CMD_VERSION + "                                " + locale.getString("con.desc.version") + "\n" );

        return sb.toString();

    }

    /**
     *  prints version information
     *
     */

    protected String cmdVersion() {
        return "Sockso " +Sockso.VERSION;
    }

    /**
     *  handles the CMD_EXIT command
     *
     */

    protected String cmdExit() {
        Main.exit();
        return "Exiting...";
    }

}
