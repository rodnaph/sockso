
package com.pugh.sockso.music.encoders;

import com.pugh.sockso.music.Track;
import com.pugh.sockso.web.action.FileServer;

import java.io.IOException;
import java.io.DataInputStream;

/**
 *  uses ffmpeg and lame to re-encode from wma to mp3
 * 
 */

public class FfmpegToLame extends BuiltinScriptEncoder {

    public String[] getSupportedFormats() {
        return new String[] {
            "wma", "m4a"
        };
    }
    
    public DataInputStream getAudioStream( Track track, int bitrate ) throws IOException {
        return getAudioStreamFromScript( track, "ffmpegToLame" );
    }

    @Override
    public String toString() {
        return "Convert to mp3 (ffmpeg and lame)";
    }
    
    public String getOutputMimeType() {
        return FileServer.getMimeType( "file.mp3" );
    }
    
}
