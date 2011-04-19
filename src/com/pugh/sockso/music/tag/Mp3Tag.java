
/**
 * Uses jaudiotagger to read mps v1 & v2 tags
 *
 */

package com.pugh.sockso.music.tag;

import java.io.File;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v24Frames;
import org.jaudiotagger.tag.id3.ID3v24Tag;

public class Mp3Tag extends AudioTag {

	/**
     *  Returns ID3Data for the file.
     *
     *  We would prefer to use ID3V2 tags, so we try to parse the ID3V2
     *  tags first, and then v1 to get any missing tags.
     *
     */

    public void parse( final File file ) {

        try {

            MP3File f = (MP3File) AudioFileIO.read( file );
            if ( f.hasID3v2Tag() )
                parseID3v2Tag( f );
            if ( f.hasID3v1Tag() )
                parseID3v1Tag( f );

        } catch ( Exception e ) {}

    }

    private void parseID3v2Tag(MP3File f) {

        ID3v24Tag v2tag  = f.getID3v2TagAsv24();

        artistTitle = v2tag.getFirst( ID3v24Frames.FRAME_ID_ARTIST );
        albumTitle = v2tag.getFirst( ID3v24Frames.FRAME_ID_ALBUM );
        trackTitle = v2tag.getFirst( ID3v24Frames.FRAME_ID_TITLE );
        String trackN = v2tag.getFirst( ID3v24Frames.FRAME_ID_TRACK );

        try {
            trackNumber = Integer.parseInt( trackN );
        } catch ( final NumberFormatException ignored ) {}

    }

    private void parseID3v1Tag(MP3File f) {

        ID3v1Tag tag = f.getID3v1Tag();

        try {

            if ( artistTitle.equals( "" ) )
                artistTitle = new String ( tag.getArtist().get(0).toString() );
            if ( albumTitle.equals( "" ) )
                albumTitle = new String ( tag.getAlbum().get(0).toString() );
            if ( trackTitle.equals( "" ) )
                trackTitle = new String ( tag.getTitle().get(0).toString() );
            if ( trackNumber == 0 )
                try {
                    String trackN = new String ( tag.getTrack().get(0).toString() );
                    trackNumber = Integer.parseInt( trackN );
                } catch ( final NumberFormatException ignored ) {}

        } catch ( final Exception e ) {}

    }

}
