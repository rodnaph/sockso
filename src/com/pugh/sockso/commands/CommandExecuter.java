
package com.pugh.sockso.commands;

import java.util.Vector;

import com.pugh.sockso.Properties;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.CollectionManager;
import com.pugh.sockso.resources.Locale;
import org.apache.log4j.Logger;

/**
 *  Executes console commands
 *
 */

public class CommandExecuter {

    private final static Logger log = Logger.getLogger( CommandExecuter.class );

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
            new UserActive( db, locale ),

            new ColAdd( cm, locale ),
            new ColDel( cm, locale ),
            new ColList( db ),
            new ColScan( cm, db ),

            new PropSet( p, locale ),
            new PropDel( p, locale ),
            new PropList( p ),

            new Version(),
            new Exit()

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
            if ( cmd.getName().equals(name) ) {
                return runCommand( cmd, args );
            }
        }

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

        final int numArgs = args.length - 1;
        
        if ( cmd.getNumArgs() != -1 && cmd.getNumArgs() != numArgs ) {
            return "This command requires " +cmd.getNumArgs()+ " arguments";
        }
        
        log.debug( "Executing Command: '" +cmd.getName()+ "'" );
        
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
    	
    	Vector<String> args = new Vector<String>();

    	String arg = new String();
        boolean previousEscape = false;

    	for ( char c: command.toCharArray() ) {

            if ( Character.isWhitespace(c)) {
                if ( previousEscape ) {
                    arg += c;
                }
                else {
                    args.add(new String(arg));
                    arg = "";
                }
                continue;
            }

            if ( c == '\\' && !previousEscape) {
                previousEscape = true;
                continue;
            }

            arg += c;

            previousEscape = false;

    	}

    	if (arg.length() > 0) {
            args.add(arg);
        }

        return args.toArray(new String[0]);

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

        return sb.toString();

    }

}
