package com.pugh.sockso.music.encoders;

import com.pugh.sockso.music.Track;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import org.apache.log4j.Logger;

/**
 *
 * @author Nathan Perrier
 */
public class CustomEncoder extends AbstractEncoder {

    protected final Logger log = Logger.getLogger( CustomEncoder.class );

    private String command;

    public CustomEncoder( String command ) {
        this.command = command;
    }

    public DataInputStream getAudioStream( Track track, int bitrate ) throws IOException {

        // break up users command into it's parts, then add the track we're
        // going to play, and the output redirection to the end.

        final String[] cmdArgs = command.split(" ");
        final String[] allArgs = new String[cmdArgs.length + 2];

        System.arraycopy(cmdArgs, 0, allArgs, 0, cmdArgs.length);

        allArgs[ cmdArgs.length] = track.getPath();
        allArgs[ cmdArgs.length + 1] = "-";

        log.debug("Encoding with custom command: " + Arrays.toString(allArgs));

        final ProcessBuilder pb = new ProcessBuilder(allArgs);
        final Process process = pb.start();

        return new DataInputStream(process.getInputStream());
    }
    
}
