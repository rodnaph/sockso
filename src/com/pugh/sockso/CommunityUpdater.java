
package com.pugh.sockso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class CommunityUpdater extends Thread implements PropertiesListener {

    private static final Logger log = Logger.getLogger( CommunityUpdater.class );

    private final int MINUTES_BETWEEN_PINGS = 15;

    private final Properties p;

    /**
     *  Create a new community updater
     *
     *  @param p
     *
     */

    @Inject
    public CommunityUpdater( final Properties p ) {
        
        this.p = p;

        p.addPropertiesListener( this );
        
    }

    /**
     *  When properties are saved interrupt our wait to check if we need to
     *  ping the community server.
     *
     *  @param p
     *
     */

    public void propertiesSaved( final Properties p ) {

        this.interrupt();

    }

    /**
     *  Entry point for this thread
     *
     */

    @Override
    public void run() {

        while ( true ) {

            check();

            try { Thread.sleep( 1000 * 60 * MINUTES_BETWEEN_PINGS ); }
            catch ( final InterruptedException e ) {}

        }

    }

    /**
     *  Checks if the community is enabled for this server, and sends a ping
     *  if it is.
     *
     */

    protected void check() {

        if ( p.get(Constants.COMMUNITY_ENABLED).equals(p.YES) ) {
            ping();
        }

    }

    /**
     *  Sends a ping request to the community server to inform it that we're
     *  alive and to update our details.
     *
     */

    protected void ping() {

        BufferedReader in = null;
        OutputStreamWriter wr = null;

        try {

            String line;

            final String pingUrl = getPingUrl();
            final HttpURLConnection cnn = getUrlConnection( pingUrl );
            final String json = "{" +
                                    " \"skey\":      \"" +getKey()+ "\", " +
                                    " \"port\":      \"" +p.get(Constants.SERVER_PORT)+ "\", " +
                                    " \"basepath\":  \"" +p.get(Constants.SERVER_BASE_PATH)+ "\" " +
                                "}";

            cnn.setDoOutput( true );

            log.debug( "Ping community server: " +json );

            wr = new OutputStreamWriter( cnn.getOutputStream() );
            wr.write( json );
            wr.flush();

            in = new BufferedReader( new InputStreamReader(cnn.getInputStream()) );

            while ( (line = in.readLine()) != null ) {
                log.debug( "Ping response: " +line );
            }

        }

        catch ( final IOException e ) {
            log.error( e );
        }

        finally {
            Utils.close( wr );
            Utils.close( in );
        }

    }

    /**
     *  Returns the connection object to use (seam for testing)
     *
     *  @param url
     *
     *  @return
     *
     *  @throws IOException
     *
     */
    
    protected HttpURLConnection getUrlConnection( final String url ) throws IOException {

        return (HttpURLConnection) new URL( url ).openConnection();

    }

    /**
     *  Returns the ping url, which can be specified by settings
     * 
     *  @return
     *
     */

    protected String getPingUrl() {

        return p.get(
            Constants.COMMUNITY_PING_URL,
            Constants.WEBSITE_URL + "/community/ping"
        );
        
    }

    /**
     *  Fetches the servers unique key (generating it if it doesn't exist yet)
     *
     *  @return
     *
     */

    private String getKey() {

        String key = p.get( Constants.SERVER_KEY, "" );

        if ( key.length() == 0 ) {
            key = Utils.getRandomString( 32 );
            p.set( Constants.SERVER_KEY, key );
            p.save();
        }

        return key;

    }

}
