/*
 * Console.java
 * 
 * Created on Jul 25, 2007, 11:18:07 AM
 * 
 * This class creates a shell console for the user to
 * interact with sockso.
 *
 */

package com.pugh.sockso;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.events.LatestVersionEvent;
import com.pugh.sockso.web.User;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.music.CollectionManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.File;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Arrays;

import org.apache.log4j.Logger;

public class Console implements Manager {

    private static final Logger log = Logger.getLogger( Console.class );
    
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
    protected static final String CMD_VERSION = "version";

    private final Database db;
    private final Properties p;
    private final CollectionManager cm;
    private final Locale locale;
    private final PrintStream out;
    private final InputStream is;

    private static final String PROMPT = "#SoCkSo#> ";
    
    public Console( final Database db, final Properties p, final CollectionManager cm, final Locale locale ) {
        this( db, p, cm, System.out, System.in, locale );
    }
    
    public Console( final Database db, final Properties p, final CollectionManager cm, final PrintStream out, final InputStream is, final Locale locale ) {

        this.db = db;
        this.p = p;
        this.cm = cm;
        this.out = out;
        this.is = is;
        this.locale = locale;
                
    }
    
    /**
     *  checks for a newer version
     * 
     */
    
    public void latestVersionReceived( final LatestVersionEvent evt ) {

        final String latestVersion = evt.getVersion();

        if ( latestVersion != null && !latestVersion.equals(Sockso.VERSION) ) {
            out.println( locale.getString(
                "misc.msg.updateAvailable",
                new String[] { latestVersion }
            ));
        }

    }
    
    /**
     *  opens the console, presenting the user with a shell
     * 
     */
    
    public void open() {

        BufferedReader in = null;

        try {
        
            in = new BufferedReader( new InputStreamReader(is) );
            
            do {

                out.print( PROMPT );
                final String command = in.readLine();
 
                // if we've been started in the background then we won't
                // be attached to any input stream, so can't use console...
                if ( command == null ) {
                    return;
                }
  
                dispatchCommand( command );
                
            }
            while ( true );

        }
        
        catch ( final IOException e ) {
            log.error( e );
        }
        
        catch ( final SQLException e ) {
            log.error( e );
        }
        
        finally { Utils.close(in); }
        
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
     *  processes a command (eg. colall /home/user/music)
     * 
     *  @param command the command to process
     * 
     */
    
    public void dispatchCommand( final String command ) throws SQLException {
        
        final String[] args = getArgs( command );
        final String name = args.length > 0 ? args[0] : "";
        
        if ( name.equals("") ) { /* ignore empty lines */ }
        else if ( name.equals(CMD_EXIT) ) { cmdExit(); }
        else if ( name.equals(CMD_COLADD) ) { cmdColAdd( args ); }
        else if ( name.equals(CMD_COLDEL) ) { cmdColDel( args ); }
        else if ( name.equals(CMD_COLLIST) ) { cmdColList(); }
        else if ( name.equals(CMD_COLSCAN) ) { cmdColScan(); }
        else if ( name.equals(CMD_PROPLIST) ) { cmdPropList( args ); }
        else if ( name.equals(CMD_PROPSET) ) { cmdPropSet( args ); }
        else if ( name.equals(CMD_PROPDEL) ) { cmdPropDel( args ); }
        else if ( name.equals(CMD_USERLIST) ) { cmdUserList( args ); }
        else if ( name.equals(CMD_USERADD) ) { cmdUserAdd( args ); }
        else if ( name.equals(CMD_USERDEL) ) { cmdUserDel( args ); }
        else if ( name.equals(CMD_VERSION) ) { cmdVersion(); }

        else printCommands();
        
    }
    
    /**
     *  prints version information
     * 
     */
    
    protected void cmdVersion() {
        out.println( "Sockso " +Sockso.VERSION );
    }
    
    /**
     *  handles the CMD_EXIT command
     * 
     */
    
    protected void cmdExit() {
        Main.exit();
    }
    
    /**
     *  prints out the available commands for the user
     * 
     */
    
    protected void printCommands() {
        
        out.println( locale.getString("con.desc.commands") );
        out.println( CMD_COLADD + " PATH                " + locale.getString("con.desc.addDirectory") );
        out.println( CMD_COLDEL + " PATH                " + locale.getString("con.desc.removeDirectory") );
        out.println( CMD_COLLIST + "                    " + locale.getString("con.desc.listDirectories") );
        out.println( CMD_COLSCAN + "                    " + locale.getString("con.desc.rescanCollection") );
        out.println( CMD_EXIT + "                       " + locale.getString("con.desc.exit") );
        out.println( CMD_PROPLIST + " FILTER            " + locale.getString("con.desc.listProperties") );
        out.println( CMD_PROPSET + " NAME VALUE         " + locale.getString("con.desc.setProperty") );
        out.println( CMD_PROPDEL + " NAME               " + locale.getString("con.desc.delProperty") );
        out.println( CMD_USERLIST + "                   " + locale.getString("con.desc.listUsers") );
        out.println( CMD_USERADD + " NAME PASS EMAIL    " + locale.getString("con.desc.addUser") );
        out.println( CMD_USERDEL + " ID                 " + locale.getString("con.desc.deleteUser") );
        out.println( CMD_VERSION + "                    " + locale.getString("con.desc.version") );
        out.println( "" );

    }

    /**
     *  handles the CMD_USERLIST to list users
     * 
     *  @param args command arguments
     * 
     */
    
    protected void cmdUserList( final String[] args ) throws SQLException {
        
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
        
            final String sql = " select id, name, email " +
                               " from users " +
                               " order by name asc ";

            st = db.prepare( sql );
            rs = st.executeQuery();

            while ( rs.next() )
                out.println( rs.getString("id") + "\t" +
                             rs.getString("name") + "\t" +
                             rs.getString("email") );

        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
            
    }
    
    /**
     *  handles the CMD_USERADD to add a user
     * 
     *  @param args command arguments
     * 
     */
    
    protected void cmdUserAdd( final String[] args ) throws SQLException {
        
        if ( args.length < 4 )
            printCommands();
        else {
            
            try {
                
                final Validater v = new Validater( db );
                final String name = args[ 1 ];
                final String pass = args[ 2 ];
                final String email = args[ 3 ];
                
                if ( v.usernameExists(name) ) {
                    throw new ValidationException( locale.getString("con.err.usernameExists") );
                }
                
                if ( v.emailExists(email) ) {
                    throw new ValidationException( locale.getString("con.err.emailExists") );
                }

                final User newUser = new User( name, pass, email );

                newUser.save( db );

                out.println( newUser.getId() != -1
                    ? locale.getString("con.msg.userCreated")
                    : locale.getString("con.err.errorCreatingUser") );

            }
            catch ( final ValidationException e ) {
                out.println( e.getMessage() );
            }
        }
        
    }
    
    /**
     *  handles the CMD_USERDEL to delete a user
     * 
     *  @param args command arguments
     * 
     */
    
    protected void cmdUserDel( final String[] args ) {
        
        if ( args.length < 2 )
            printCommands();
        else {
            try {
                final String sql = " delete from users " +
                            " where id = " + db.escape(args[1]);
                db.update( sql );
                out.println( locale.getString("con.msg.userDeleted") );
            }
            catch ( final SQLException e ) {
                out.println( locale.getString("con.err.errorDeletingUser") );
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
    
    protected void cmdColDel( final String[] args ) {
        
        if ( args.length < 2 )
            printCommands();
        else {
        
            final String path = args[ 1 ];
        
            out.println( cm.removeDirectory(path)
                ? locale.getString("con.msg.directoryDeleted")
                : locale.getString("con.err.directoryNotInColl") );

        }
        
    }
    
    /**
     *  handles the CMD_COLADD command, adds a directory
     *  to the collection
     * 
     *  @param args command arguments
     * 
     */
    
    protected void cmdColAdd( final String[] args ) {

        if ( args.length < 2 )
            printCommands();
        else {
        
            final String path = args[ 1 ];
            final File file = new File( path );
            
            if ( file.exists() ) {
                cm.addDirectory( file );
                out.println( locale.getString("con.msg.directoryAdded") );
            }
            else
                out.println( locale.getString("con.err.pathNotExist",new String[] {path}) );

        }
        
    }
    
    /**
     *  handles the CMD_COLLIST command to list the
     *  directories that are in the collection
     * 
     *  @throws SQLException
     * 
     */
    
    protected void cmdColList() throws SQLException {
        
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
        
            final String sql = " select c.id, c.path " +
                               " from collection c " +
                               " order by c.path asc ";
        
            st = db.prepare( sql );
            rs = st.executeQuery();
        
            out.println( " ID  PATH" );
            out.println( "----------" );
        
            while ( rs.next() )
                out.println( " [" +rs.getString("id")+ "] " +rs.getString("path") );

        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }

    }
    
    /**
     *  handles command to scan the collection now
     * 
     */
    
    protected void cmdColScan() {

        out.println( "Scanning collection..." );
        
        cm.checkCollection();

    }
    
    /**
     *  handles the CMD_PROPSET command to set a
     *  particular application property
     * 
     *  @param args command arguments
     * 
     */
    
    protected void cmdPropSet( final String[] args ) {
        
        if ( args.length < 3 )
            printCommands();
        else {
        
            final String name = args[ 1 ];
            final String value = Utils.joinArray( args, " ", 2, args.length - 1 );

            p.set( name, value );
            p.save();

            out.println( locale.getString("con.msg.propertySaved") );

        }

    }
    
    /**
     *  handles the CMD_PROPLIST command to list the
     *  applications properties
     * 
     */
    
    protected void cmdPropList( String[] args ) {

        final String pattern = ( args.length > 1 ) ? args[1] : null;
        final String[] props = p.getProperties();
        final int longest = getLongestStringLength( props );
        
        Arrays.sort( props );
        
        // print header

        out.println( " NAME" +getPadding(4,longest)+ "  VALUE" );
        if ( pattern != null ) {
            out.println( "\n (containing '" +pattern+ "')" );
        }
        out.println();
        
        // print properties
        
        for ( final String prop : props ) {

            // if we have a pattern, check this property matches
            if ( pattern != null && !prop.contains(pattern) ) {
                continue;
            }
            
            out.println( " " +prop + getPadding(prop.length(),longest)+ "  " + p.get(prop) );

        }

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
    
    protected void cmdPropDel( final String[] args ) {
       
        if ( args.length < 2 )
            printCommands();
        else {
            
            final String propName = args[ 1 ];
            
            if ( p.exists(propName) ) {
            
                p.delete( propName );
                p.save();

                out.println( locale.getString("con.msg.propertyDeleted") );
                
            }
            
            else out.println( locale.getString("con.err.propertyDoesntExist") );

        }
        
    }
    
    /**
     *  closes the console
     * 
     */
    
    public void close() {
        // nothing to do...?
    }
    
}
