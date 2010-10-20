/*
 * The interface that built in sockso encoders need to implement
 * 
 */

package com.pugh.sockso.music.encoders;

import com.pugh.sockso.music.Track;

import java.io.IOException;
import java.io.DataInputStream;

public interface BuiltinEncoder {

    /**
     *  returns an array of file extensions for formats this encoder can handle
     * 
     *  @return array of file lowercase extensions
     * 
     */
    
    public String[] getSupportedFormats();
    
    /**
     *  given a track on disk, returns a data stream that can be used to
     *  stream the track via this encoder to the client
     * 
     *  @param p the properties object
     *  @param track the track to get the stream for
     *  @return a data stream that can be sent to the client
     * 
     *  @throws IOException
     * 
     */
    
    public DataInputStream getAudioStream( Track track, int bitrate ) throws IOException;
    
    /**
     *  returns the default bitrate to use with this encoder if the user
     *  doesn't specify one
     * 
     *  @return integer for bitrate
     * 
     */
    
    public int getDefaultBitrate();
    
    /**
     *  returns the mime type of the data outputted by this encoder
     * 
     *  @return mime type
     * 
     */
    
    public String getOutputMimeType();
    
}
