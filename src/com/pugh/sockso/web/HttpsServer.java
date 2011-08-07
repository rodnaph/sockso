
package com.pugh.sockso.web;

import com.pugh.sockso.Options;
import com.pugh.sockso.Properties;

import java.io.IOException;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import joptsimple.OptionSet;

import com.google.inject.Singleton;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 *  extends the main HttpServer class to add HTTPS support
 * 
 */

@Singleton
public class HttpsServer extends HttpServer {

    private String sslKeystore = "ssl/keystore",
                   sslKeystorePassword = "sockso123";
    
    /**
     *  Creates a new instance of Server.  If the ip address given is null,
     *  then the server will try and work it out for itself.
     *
     *  @param port
     *  @param dispatcher
     *  @param db the database to use
     *  @param p app properties
     *  @param r
     * 
     */
    
    @Inject
    public HttpsServer( final Properties p, final Injector injector ) {

        super( injector, p );

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
    
    protected SSLServerSocket getServerSocket( final int port ) throws IOException {

        System.setProperty( "javax.net.ssl.keyStore", sslKeystore );
        System.setProperty( "javax.net.ssl.keyStorePassword", sslKeystorePassword );

        SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        SSLServerSocket ss = (SSLServerSocket) sslserversocketfactory.createServerSocket( port );
        
        return ss;

    }

    @Override
    public void start( final OptionSet options, final int port ) {
        
        if ( options.has(Options.OPT_SSL_KEYSTORE) )
            sslKeystore = options.valueOf( Options.OPT_SSL_KEYSTORE ).toString();

        if ( options.has(Options.OPT_SSL_PASSWORD) )
            sslKeystorePassword = options.valueOf( Options.OPT_SSL_PASSWORD ).toString();

        // then call parent method to start server
        super.start( options, port );
        
    }

    public String getProtocol() {
        
        return "https";
        
    }

}
