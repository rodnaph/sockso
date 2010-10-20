
package com.pugh.sockso.music.playlist;

/**
 *  defines the interface for classes that allow access to playlist files like
 *  m3u, pls, etc...  and also provides the method getPlaylistFile() to fetch
 *  a class of the correct type for each playlist we can handle.
 * 
 */

import com.pugh.sockso.Utils;

import java.io.File;
import java.io.IOException;

public abstract class PlaylistFile {

    /**
     *  returns the paths of the tracks found in the playlist file.  if these
     *  are local paths then they NEED to be absolute.
     * 
     *  @return
     * 
     */
    
    public abstract String[] getPaths();
    
    /**
     *  loads the playlist file from disk (or wherever)
     * 
     */
    
    public abstract void load() throws IOException;

    /**
     *  given a file, will try and determine the correct playlist type and
     *  return the right class to handle reading it.  if the playlist isn't
     *  something we know about then returns null.
     * 
     *  @param file
     * 
     *  @return
     * 
     *  @throws java.io.IOException
     * 
     */
    
    public static PlaylistFile getPlaylistFile( final File file ) throws IOException {
        
        final String ext = Utils.getExt( file );

        if ( ext.equals("m3u") ) {
            final M3uFile playlist = new M3uFile( file );
            playlist.load();
            return playlist;
        }
        
        return null;
        
    }
    
}
