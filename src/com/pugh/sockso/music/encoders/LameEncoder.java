/*
 *  Allows streaming of data using lame
 * 
 */

package com.pugh.sockso.music.encoders;

import com.pugh.sockso.music.Track;
import com.pugh.sockso.music.Files;

import java.io.IOException;
import java.io.DataInputStream;

public class LameEncoder extends AbstractBuiltinEncoder {

    @Override
    public String toString() {
        return "Lame";
    }
    
    public String[] getSupportedFormats() {
        return new String[] {
            "mp3"
        };
    }

    public DataInputStream getAudioStream( Track track, int bitrate ) throws IOException {

        String args[] = new String[] {
            "lame",
            "-b", String.valueOf(bitrate),
            "--tt", track.getName(),
            "--ta", track.getArtist().getName(),
            "--tl", track.getAlbum().getName(),
            "--add-id3v2",
            "--silent",
            track.getPath(),
            "-"
        };
                
        return getAudioStreamFromCmd( args );

    }

    public String getOutputMimeType() {
        return Files.getMimeType( "file.mp3" );
    }

}
