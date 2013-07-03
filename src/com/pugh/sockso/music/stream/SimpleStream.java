package com.pugh.sockso.music.stream;

import com.pugh.sockso.Utils;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.web.Response;
import com.pugh.sockso.web.action.AbstractMusicStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 *
 * @author Nathan Perrier
 */
public class SimpleStream extends AbstractMusicStream {

    private static final Logger log = Logger.getLogger(SimpleStream.class);

    public SimpleStream( final Track track ) {
       super(track);
    }

    @Override
    public void setHeaders( final Response response ) {
        super.setHeaders(response);

        final long length = new File(this.track.getPath()).length();

        response.addHeader("Content-Length", Long.toString(length));
        response.addHeader("Accept-Ranges", "bytes");

    }

    public void sendAudioStream( final DataOutputStream client ) throws IOException {

        final DataInputStream audio = getAudioStream();

        final long contentLength = new File(track.getPath()).length();

        final byte[] buffer = new byte[STREAM_BUFFER_SIZE];
        int readBlock = STREAM_BUFFER_SIZE;
        long totalBytes = 0;

        try {

            int lastPct = -1;
            for ( int bytesRead = 0; bytesRead >= 0 && totalBytes < contentLength; bytesRead = audio.read(buffer, 0, readBlock) ) {

                totalBytes += bytesRead;

                if ( totalBytes + readBlock > contentLength ) {
                    readBlock = (int) (contentLength - totalBytes);
                }

                client.write(buffer, 0, bytesRead);

                int pctRead = (int) (((double) totalBytes / (double) contentLength) * 100);

                if (pctRead > lastPct && pctRead % 5 == 0) {
                    lastPct = pctRead;
                    log.debug(String.format("Sent %2d%%", pctRead));
                }
            }

        } finally {
            Utils.close(audio);
            Utils.close(client);
        }

    }
}
