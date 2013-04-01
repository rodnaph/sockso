
/**
 * Uses jaudiotagger to read mp3 v1 & v2 tags
 *
 */

package com.pugh.sockso.music.tag;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v24Frames;
import org.jaudiotagger.tag.id3.ID3v24Tag;

public class Mp3Tag extends AudioTag {

    private static final Logger log = Logger.getLogger( AudioTag.class );

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

        } catch ( Exception e ) {
            log.error( "Error parsing MP3 tag: " + e.getMessage() );
        }

    }

    private void parseID3v2Tag( MP3File f ) {

        ID3v24Tag v2tag  = f.getID3v2TagAsv24();

        artistTitle = v2tag.getFirst( ID3v24Frames.FRAME_ID_ARTIST );
        albumTitle = v2tag.getFirst( ID3v24Frames.FRAME_ID_ALBUM );
        albumArtist = v2tag.getFirst( FieldKey.ALBUM_ARTIST );
        trackTitle = v2tag.getFirst( ID3v24Frames.FRAME_ID_TITLE );
        albumYear = v2tag.getFirst( ID3v24Frames.FRAME_ID_YEAR );
        genre = v2tag.getFirst( ID3v24Frames.FRAME_ID_GENRE );
        String trackN = v2tag.getFirst( ID3v24Frames.FRAME_ID_TRACK );

        try {
            trackNumber = Integer.parseInt( trackN );
        } catch ( final NumberFormatException e ) {
            log.warn("Could not parse track number: " + trackN, e);
        }

        Artwork artwork = v2tag.getFirstArtwork();
        if ( artwork != null ) {
            try {
                coverArt = artwork.getImage();
            } catch (final IOException ioe) {
                log.warn("Unable to extract cover art image: " + ioe.getMessage());
            }
        }

    }

    private void parseID3v1Tag( MP3File f ) {

        ID3v1Tag tag = f.getID3v1Tag();

        try {

            if ( artistTitle.equals( "" ) )
                artistTitle = tag.getArtist().get(0).toString();
            if ( albumTitle.equals( "" ) )
                albumTitle = tag.getAlbum().get(0).toString();
            if ( trackTitle.equals( "" ) )
                trackTitle = tag.getTitle().get(0).toString();
            if ( albumYear.equals( "" ) )
                albumYear = tag.getYear().get(0).toString();
            if ( genre.equals( "" ) )
                genre = tag.getGenre().get(0).toString();
            if ( trackNumber == 0 ) {
                    String trackN = tag.getTrack().get(0).toString();
                    try {
                    trackNumber = Integer.parseInt( trackN );
                } catch ( final NumberFormatException e ) {
                    log.warn("Could not parse track number " + trackN, e);
                }
            }

        } catch ( final Exception e ) {}

    }

}
