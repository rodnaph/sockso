/*
 * FileResources.java
 * 
 * Created on Jul 19, 2007, 11:00:21 AM
 * 
 * Fetches resources straight from the file system
 * 
 */

package com.pugh.sockso.resources;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.awt.Toolkit;
import java.awt.Image;

import com.google.inject.Singleton;

@Singleton
public class FileResources extends AbstractResources {

    private static final String resourcesDir;
    
    static {

        // this is done statically because it's used in getLocales() which is
        // called from the AbstractResources constructor
        resourcesDir = ( new File("../resources").exists() ? "../" : "" )
                + "resources/";
        
        log.debug( "FileResources: dir=" +resourcesDir );
        
    }
    
    /**
     *  constructor
     * 
     */
    
    public FileResources() {
        
        log.debug( "Created FileResources()" );
        
    }
    
    /**
     *  returns a resources as a stream
     * 
     *  @param name
     * 
     *  @return
     * 
     */
    
    public InputStream getResourceAsStream( final String name ) {
        
        InputStream in = null;

        try {
            final String path = getResourcePath( name );
            in =  new FileInputStream( path );
        }
        
        catch ( final FileNotFoundException e ) {
            log.error( e );
        }
        
        return in;
        
    }
    
    public Image getImage( final String name ) {
        
        final String path = getResourcePath( name );
        return Toolkit.getDefaultToolkit().createImage( path );
        
    }
    
    protected String getResourcePath( final String name ) {
        return resourcesDir + name;
    }

    public String[] getLocales() {
        
        final File localeDir = new File( resourcesDir + "locales" );

        return getLocalesFromFiles( localeDir.list() );
        
    }

}
