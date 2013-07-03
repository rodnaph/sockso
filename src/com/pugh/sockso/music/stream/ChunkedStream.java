package com.pugh.sockso.music.stream;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.Utils;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.music.encoders.Encoder;
import com.pugh.sockso.web.Response;
import com.pugh.sockso.web.action.AbstractMusicStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

/**
 *
 * @author Nathan Perrier
 */
public class ChunkedStream extends AbstractMusicStream {

    private static final Logger log = Logger.getLogger(ChunkedStream.class);

    private Encoder encoder;
    private Properties props;
    
    private static final byte[] CRLF = "\r\n".getBytes();

    public ChunkedStream( final Track track, final Encoder encoder, final Properties props ) {
        super(track);
        this.encoder = encoder;
        this.props= props;
    }

    @Override
    public DataInputStream getAudioStream() throws IOException {

        final String ext = Utils.getExt(track.getPath());
        final String bitrateStr = this.props.get(Constants.PROP_ENCODERS_PREFIX + ext + ".bitrate");

        final int bitrate = bitrateStr.equals("")
                ? encoder.getDefaultBitrate()
                : Integer.valueOf(bitrateStr);

        return encoder.getAudioStream(this.track, bitrate);

    }

    @Override
    public void setHeaders( final Response response ) {
        super.setHeaders(response);

        response.addHeader("Transfer-Encoding", "chunked");
        
    }

    public void sendAudioStream( final DataOutputStream client ) throws IOException {

        final DataInputStream audio = getAudioStream();
        
        final ChunkedOutputStream out = new ChunkedOutputStream(client);

        final byte[] buffer = new byte[STREAM_BUFFER_SIZE];
        int readBlock = STREAM_BUFFER_SIZE;
        long totalBytes = 0;

        try {

            int nextRead = -1;
            int bytesRead;
            while( (bytesRead = audio.read(buffer, 0, readBlock)) > 0 ) {

                out.write(buffer, 0, bytesRead);

                totalBytes += bytesRead;

                if ( totalBytes > nextRead ) {
                    nextRead += STREAM_BUFFER_SIZE * 12; // print ~every 100 KB
                    log.debug(String.format("Sent %2d%%", bytesRead));
                }
            }

        } finally {
            Utils.close(audio);
            Utils.close(out);
        }

    }

    static class ChunkedOutputStream extends OutputStream {

        OutputStream output = null;

        public ChunkedOutputStream(OutputStream output) {

            this.output = output;
        }

        @Override
        public void write(int i) throws IOException {

            write(new byte[] { (byte) i }, 0, 1);
        }

        @Override
        public void write(byte[] b, int offset, int length) throws IOException {

            writeHeader(length);

            output.write(CRLF, 0, CRLF.length);
            output.write(b, offset, length);
            output.write(CRLF, 0, CRLF.length);
        }

        @Override
        public void flush() throws IOException {

            output.flush();
        }

        @Override
        public void close() throws IOException {

            writeHeader(0);

            output.write(CRLF, 0, CRLF.length);
            output.write(CRLF, 0, CRLF.length);

            output.close();
        }

        private void writeHeader( int length ) throws IOException {

            byte[] header = Integer.toHexString(length).getBytes();

            output.write(header, 0, header.length);
        }
    }

}
