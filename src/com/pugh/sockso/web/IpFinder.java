
package com.pugh.sockso.web;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Options;
import com.pugh.sockso.Properties;
import com.pugh.sockso.Utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.net.URL;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;
import java.net.SocketTimeoutException;

import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import joptsimple.OptionSet;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

/**
 *  This class is responsible for finding the IP address to use for Sockso
 *
 */

public class IpFinder {

    public static Logger log = Logger.getLogger( IpFinder.class );

    public static final String LOOPBACK = "127.0.0.1";

    private final OptionSet options;
    private final Properties p;

    private String ip;

    /**
     *  Constructor
     * 
     *  @param p
     * 
     */

    @Inject
    public IpFinder( final Properties p ) {

        this( p, null );

    }

    /**
     *  Constructor, with OptionSet of command line args
     * 
     *  @param p
     *  @param options
     * 
     */

    public IpFinder( final Properties p, final OptionSet options ) {
        
        this.options = options;
        this.p = p;

        ip = LOOPBACK;

    }

    /**
     *  Inits the object, loading any stored info, reading options, etc...
     *  This needs to be called before the object is used.
     * 
     */

    public void init() {

        ip = getIpFromOptions();

        if ( ip == null ) {
            new Thread() {

                @Override
                public void run() {

                    while ( true ) {
                        update();
                        try { Thread.sleep(Constants.SERVER_IP_TIMEOUT); }
                        catch ( final InterruptedException e ) {
                            /* nothing */
                        }
                    }

                }

            }.start();
        }

    }

    /**
     *  Refreshes the IP, but invalidates cache first so it's not used.  This
     *  is a "hard" refresh.
     * 
     */
    
    public void refresh() {

        p.set( Constants.SERVER_HOST_LAST_UPDATED, 0 );

        update();

    }

    /**
     *  Refreshes the IP by first looking for it saved in properties, then
     *  trying to fetch ip from internet, then using local lan ip, then finally
     *  using loopback.
     *
     */

    protected void update() {

        ip = null;

        // 1. from properties
        if ( ip == null ) { ip = getIpFromProperties(); }

        // 2. from internet
        if ( ip == null ) { ip = getIpFromInternet(); }

        // 3. from intranet
        if ( ip == null ) { ip = getIpFromIntranet(); }

        // 4. otherwise loopback
        if ( ip == null ) { ip = LOOPBACK; }

        save();

        log.debug( "Using IP " +ip );

    }

    /**
     *  saves the ip to the properties, also saving the time so we can work out
     *  when the ip has gone stale.
     *
     */

    protected void save() {

        p.set( Constants.SERVER_HOST, ip );
        p.set( Constants.SERVER_HOST_LAST_UPDATED, Long.toString(new Date().getTime()) );
        p.save();

    }

    /**
     *  Tries to fetch the IP address from the options, returns null if nothing
     *  was specified.
     * 
     *  @return
     * 
     */

    protected String getIpFromOptions() {

        log.debug( "Get IP from options" );

        return ( options != null && options.has(Options.OPT_IP) )
            ? options.valueOf( Options.OPT_IP ).toString()
            : null;

    }

    /**
     *  Tried to fetch the IP address from the properties.  If it's timeout has
     *  expired then null is returned.
     * 
     *  @return
     * 
     */

    protected String getIpFromProperties() {

        log.debug( "Get IP from properties" );

        final String possibleIp = p.get( Constants.SERVER_HOST );
        final long lastUpdated = p.get( Constants.SERVER_HOST_LAST_UPDATED, 0 );
        final long ipTimeout = new Date().getTime() - Constants.SERVER_IP_TIMEOUT;

        return ( possibleIp.equals("") || lastUpdated < ipTimeout )
            ? null
            : possibleIp;

    }

    /**
     * tries to get the global ip address from whatismyip.org, returns a boolean
     * indicating success or not
     *
     * @return success or not
     *
     */

    private String getIpFromInternet() {

        log.debug( "Get IP from internet" );

        try {

            final String natUrl = Constants.WEBSITE_URL + "/nat/ip/";

            log.info( "Fetching IP from " + natUrl );

            final URL url = new URL( natUrl );
            final HttpURLConnection cnn = (HttpURLConnection) url.openConnection();

            cnn.setRequestMethod( "GET" );

            return getIpFromUrl( cnn );

        }

        catch ( final SocketTimeoutException e ) {
            log.warn( e );
        }

        catch ( final UnknownHostException e ) {
            // this is ok? just no inet?
            log.warn( e );
        }

        catch ( final IOException e ) {
            log.warn( e );
        }

        return null;

    }

    /**
     * tries to fetch the local ip address, returns a boolean indicating if this
     * went well.
     *
     * @return success
     *
     */

    private String getIpFromIntranet() {

        log.debug( "Get IP from intranet" );

        try {
            return Utils.getLocalIp();
        }

        catch ( UnknownHostException e ) {
            log.warn(e);
        }

        return null;

    }

    /**
     *  tries to read an IP address from a url
     *
     *  @param cnn
     *
     *  @return
     *
     *  @throws java.io.IOException
     *
     */

    protected String getIpFromUrl( final HttpURLConnection cnn ) throws IOException {

        BufferedReader in = null;

        try {

            // read input and check format matches an IP address
            in = new BufferedReader( new InputStreamReader(cnn.getInputStream()) );
            final String s = in.readLine();

            if ( !isValidIpFormat(s) ) {
                throw new IOException( "Invalid response: " + s );
            }

            return s;

        }

        finally { Utils.close(in); }

    }

    /**
     *  Checks if a string is in a valid IP address format
     * 
     *  @param possibleIp
     * 
     *  @return
     * 
     */

    protected boolean isValidIpFormat( final String possibleIp ) {

        final Matcher m = Pattern
                            .compile( "\\d+.\\d+.\\d+.\\d+" )
                            .matcher( possibleIp );

        return m.matches();

    }

    /**
     *  Returns the current ip address we have
     * 
     *  @return
     * 
     */

    public String getIp() {

        return ip;

    }

}
