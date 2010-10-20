
package com.pugh.sockso.music.encoders;

import com.pugh.sockso.music.Track;
import com.pugh.sockso.web.action.FileServer;

import java.io.IOException;
import java.io.DataInputStream;

/**
 * this class converts ogg vorbis to mp3 with oggdec and lame
 * 
 * @author rod
 * 
 */

public class OggDecToLame extends BuiltinScriptEncoder {

    public String[] getSupportedFormats() {
        return new String[] {
            "ogg"
        };
    }
    
    public DataInputStream getAudioStream( Track track, int bitrate ) throws IOException {
        return getAudioStreamFromScript( track, "oggDecToLame" );
    }

    @Override
    public String toString() {
        return "Convert to mp3 (oggdec and lame)";
    }
    
    public String getOutputMimeType() {
        return FileServer.getMimeType( "file.mp3" );
    }
    
}
