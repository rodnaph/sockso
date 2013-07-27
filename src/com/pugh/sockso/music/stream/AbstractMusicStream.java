package com.pugh.sockso.music.stream;

import com.pugh.sockso.music.Files;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.web.Response;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * A class to encapsulate an audio stream and information about it, for
 * instance the mime type of the data.
 *
 */
public abstract class AbstractMusicStream implements MusicStream {

    public static final int STREAM_BUFFER_SIZE = 1024 * 8; // 8 KB block size
    
    protected Track track;

    public AbstractMusicStream( final Track track ) {
        this.track = track;
    }

    public DataInputStream getAudioStream() throws IOException {

        final FileInputStream fileStream = new FileInputStream(this.track.getPath());

        return new DataInputStream(fileStream);
    }

    public void setHeaders( final Response response ) {

        final String mimeType = Files.getMimeType(this.track.getPath());
        final String filename = track.getArtist().getName() + " - " + track.getName();
        
        response.addHeader("Content-Disposition", "filename=\"" + filename + "\"");
        response.addHeader("Content-Type", mimeType);
        
    }
    
}