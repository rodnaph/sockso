
package com.pugh.sockso.music.playlist;

import com.pugh.sockso.Utils;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;

import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 *  this class allows access to the track information from an m3u file, in
 *  either standard or extended format.
 * 
 */

public class M3uFile extends PlaylistFile {

    private final static Logger log = Logger.getLogger( M3uFile.class );

    private final ArrayList<String> paths;
    private final File file;
    
    /**
     *  constructor
     * 
     *  @param file
     * 
     *  @throws IOException
     * 
     */
    
    public M3uFile( final File file ) {
        
        this.file = file;

        paths = new ArrayList<String>();
        
    }
    
    /**
     *  loads the m3u file from disk
     * 
     *  @throws java.io.IOException
     * 
     */
    
    public void load() throws IOException {
        
        final String[] lines = getLines( file );

        loadLines( lines );

    }

    /**
     *  Loads the lines into the playlist
     *
     *  @param lines
     *
     */
    
    public void loadLines( final String[] lines ) {

        if ( lines.length > 0 ) {
            if ( lines[0].equals("#EXTM3U") ) {
                loadExtendedM3u( lines );
            }
            else {
                loadStandardM3u( lines );
            }
        }

    }
    
    /**
     *  returns the lines from a file
     * 
     *  @param file
     * 
     *  @return
     * 
     */
    
    protected String[] getLines( final File file ) throws IOException {
        
        BufferedReader in = null;
        final ArrayList<String> lines = new ArrayList<String>();
        
        try {
            
            in = new BufferedReader( new InputStreamReader(new FileInputStream(file)) );
            String line = null;
            
            while ( (line = in.readLine()) != null ) {
                log.debug( "Read line: " +line );
                lines.add( line );
            }
            
        }
        
        finally {
            Utils.close( in );
        }
        
        return lines.toArray( new String[]{} ); // dodgy generics... :(

    }

    /**
     *  loads track info from lines from an extended m3u file
     * 
     *  @param lines
     * 
     */
    
    private void loadExtendedM3u( final String[] lines ) {
        
        log.debug( "Reading Extended M3u File" );
        
        for ( final String line : lines ) {
            if ( !line.matches("^#EXT.*") && !line.equals("") ) {
                loadPath( line );
            }
        }

    }

    /**
     *  loads track info from lines from a standard m3u file
     * 
     *  @param lines
     * 
     */
    
    private void loadStandardM3u( final String[] lines ) {
        
        log.debug( "Reading Standard M3u File" );
        
        for ( final String line : lines ) {
            if ( !line.equals("") ) {
                loadPath( line );
            }
        }
        
    }
    
    private void loadPath(String line) {
        
        File f = new File(line);
        if (!f.isAbsolute()) {
            log.debug( "Relative path found: " +line );
            f = new File(this.file,line);
            line = f.getAbsolutePath();
        }
        log.debug( "Adding path: " +line );
        paths.add( line );
    }
    
    
    /**
     *  returns the paths from this file (absolute)
     * 
     *  @return
     * 
     */
    
    public String[] getPaths() {

        return paths.toArray( new String[] {} );

    }

}
