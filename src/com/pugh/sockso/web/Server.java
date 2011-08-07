/**
 * Server.java
 *
 * Created on May 8, 2007, 12:30 PM
 *
 * Some kind of server.
 * 
 */

package com.pugh.sockso.web;

import joptsimple.OptionSet;

public interface Server {
    
    /**
     *  starts the web server, optionally binding to a specified IP address
     *  (if this is null we need to work it out)
     * 
     *  @param ip
     * 
     */
    
    public void start( final OptionSet options, final int port );
    
    /**
     * shuts down the server, asks any threads that are currently still running
     * to finish
     * 
     */

    public void shutdown();

    /**
     * called by threads when they complete
     * 
     * @param thread the thread that has completed
     * 
     */

    public void requestComplete( final ServerThread thread );

    /**
     * returns the ip address the server is bound to and the port that we're
     * listening on
     * 
     * @return ip/port combo
     * 
     */

    public String getHost();

    /**
     * returns the port the server is currently listening on
     * 
     * @return the port number
     * 
     */

    public int getPort();
    
    /**
     *  returns the protocol the server is using
     * 
     *  @return
     * 
     */
    
    public String getProtocol();

}
