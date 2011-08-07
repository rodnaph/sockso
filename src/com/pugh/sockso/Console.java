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

import com.pugh.sockso.commands.CommandExecuter;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.events.LatestVersionEvent;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.music.CollectionManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.PrintStream;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
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
    protected static final String CMD_USERADMIN = "useradmin";
    protected static final String CMD_VERSION = "version";

    private final Database db;
    private final Properties p;
    private final CollectionManager cm;
    private final Locale locale;
    private final PrintStream out;
    private final InputStream is;

    private static final String PROMPT = "#SoCkSo#> ";
    
    @Inject
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

        final CommandExecuter cmd = new CommandExecuter(db, p, cm, locale);
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
  
                out.println( cmd.execute(command) );
                
            }
            while ( true );

        }
        
        catch ( final Exception e ) {
            log.error( e );
        }
        
        finally { Utils.close(in); }
        
    }

    /**
     *  closes the console
     * 
     */
    
    public void close() {
        // nothing to do...?
    }
    
}
