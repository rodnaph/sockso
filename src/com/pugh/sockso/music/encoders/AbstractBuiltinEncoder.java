package com.pugh.sockso.music.encoders;

import java.io.IOException;
import java.io.DataInputStream;
import java.util.Arrays;

import org.apache.log4j.Logger;

/*
 *  All built in encoders should extend this class
 *
 */
public abstract class AbstractBuiltinEncoder extends AbstractEncoder implements BuiltinEncoder {
    
    protected final Logger log = Logger.getLogger( AbstractBuiltinEncoder.class );
    
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

        log.debug( Arrays.toString(args) );

        return new DataInputStream(
            new ProcessBuilder( args ).start().getInputStream()
        );

    }

}
