
package com.pugh.sockso.music.encoders;

import com.pugh.sockso.music.Track;
import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author Nathan Perrier
 */
public interface Encoder {

    /**
     *  given a track on disk, returns a data stream that can be used to
     *  stream the track via this encoder to the client
     *
     *  @param track the track to get the stream for
     *  @return a data stream that can be sent to the client
     *
     *  @throws IOException
     *
     */

    public DataInputStream getAudioStream( Track track ) throws IOException;

    /**
     *  given a track on disk, returns a data stream that can be used to
     *  stream the track via this encoder to the client
     *
     *  @param track the track to get the stream for
     *  @param bitrate the bitrate of the stream
     *
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
    
}
