package com.pugh.sockso.music.encoders;

import com.pugh.sockso.music.Files;
import com.pugh.sockso.music.Track;

import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author Nathan Perrier
 */
public abstract class AbstractEncoder implements Encoder {

    private static final int DEFAULT_BITRATE = 128;

    public int getDefaultBitrate() {

        return DEFAULT_BITRATE;
    }

    public DataInputStream getAudioStream( final Track track ) throws IOException {

        return getAudioStream( track, getDefaultBitrate() );
    }

    public String getOutputMimeType() {

        return Files.getMimeType( "file.mp3" );
    }

}
