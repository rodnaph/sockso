/*
 * ResourceSource.java
 * 
 * Created on Jul 19, 2007, 10:53:20 AM
 * 
 * Abstract class for the different resource fetchers to extend.
 * 
 */

package com.pugh.sockso.resources;

import java.io.InputStream;
import java.awt.Image;

import org.apache.log4j.Logger;

public interface Resources {

    public static final Logger log = Logger.getLogger( Resources.class );
   
    public static final String DEFAULT_LOCALE = "en";

    /**
     *  inits this resource type with data for the specified locale.  this needs
     *  to be called before the class can be used.
     * 
     *  @param locale
     * 
     */
    
    public abstract void init( final String locale );
    
    /**
     *  returns a resource as a stream
     * 
     *  @param name the resource name
     *  @return the resources input stream
     * 
     */

    public abstract InputStream getResourceAsStream( final String name );
    
    /**
     *  returns a resource as an image
     * 
     *  @param name the resource name
     *  @return the image
     * 
     */
    
    public abstract Image getImage( final String name );
 
}
