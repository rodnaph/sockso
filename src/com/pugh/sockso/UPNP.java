/*
 * Services.java
 * 
 * Created on Jun 25, 2007, 11:18:18 AM
 * 
 * Provides services for interacting with UPNP devices
 * 
 * UPNP library: http://www.sbbi.net/site/upnp/
 * 
 */

package com.pugh.sockso;

import java.net.UnknownHostException;
import java.io.IOException;

import net.sbbi.upnp.impls.InternetGatewayDevice;
import net.sbbi.upnp.messages.UPNPResponseException;

import org.apache.log4j.Logger;

public class UPNP {

    private static final int TIMEOUT_SECS = 3; // seconds
    private static final Logger log = Logger.getLogger( UPNP.class );

    /**
     *  returns the UPNP enabled internet gateway devices found on the network
     * 
     *  @return array of devices
     * 
     *  @throws IOException
     * 
     */
    
    public static InternetGatewayDevice[] getRouterDevices() throws IOException {
        
        log.debug( "Searching for devices" );
        
        return InternetGatewayDevice.getDevices( TIMEOUT_SECS * 1000 );

    }
    
    /**
     *  tries to forward a port for a gateway device
     * 
     *  @param device the devie to forward the port on
     *  @param port the port to forward
     *  @return boolean indicating success
     * 
     *  @throws UnknownHostException
     *  @throws IOException
     *  @throws UPNPResponseException
     * 
     */
    
    public static boolean forwardPort(final  InternetGatewayDevice device, final int port ) throws UnknownHostException, IOException, UPNPResponseException {

        final String localHostIP = Utils.getLocalIp();

        final boolean success = device.addPortMapping( "Sockso", 
                                               null, port, port,
                                               localHostIP, 0, "TCP" );

        if ( success )
            log.debug( "Port " + port + " forwarded" );
        
        return success;

    }

    /**
     *  look for UPNP enabled routers on the network, and if we find
     *  some then try and forward the specified port from the first
     *
     *  @param port the port to forward
     *
     */

    public static void tryPortForwarding( final int port ) {

        try {
            final InternetGatewayDevice[] devices = getRouterDevices();
            if ( devices != null && devices.length > 0 ) {
                log.debug( "Found device " + devices[0].getIGDRootDevice().getModelName() );
                forwardPort( devices[0], port );
            }
            else log.debug( "No devices found" );
        }

        catch ( final Exception e ) {
            log.error( e.getMessage() ); 
        }

    }

}
