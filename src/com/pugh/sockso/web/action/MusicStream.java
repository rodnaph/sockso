
package com.pugh.sockso.web.action;

import java.io.DataInputStream;

/**
 *  a class to encapsulate an audio stream and information about it, for
 *  instance the mime type of the data.
 * 
 */

public class MusicStream {
    
    private final DataInputStream stream;
    private final String mimeType;
    
    /**
     *  constructor
     * 
     *  @param stream
     *  @param mimeType
     * 
     */
    
    public MusicStream( final DataInputStream stream, final String mimeType ) {
        this.stream = stream;
        this.mimeType = mimeType;
    }

    /**
     *  returns the audio stream
     * 
     *  @return
     * 
     */
    
    public DataInputStream getAudioStream() {
        return stream;
    }
    
    /**
     *  returns the mime type of the data from audio stream
     * 
     *  @return
     * 
     */
    
    public String getMimeType() {
        return mimeType;
    }
    
}