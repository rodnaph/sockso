
package com.pugh.sockso.music.stream;

import com.pugh.sockso.web.Response;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public interface MusicStream {

    /**
     * Stream from a music file
     *
     * @return DataInputStream
     * 
     * @throws
     *
     */
    public DataInputStream getAudioStream() throws IOException;

    /**
     * Send audio stream to client
     * 
     * @param client
     * 
     * @throws IOException
     */
    public void sendAudioStream( final DataOutputStream client ) throws IOException;


    /**
     * Set headers for streaming
     *
     * @param response
     */
    public void setHeaders( final Response response );
    
}