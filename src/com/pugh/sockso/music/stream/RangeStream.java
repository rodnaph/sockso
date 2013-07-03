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
 * For client requested streaming from arbitrary starting point
 *
 * @author Nathan Perrier
 */
public class RangeStream extends AbstractMusicStream {

    private static final Logger log = Logger.getLogger(RangeStream.class);

    private Range range;

    public RangeStream( final Track track, final Range range ) {
        super(track);
        this.range = range;
    }

    @Override
    public void setHeaders( final Response response ) {
        super.setHeaders(response);
        
        final long trackLength = new File(this.track.getPath()).length();

        // set headers required to satisfy Range requests:
        // Content-Length: 2980
        final long contentLength = range.getLength();
        // Content-Range: bytes 1000-3979/3980
        final String contentRange = "bytes " + range.getStart() + "-" + range.getEnd() + "/" + trackLength;

        response.setStatus(206); // Partial Content
        response.addHeader("Content-Range", contentRange);
        response.addHeader("Content-Length", Long.toString(contentLength));
        response.addHeader("Accept-Ranges", "bytes");

    }

    /**
     * Sends the music stream to the client, optionally handling range
     * requests
     *
     * @throws java.io.IOException
     *
     */
    public void sendAudioStream( final DataOutputStream client ) throws IOException {

        DataInputStream audio = getAudioStream();

        long contentLength = this.range.getLength();

        byte[] buffer = new byte[STREAM_BUFFER_SIZE];
        int readBlock = STREAM_BUFFER_SIZE;
        long totalBytes = 0;
        
        try {

            audio.skip(this.range.start);
            log.debug("Skipped " + this.range.start + " bytes");

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

    
    /**
     * Simple pair of values with the constraint that start &lt; end
     */
    public static class Range {

        private long start;
        private long end;

        public Range( long start, long end ) {
            assert start >= 0;
            assert end > 0;
            assert start < end;

            this.start = start;
            this.end = end;
        }

        public long getStart() {
            return this.start;
        }

        public long getEnd() {
            return this.end;
        }

        // start and end represent byte positions, so we need to +1
        public long getLength() {
            return end - start + 1;
        }
    }

}
