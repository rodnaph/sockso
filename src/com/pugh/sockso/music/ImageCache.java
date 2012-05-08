
package com.pugh.sockso.music;

import java.io.IOException;

/**
 *
 * @author Nathan Perrier
 */
public interface ImageCache {

    /**
     *  checks if the cover art image associated with itemName is cached
     * 
     *  @param itemName the properties object
     * 
     *  @return true if the cover art image is cached
     * 
     */
    public boolean isCached( final String itemName );

    /**
     *  Adds the cover art image to the cache
     * 
     *  @param cover the cover art to cache
     * 
     *  @throws IOException
     * 
     */
    public void addToCache( final CoverArt cover ) throws IOException;

    /**
     *  Retrieves the cover art from the cache
     * 
     *  @param itemName associated with cover art
     * 
     *  @return cover art, else null if not found
     * 
     *  @throws IOException
     * 
     */
    public CoverArt getCoverArt( String itemName ) throws IOException;

}
