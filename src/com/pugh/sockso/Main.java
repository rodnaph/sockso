
package com.pugh.sockso;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.db.DatabaseConnectionException;
import com.pugh.sockso.db.DBExporter;
import com.pugh.sockso.db.HSQLDatabase;
import com.pugh.sockso.db.MySQLDatabase;
import com.pugh.sockso.db.SQLiteDatabase;
import com.pugh.sockso.gui.AppFrame;
import com.pugh.sockso.gui.Splash;
import com.pugh.sockso.music.CollectionManager;
import com.pugh.sockso.music.DBCollectionManager;
import com.pugh.sockso.music.indexing.Indexer;
import com.pugh.sockso.music.indexing.TrackIndexer;
import com.pugh.sockso.music.scheduling.SchedulerRunner;
import com.pugh.sockso.resources.FileResources;
import com.pugh.sockso.resources.JarResources;
import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.web.Dispatcher;
import com.pugh.sockso.web.IpFinder;
import com.pugh.sockso.web.Server;
import com.pugh.sockso.web.SessionCleaner;
import com.pugh.sockso.web.HttpServer;
import com.pugh.sockso.web.HttpsServer;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.InputStream;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.logging.LogManager;

public class Main {
    
    private static final Logger log = Logger.getLogger( Main.class );

    private static volatile boolean shutdownStarted = false;

    private static Dispatcher dispatcher;
    private static Database db;
    private static Properties p;
    private static Server sv;
    private static CollectionManager cm;
    private static Manager manager;
    private static Resources r;
    private static Locale locale;
    private static Indexer indexer;
    private static SchedulerRunner sched;

    /**
     *  application entry point
     *
     *  @param args the command line arguments
     *
     */

    public static void main( final String[] args ) throws Exception{

        Runtime.getRuntime().addShutdownHook( new Shutdown() );

        initLogger( getLogPropsFile("default") );
        initJavaLogger();

        //
        //  do initial setup, we're gonna need to parse the command line
        //  arguments, and make sure we're connected to the database
        //

        final OptionParser parser = Options.getParser();
        OptionSet options = null;
        try { options = parser.parse(args); }
            catch ( final Exception e ) {
                System.err.println( "Invalid command line switch!\n" );
            }

        // check if user asked for basic things as early as we can, don't
        // wanna go doing anything we don't need to.
        if ( options == null || options.has(Options.OPT_HELP) ) {
            parser.printHelpOn( System.out );
            exit( false );
        }
        
        // print version info?
        if ( options.has(Options.OPT_VERSION) ) {
            System.out.println( "Sockso " +Sockso.VERSION );
            exit( false );
        }

        // set a user-defined data directory?
        if ( options.hasArgument(Options.OPT_DATADIR) ) {
            final File dir = new File( (String) options.valueOf(Options.OPT_DATADIR) );
            if ( !dir.exists() ) {
                log.error( dir.getAbsolutePath()+ " does not exist" );
                exit( 1 );
            }
            Utils.setApplicationDirectory( dir );
        }

        setupAppDirectory();
        
        try {
            db = getDatabase( options );
            db.connect( options );
        }        
        catch ( final Exception e ) {
            log.error( e );
            exit( 1 );
        }

        //
        //  final setup from command line options before we try and do
        //  something kinda useful
        //

        if ( options.hasArgument(Options.OPT_LOGTYPE) ) {
            PropertyConfigurator.configure( getLogPropsFile(options.valueOf(Options.OPT_LOGTYPE).toString()) );
        }

        //
        //  now decide exactly what we're gonna be doing, hmm...
        //

        // perform a database query
        if ( options.has(Options.OPT_QUERY) ) {
            actionQuery( options );
        }

        // default is start sockso normally
        else {
            actionDefault( options );
        }

    }

    /**
     *  performs a database query and outputs the results
     * 
     *  @param options
     * 
     */
    
    private static void actionQuery( final OptionSet options ) {

        BufferedReader in = null;
        
        try {

            String sql = "", line = "";

            // if we were given a filename use that as input, otherwise
            // read from stdin
            in = new BufferedReader(
                options.hasArgument(Options.OPT_QUERY)
                    ? new FileReader( options.valueOf(Options.OPT_QUERY).toString() )
                    : new InputStreamReader( System.in )
            );

            // read in query, then output xml
            while ( (line = in.readLine()) != null )
                sql += line + "\n";
            
            final DBExporter exporter = new DBExporter( db );
            System.out.print(
                exporter.export( sql, DBExporter.Format.XML )
            );

        }
        
        catch ( final IOException e ) {
            log.error( e );
        }
        
        finally { Utils.close(in); }
        
    }
    
    /**
     *  starts sockso normally in either GUI or Console mode
     * 
     *  @param options
     * 
     *  @throws java.sql.SQLException
     * 
     */
    
    private static void actionDefault( final OptionSet options ) throws Exception {

        final boolean useGui = getUseGui( options );
        final String localeString = getLocale( options );
        
        log.info( "Initializing Resources (" + locale + ")" );
        r = getResources( options );
        r.init( localeString );

        locale = r.getCurrentLocale();

        if ( useGui ) {
            Splash.start( r );
        }

        log.info( "Loading Properties" );
        p = new DBProperties( db );
        p.init();

        log.info( "Creating Indexer" );
        indexer = getIndexer();

        log.info( "Starting Scheduler" );
        sched = new SchedulerRunner( indexer, p );
        sched.start();

        log.info( "Starting Collection Manager" );
        cm = new DBCollectionManager( db, p, indexer );
        indexer.addIndexListener( (DBCollectionManager) cm );

        new CommunityUpdater( p ).start();

        new SessionCleaner( db ).init();

        final IpFinder ipFinder = new IpFinder( p, options );
        ipFinder.init();

        final int port = getSavedPort( p );
        final String protocol = getProtocol( options );

        dispatcher = new Dispatcher( protocol, port, p, r, cm, db, new ObjectCache() );
        
        log.info( "Starting Web Server" );
        sv = getServer( port, options );
        sv.start( options );

        if ( options.has(Options.OPT_UPNP) ) {
            log.info( "Trying UPNP Magic" );
            UPNP.tryPortForwarding( sv.getPort() );
        }

        manager = getManager( useGui, ipFinder );

        final VersionChecker versionChecker = new VersionChecker( p );
        versionChecker.addLatestVersionListener( manager );
        versionChecker.fetchLatestVersion();
        
        manager.open();

    }

    /**
     *  Works out if we're using the GUI or the console
     *
     *  @param useGui
     *  @param ipFinder
     *
     *  @return
     *
     */

    public static Manager getManager( final boolean useGui, final IpFinder ipFinder ) {

        return ( useGui )
            ? new AppFrame( db, p, sv, cm, r, ipFinder )
            : new Console( db, p, cm, locale );

    }

    /**
     *  returns the correct database to use
     * 
     *  @param options
     * 
     *  @return
     * 
     */
    
    protected static Database getDatabase( final OptionSet options ) throws DatabaseConnectionException {

        final String dbtype = options.has( Options.OPT_DBTYPE )
            ? options.valueOf(Options.OPT_DBTYPE).toString() : "";

        // mysql
        if ( dbtype.equals("mysql") ) {
            log.info( "Using MySQL Database" );
            return new MySQLDatabase();
        }
        
        // sqlite
        if ( dbtype.equals("sqlite") ) {
            log.info( "Using sqlite Database" );
            return new SQLiteDatabase();
        }

        // hsql (default)
        log.info( "Using HSQL Database" );
        return new HSQLDatabase();

    }
    
    /**
     *  returns a boolean indicating if we should start the GUI or not
     * 
     *  @param options
     * 
     *  @return
     * 
     */
    
    protected static boolean getUseGui( final OptionSet options ) {

        return !options.has( Options.OPT_NOGUI );
        
    }
    
    /**
     *  returns the locale to use (eg. "en", "nb", etc...)
     * 
     *  @param options
     * 
     *  @return
     * 
     */
    
    protected static String getLocale( final OptionSet options ) {

        return options.has( Options.OPT_LOCALE )
            ? options.valueOf(Options.OPT_LOCALE).toString()
            : Resources.DEFAULT_LOCALE;

    }
    
    /**
     *  returns the resources object to use
     * 
     *  @param options
     * 
     *  @return
     * 
     */
    
    protected static Resources getResources( final OptionSet options ) {

        final String resourceType = options.has( Options.OPT_RESOURCESTYPE )
            ? options.valueOf(Options.OPT_RESOURCESTYPE).toString() : "";

        log.debug( "Resources type: " +resourceType );
        
        return resourceType.equals( "jar" )
            ? new JarResources()
            : new FileResources();

    }

    /**
     *  Returns the protocol to use for web serving
     *
     *  @param options
     *
     *  @return
     *
     */
    
    protected static String getProtocol( final OptionSet options ) {
        
        return options.has( Options.OPT_SSL )
            ? "https"
            : "http";
        
    }

    /**
     *  returns the server to use
     * 
     *  @param options
     * 
     *  @return
     * 
     */
    
    protected static Server getServer( final int port, final OptionSet options ) {

        return options.has( Options.OPT_SSL )
            ? new HttpsServer( port, dispatcher, db, p, r )
            : new HttpServer( port, dispatcher, db, p, r );

    }

    /**
     *  Returns the Indexer to use
     *
     *  @return
     *
     */

    protected static Indexer getIndexer() {

        return new TrackIndexer( db );
        
    }

    /**
     *  fetches the port stored in the application properties, if this isn't
     *  something acceptable then it'll return the DEFAULT_PORT
     *
     *  @param p
     *
     *  @return
     *
     */

    protected static int getSavedPort( final Properties p ) {

        int thePort = HttpServer.DEFAULT_PORT;

        try {
            thePort = Integer.parseInt(p.get(Constants.SERVER_PORT));
        }
        catch ( final NumberFormatException e ) {
            log.error( "Invalid port number: " + e );
        }

        return thePort;

    }

    /**
     *  returns name of logger properties file for given type
     * 
     *  @param type the logging type
     *  @return the path to the props file
     * 
     */
    
    private static String getLogPropsFile( final String type ) {
        return "log/" + type + ".properties";
    }

    /**
     *  shuts down the application
     * 
     */
    
    public static void exit() {
        exit( 0, true );
    }

    public static void exit( int status ) {
        exit( status, true );
    }
    
    public static void exit( boolean showOutput ) {
        exit( 0, showOutput );
    }
    
    /**
     *  shuts down the application and exits with the specifed exit code
     * 
     *  @param status the exit code
     *
     */

    public static void exit( final int status, final boolean showOutput ) {

        if ( showOutput ) {
            log.info( "Shutting Down" );
        }

        shutdown();

        if ( showOutput ) {
            log.info( "Thank you for your attention, bye!" );
        }

        System.exit( status );

    }

    /**
     *  Shuts down application components
     *
     */

    protected static void shutdown() {

        if ( !shutdownStarted ) {

            shutdownStarted = true;

            if ( indexer != null ) {
                // @TODO
            }

            if ( sv != null ) {
                sv.shutdown();
                sv = null;
            }

            if ( manager != null) {
                manager.close();
                manager = null;
            }

            shutdownDatabase();

        }

    }

    /**
     *  Shuts down the database connection if it's open
     *
     *  @return
     *
     */
    
    public static void shutdownDatabase() {

        // finally shutdown database (do this last so
        // anything can still be written here from the
        // other components)
        if ( db != null ) {
            db.close();
            db = null;
        }

    }
    
    /**
     *  creates the folder in the users home directory that is used
     *  to store application stuff (the database)
     *
     */
    
    private static void setupAppDirectory() {
        
        final String[] dirs = {
            Utils.getApplicationDirectory(),
            Utils.getCoversDirectory()
        };
        
        for ( final String dir : dirs ) {
            final File file = new File( dir );
            if ( !file.exists() ) {
                if ( !file.mkdir() ) {
                    log.fatal( "Unable to create directory: " + dir );
                    exit( 1 );
                }
            }
        }

    }

    /**
     *  initialises the logger for the test cases
     * 
     */
    
    public static void initTestLogger() {
        
        initLogger( "dist-files/log/test.properties" );

    }
    
    /**
     *  inits the logging framework with the specified props file
     * 
     *  @param propsFile the properties file to use
     * 
     */
    

    private static void initLogger( final String propsFile ) {
        
        PropertyConfigurator.configure( propsFile );

    }

    /**
     *  Inits loggers using java.util.logging
     *
     */
     
    private static void initJavaLogger() throws IOException {
    
        final String propsFile = "log/javalogging.properties";
        final InputStream is = new FileInputStream( new File(propsFile) );
        
        LogManager.getLogManager()
                  .readConfiguration( is );

        Utils.close( is );
        
    }

}
