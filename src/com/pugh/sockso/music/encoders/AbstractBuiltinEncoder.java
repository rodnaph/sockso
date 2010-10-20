/*
 *  All built in encoders should extend this class
 * 
 */

package com.pugh.sockso.music.encoders;

import java.io.IOException;
import java.io.DataInputStream;

import org.apache.log4j.Logger;

public abstract class AbstractBuiltinEncoder implements BuiltinEncoder {
    
    protected final Logger log = Logger.getLogger( AbstractBuiltinEncoder.class );
    
    private final int DEFAULT_BITRATE = 128;

    public int getDefaultBitrate() {
        return DEFAULT_BITRATE;
    }
    
    /**
     *  returns an audio stream from the specified command
     * 
     *  @param args
     * 
     *  @return
     * 
     *  @throws java.io.IOException
     * 
     */
    
    public DataInputStream getAudioStreamFromCmd( final String[] args ) throws IOException {

        log.debug( java.util.Arrays.toString(args) );

        return new DataInputStream(
            new ProcessBuilder( args ).start().getInputStream()
        );

    }
    
}
