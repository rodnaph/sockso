
package com.pugh.sockso;

import com.pugh.sockso.resources.Resources;

import joptsimple.OptionParser;

/**
 *  command line options parsing
 * 
 */

public class Options {

    public static final String OPT_NOGUI = "nogui";
    public static final String OPT_HELP = "help";
    public static final String OPT_LOGTYPE = "logtype";
    public static final String OPT_UPNP = "upnp";
    public static final String OPT_RESOURCESTYPE = "resourcestype";
    public static final String OPT_LOCALE = "locale";
    public static final String OPT_QUERY = "query";
    public static final String OPT_VERSION = "version";
    public static final String OPT_DATADIR = "datadir";
    public static final String OPT_IP = "ip";

    public static final String OPT_SSL = "ssl";
    public static final String OPT_SSL_KEYSTORE = "sslKeystore";
    public static final String OPT_SSL_PASSWORD = "sslKeystorePassword";

    public static final String OPT_DBTYPE = "dbtype";
    public static final String OPT_DBHOST = "dbhost";
    public static final String OPT_DBUSER = "dbuser";
    public static final String OPT_DBPASS = "dbpass";
    public static final String OPT_DBNAME = "dbname";
    
    public static final String OPT_ADMIN = "admin";
    
    /**
     *  returns an options parser to use
     * 
     *  @return
     * 
     */
    
    public static OptionParser getParser() {
        
        final OptionParser parser = new OptionParser();

        parser.accepts( OPT_NOGUI, "Run without a GUI" );
        
        parser.accepts( OPT_HELP, "Show help options" );
        
        parser.accepts( OPT_LOGTYPE, "Specify logging type" )
            .withRequiredArg().describedAs( "default,dev" );
        
        parser.accepts( OPT_UPNP, "Try UPNP port forwarding" );
        
        parser.accepts( OPT_RESOURCESTYPE, "(dev only) How to fetch resources" )
            .withRequiredArg().describedAs( "jar,file" );
        
        parser.accepts( OPT_LOCALE, "language locale" )
            .withRequiredArg().describedAs( Resources.DEFAULT_LOCALE );
        
        parser.accepts( OPT_IP, "Fixed IP" )
            .withRequiredArg().describedAs( "ipaddress" );
        
        parser.accepts( OPT_QUERY, "SQL query file" )
            .withOptionalArg().describedAs( "file.txt" );
        
        parser.accepts( OPT_VERSION, "Print version" );
        
        parser.accepts( OPT_DATADIR, "Data directory"  )
            .withRequiredArg().describedAs( "full path" );

        //
        //  ssl
        //
        
        parser.accepts( OPT_SSL, "Use SSL" );
        
        parser.accepts( OPT_SSL_KEYSTORE, "Path of SSL keystore file" )
            .withRequiredArg().describedAs( "keystore file" );

        parser.accepts( OPT_SSL_PASSWORD, "SSL keystore password" )
            .withRequiredArg().describedAs( "password" );

        //
        //  database
        //
        
        parser.accepts( OPT_DBTYPE, "Database type" )
            .withRequiredArg().describedAs( "hsql,mysql" );
        
        parser.accepts( OPT_DBHOST, "Database host" )
            .withRequiredArg().describedAs( "DNS or IP" );
        
        parser.accepts( OPT_DBUSER, "Database user" )
            .withRequiredArg().describedAs( "username" );
        
        parser.accepts( OPT_DBPASS, "Database password" )
            .withRequiredArg().describedAs( "password" );

        parser.accepts( OPT_DBNAME, "Database name" )
            .withRequiredArg().describedAs( "name" );

        //
        //  admin
        //
        
        parser.accepts( OPT_ADMIN, "Run in admin mode" );
        
        return parser;

    }
    

}
