package com.pugh.sockso.music.tag;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.pugh.sockso.music.tag.aac.AACMetaData;

public class AACTag extends AudioTag {

    /**
     * Alternate method, calls external faad program and parses
     * stderr (which for some reason faad uses instead of stdout) to
     * retrieve tag information.
     *
     * @param file
     * @throws IOException
     */
    public void faad( File file ) throws IOException {

        ProcessBuilder pb = new ProcessBuilder(new String[]{ "sh", "scripts/unix/aactag.sh", file.getAbsolutePath() });

        Process proc = pb.start();

        InputStream inputstream = proc.getInputStream();
        InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

        String line;
        while ( (line = bufferedreader.readLine()) != null ) {
        }

        bufferedreader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

        while ( (line = bufferedreader.readLine()) != null ) {
            if ( line.startsWith("title: ") ) {
                this.trackTitle = line.substring("title: ".length());
            } else if ( line.startsWith("track: ") ) {
                try {
                    this.trackNumber = Integer.parseInt(line.substring("track: ".length()));
                } catch (NumberFormatException nfe) {
                }
            } else if ( line.startsWith("album: ") ) {
                this.albumTitle = line.substring("album: ".length());
            } else if ( line.startsWith("artist: ") ) {
                this.artistTitle = line.substring("artist: ".length());
            } else if ( line.startsWith("genre: ") ) {
                this.genre = line.substring("genre: ".length());
            }
        }

        try {
            if ( proc.waitFor() != 0 ) {
            }
        } catch (InterruptedException e) {
        }
    }

    public void parse( File file ) throws IOException {
        AACMetaData aacMetaData = new AACMetaData(file);
        this.albumTitle = aacMetaData.getAlbum();
        this.artistTitle = aacMetaData.getArtist();
        this.trackNumber = aacMetaData.getNumber();
        this.trackTitle = aacMetaData.getTrack();
        this.genre = aacMetaData.getGenre();
    }
}
