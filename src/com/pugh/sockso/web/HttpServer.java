
package com.pugh.sockso.web;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.PropertiesListener;

import java.io.IOException;

import java.net.Socket;
import java.net.ServerSocket;

import java.util.Vector;

import javax.swing.JOptionPane; // @TODO remove

import joptsimple.OptionSet;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * 
 * A basic HTTP server.
 * 
 */

@Singleton
public class HttpServer extends Thread implements Server, PropertiesListener {

    public static final int DEFAULT_PORT = 4444;

    private final Injector injector;
    private final Properties p;
    private final Vector<ServerThread> threads;
    
    private int port = DEFAULT_PORT;
    private ServerSocket ss;

    private static Logger log = Logger.getLogger(Server.class);

    /**
     *  Creates a new instance of a http server.  If the ip address given is null,
     *  then the server will try and work it out for itself.
     *
     *  @param injector
     *  @param p app properties
     *
     */

    @Inject
    public HttpServer( final Injector injector, final Properties p ) {

        this.injector = injector;
        this.p = p;

        threads = new Vector<ServerThread>();

    }
    
    /**
     *  starts the web server, optionally binding to a specific ip (if this
     *  is null then we'll try and work it out ourselves)
     *
     *  @param options
     *
     */

    public void start( final OptionSet options, final int port ) {

        this.port = port;
        
        p.addPropertiesListener( this );

        start();

    }

    /**
     *  the server thread. opens a socket for listening and then waits for
     *  requests.
     *
     */

    @Override
    public void run() {

        try {
            ss = getServerSocket( port );
            log.info( "Listening on " + port );
            while ( true ) {
                handleRequest( ss.accept());
            }
        }

        catch ( final IOException e ) {
            // the socket being closed "shouldnt" be an error... i think?
            if ( !e.getMessage().equals("Socket closed") )
                log.error(e);
        }

    }

    /**
     *  a connection from a client has been received, so we need to
     *  create a thread to handle it.  this thread *should* call back
     *  to us when it's done.
     *
     *  @param client
     *
     */

    protected void handleRequest( final Socket client ) {

        final ServerThread st = injector.getInstance( ServerThread.class );
        
        threads.addElement( st );
        
        st.setClientSocket( client );
        st.start();

    }

    /**
     * shuts down the server, asks any threads that are currently still running
     * to finish
     *
     */

    public void shutdown() {

        log.info( "Shutting Down " + threads.size() + " Thread(s)" );

        for ( final  ServerThread thread : threads )
                thread.shutdown();

        if ( ss != null )
            try {
                log.info("Closing Listening Socket");
                ss.close();
            }
            catch ( final Exception e ) {
                log.error(e);
            }

        log.info("Shutdown Complete");

    }

    /**
     * called by threads when they complete
     *
     * @param thread the thread that has completed
     */

    public void requestComplete( final ServerThread thread ) {

        //log.debug( "Thread Complete Signal Received" );

        threads.removeElement(thread);

    }

    /**
     * returns the ip address the server is bound to and the port that we're
     * listening on
     *
     * @return ip:port combo
     *
     * @deprecated
     * 
     */

    public String getHost() {

        return p.get( Constants.SERVER_HOST ) + ":" + getPort();

    }

    /**
     * returns the port the server is currently listening on
     *
     * @return the port number
     *
     */

    public int getPort() {

        return port;

    }

    /**
     * handles properties being saved, maybe things have changed and we need to
     * do some adjustment
     *
     * @param p the new properties object
     *
     */

    public void propertiesSaved( final Properties p ) {

        try {

            final int newPort = Integer.parseInt(p.get(Constants.SERVER_PORT, "4444"));
            if (newPort != port)
                JOptionPane.showMessageDialog( null, "You need to restart Sockso for this change to take effect" );

        }

        catch ( final NumberFormatException e ) {
            log.warn("The server port is not a number: '" + p.get(Constants.SERVER_PORT) + "'");
        }

    }

    /**
     *  returns a standard server socket
     * 
     *  @param port
     * 
     *  @return
     * 
     *  @throws java.io.IOException
     * 
     */
    
    protected ServerSocket getServerSocket( final int port ) throws IOException {

         return new ServerSocket( port );

    }

    /**
     *  Returns the name of the protocol we're using (eg "http", "https")
     *
     *  @return
     *
     */

    public String getProtocol() {
        
        return "http";
        
    }
    
}

