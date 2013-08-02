
package com.pugh.sockso.music.encoders;

import com.pugh.sockso.music.Track;

import java.io.IOException;
import java.io.DataInputStream;

/**
 *  Re-encodes flac files to mp3
 * 
 */

public class FlacToLame extends BuiltinScriptEncoder {

    public String[] getSupportedFormats() {
        return new String[] {
            "flac"
        };
    }
    
    public DataInputStream getAudioStream( Track track, int bitrate ) throws IOException {
        return getAudioStreamFromScript( track, "flacToLame" );
    }

    @Override
    public String toString() {
        return "Convert to mp3 (flac and lame)";
    }

}
