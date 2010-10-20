
package com.pugh.sockso.music.encoders;

import com.pugh.sockso.music.Track;

import java.io.DataInputStream;
import java.io.IOException;

/**
 *  Adds a nice utility method for encoders that use builtin scripts
 *  to do re-encoding (saving duplicated functionality)
 * 
 */

public abstract class BuiltinScriptEncoder extends AbstractBuiltinEncoder {

    /**
     *  returns an audio stream from a builtin script.  the correct for the
     *  platform is determined.  obviously, the scripts for each platform need
     *  to be named the same apart from the extension
     * 
     *  @param track
     *  @param script
     * 
     *  @return
     * 
     *  @throws java.io.IOException
     * 
     */
    
    public DataInputStream getAudioStreamFromScript( final Track track, final String script ) throws IOException {

        String osName = System.getProperty( "os.name" ).toLowerCase();
        String args[] = osName.matches( ".*windows.*" )
                // Windows
                ? new String[] {
                    "scripts/windows/" +script+ ".bat",
                    track.getPath(),
                    "-"
                }
                // Assume anything else is unix-like
                : new String[] {
                    "/bin/sh",
                    "scripts/unix/" +script+ ".sh",
                    track.getPath(),
                    "-"
                };

        return getAudioStreamFromCmd( args );

    }
    
}
