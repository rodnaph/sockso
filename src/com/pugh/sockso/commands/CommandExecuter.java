
package com.pugh.sockso.commands;

import com.pugh.sockso.Main;
import com.pugh.sockso.Properties;
import com.pugh.sockso.Sockso;
import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.CollectionManager;
import com.pugh.sockso.resources.Locale;

import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 *  Executes console commands
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

    private final Command[] commands;

    public CommandExecuter( final Database db, final Properties p, final CollectionManager cm, final Locale locale ) {

        this.db = db;
        this.p = p;
        this.cm = cm;
        this.locale = locale;

        commands = new Command[] {

            new UserList( db ),
            new UserAdd( db, locale ),
            new UserDel( db, locale ),
            new UserAdmin( db, locale ),

            new ColAdd( cm, locale ),
            new ColDel( cm, locale ),
            new ColList( db ),

            new PropSet( p, locale ),
            new PropDel( p, locale ),
            new PropList( p )

        };

    }

    /**
     *  Executes a command and returns the results
     *
     *  @param command
     *
     *  @return
     *
     */

    public String execute( final String command ) throws Exception {

        final String[] args = getArgs( command );
        final String name = args.length > 0 ? args[0] : "";

        for ( final Command cmd : commands ) {
            if ( cmd.getName().equals(command) ) {
                return runCommand( cmd, args );
            }
        }

        if (name.equals(CMD_EXIT)) { return cmdExit(); }
        else if (name.equals(CMD_VERSION)) { return cmdVersion(); }

        return getCommands();

    }

    /**
     *  Runs a command with the specified arguments (if they are all present)
     *
     *  @param cmd
     *  @param args
     *
     *  @return
     *
     *  @throws Exception
     *
     */
    
    protected String runCommand( final Command cmd, final String[] args ) throws Exception {

        final int numArgs = args.length;
        
        if ( cmd.getNumArgs() != -1 && cmd.getNumArgs() != numArgs ) {
            return "This command requires " +cmd.getNumArgs()+ " arguments";
        }
        
        if ( cmd.getMinArgs() != -1 && cmd.getMinArgs() > numArgs ) {
            return "This command requires a minimum of " +cmd.getMinArgs()+ " arguments";
        }

        if ( cmd.getMaxArgs() != -1 && cmd.getMaxArgs() < numArgs ) {
            return "This command requires a maximum of " +cmd.getMaxArgs()+ " arguments";
        }

        return cmd.execute( args );

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

        final String[] lines = new String[ commands.length ];
        final StringBuffer sb = new StringBuffer();

        int longest = 0;
        
        sb.append( locale.getString("con.desc.commands") + "\n" );

        for ( int i=0; i<commands.length; i++ ) {

            final Command cmd = commands[ i ];
            final StringBuffer lb = new StringBuffer();

            lb.append( "  " );
            lb.append( cmd.getName() );

            for ( final String argument : cmd.getArguments() ) {
                lb.append( " " );
                lb.append( argument );
            }

            if ( lb.length() > longest ) {
                longest = lb.length();
            }

            lines[ i ] = lb.toString();

        }

        for ( int i=0; i<lines.length; i++ ) {
            
            final String line = lines[ i ];

            sb.append( line );
            
            for ( int j=longest+5; j>line.length(); j-- ) {
                sb.append( " " );
            }
            
            sb.append( commands[i].getDescription() );
            sb.append( "\n" );

        }

//        sb.append( CMD_COLADD + " PATH                            " + locale.getString("con.desc.addDirectory") + "\n" );
//        sb.append( CMD_COLDEL + " PATH                            " + locale.getString("con.desc.removeDirectory") + "\n" );
//        sb.append( CMD_COLLIST + "                                " + locale.getString("con.desc.listDirectories") + "\n" );
//        sb.append( CMD_COLSCAN + "                                " + locale.getString("con.desc.rescanCollection") + "\n" );
//        sb.append( CMD_PROPLIST + " FILTER                        " + locale.getString("con.desc.listProperties") + "\n" );
//        sb.append( CMD_PROPSET + " NAME VALUE                     " + locale.getString("con.desc.setProperty") + "\n" );
//        sb.append( CMD_PROPDEL + " NAME                           " + locale.getString("con.desc.delProperty") + "\n" );
//        sb.append( CMD_USERLIST + "                               " + locale.getString("con.desc.listUsers") + "\n" );
//        sb.append( CMD_USERADD + " NAME PASS EMAIL ISADMIN (1/0)  " + locale.getString("con.desc.addUser") + "\n" );
//        sb.append( CMD_USERDEL + " ID                             " + locale.getString("con.desc.deleteUser") + "\n" );
//        sb.append( CMD_USERADMIN + " ID ISADMIN (1/0)             " + locale.getString("con.desc.adminUser") + "\n" );

        sb.append( CMD_VERSION + "                                " + locale.getString("con.desc.version") + "\n" );
        sb.append( CMD_EXIT + "                                   " + locale.getString("con.desc.exit") + "\n" );

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
