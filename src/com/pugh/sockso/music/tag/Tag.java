
package com.pugh.sockso.music.tag;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 *  Specified the interface a tag needs to implement
 *
 *  @author rod
 *
 */

public interface Tag {

    /**
     *  Parses the audio file to extract tag information
     *
     *  @param file
     *
     */
    public void parse( final File file ) throws IOException;

    /**
     *  returns the name of the artist
     * 
     *  @return
     * 
     */
    
    public String getArtist();
    
    /**
     *  returns the name of the album
     *
     *  @return
     * 
     */
    
    public String getAlbum();
    
    /**
     *  returns the name of the track
     * 
     *  @return
     * 
     */
    
    public String getTrack();
    
    /**
     *  returns the track number
     * 
     *  @return
     * 
     */
    
    public int getTrackNumber();
    
    /**
     * returns the album year
     *
     * @return
     *
     */

    public String getAlbumYear();

    /**
     * returns the genre
     *
     * @return
     */
    public String getGenre();

    /**
     * returns the cover art
     *
     * @return
     *
     */

    public BufferedImage getCoverArt();

}
