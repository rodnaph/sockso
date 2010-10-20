/*
 * Resources.java
 * 
 * Created on Jun 24, 2007, 12:42:27 AM
 * 
 * Allows access to the resources.jar file
 * 
 */

package com.pugh.sockso.resources;

import com.pugh.sockso.Utils;

import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.awt.Image;
import java.awt.Toolkit;

import java.util.Vector;

public class JarResources extends AbstractResources {

    private static ClassLoader classLoader;

    static {

        try { 
           final Class c = Class.forName( "ResourcesAnchor" );
           classLoader = c.getClassLoader();
        }
        
        catch ( final Exception e ) {
           log.error( e.getMessage() );
        }

    }
    
    /**
     *  constructor
     * 
     */
    
    public JarResources() {
        
        log.debug( "Created JarResources()" );
        
    }
    
    /**
     *  returns the InputStream to read a named resource
     * 
     *  @param name the resource to read
     *  @return the input stream to read from
     * 
     */

    public InputStream getResourceAsStream( final String name ) {

        return classLoader.getResourceAsStream( name );
        
    }

    /**
     *  fetches an image from the resources by name
     * 
     *  @param name the image file name
     *  @return the image
     * 
     */

    public Image getImage( final String name ) {
        
        return Toolkit.getDefaultToolkit().createImage(
            classLoader.getResource(name)
        );
        
    }
 
    public String[] getLocales() {

        final Vector<String> locales = new Vector<String>();
        
        BufferedReader in = null;

        try {

            in = new BufferedReader( new InputStreamReader(
                classLoader.getResourceAsStream( "locales/index" )
            ));

            String line = null;

            while ( (line = in.readLine()) != null )
                locales.addElement( line );

        }

        catch ( final IOException e ) {
            e.printStackTrace();
        }

        finally {
            Utils.close( in );
        }
        
        return getLocalesFromFiles(
            (String[]) locales.toArray( new String[locales.size()] )
        );

    }
    
}
