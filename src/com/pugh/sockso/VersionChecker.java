
package com.pugh.sockso;

import com.pugh.sockso.events.LatestVersionEvent;
import com.pugh.sockso.events.LatestVersionListener;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  This class handles checking for the latest version from the website, and
 *  allowing listeners to be notified of this information when it's received.
 *
 */

@Singleton
public class VersionChecker {

    private static final Logger log = Logger.getLogger( VersionChecker.class );

    private final Properties p;
    
    private final List<LatestVersionListener> listeners;

    /**
     *  Constructor
     *
     */

    @Inject
    public VersionChecker( final Properties p ) {

        this.p = p;

        this.listeners = new ArrayList<LatestVersionListener>();

    }

    /**
     *  Adds a listener
     *
     *  @param listener
     *
     */

    public void addLatestVersionListener( final LatestVersionListener listener ) {
        
        listeners.add( listener );

    }

    /**
     *  Fires a LatestVersionEvent to all listeners
     *
     *  @param version
     *
     */

    public void fireLatestVersionEvent( final String version ) {

        final LatestVersionEvent evt = new LatestVersionEvent( version );
        
        for ( final LatestVersionListener listener : listeners ) {
            listener.latestVersionReceived( evt );
        }

    }

    /**
     *  Tries to fetch the latest version from the Sockso website
     *
     */

    public void fetchLatestVersion() {

        try {

            final String latestUrl = Constants.VERSION_LATEST_URL;
            final URL url = new URL( latestUrl );
            final HttpURLConnection cnn = (HttpURLConnection) url.openConnection();

            cnn.setRequestMethod( "GET" );

            fetchLatestVersionFrom( cnn );

        }

        catch ( final IOException e ) {
            log.error( e );
        }
        
    }

    /**
     *  Uses the HTTP connection to find the latest version available.  If it
     *  succeeds then it will fire a LatestVersionEvent.
     *
     *  @param cnn
     *
     */

    protected void fetchLatestVersionFrom( final HttpURLConnection cnn ) {

        if ( p.get(Constants.VERSION_CHECK_DISABLED).equals(Properties.YES) ) {
            return;
        }
        
        try {

            BufferedReader in = null;

            try {

                in = new BufferedReader( new InputStreamReader(cnn.getInputStream()) );

                final String s = in.readLine();
                final Matcher m = Pattern.compile("\\d+.\\d+.?\\d*").matcher( s );

                log.debug( "Latest version response: " + s );

                if ( m.matches() ) {
                    fireLatestVersionEvent( s );
                }

            }

            finally { Utils.close(in); }

        }

        catch ( final IOException e ) {
            log.error( e );
        }

    }

}
