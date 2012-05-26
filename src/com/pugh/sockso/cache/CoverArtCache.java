
package com.pugh.sockso.cache;

import com.pugh.sockso.Utils;
import com.pugh.sockso.music.CoverArt;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import com.google.inject.Singleton;


@Singleton
public class CoverArtCache extends TimedCache {

    public static final String[] CACHE_IMAGE_EXTENSIONS = {"jpg", "gif", "png"};
    public static final String DEFAULT_IMAGE_TYPE = "jpg";

    /**
     *  Indicates if the item is cached
     * 
     *  @param itemName
     *  @param extension
     * 
     *  @return 
     * 
     */

    private boolean isCached(final String itemName, final String extension) {

        final File coverFile = getCoverCacheFile(itemName, extension);

        return (coverFile.isFile() && coverFile.exists());

    }

    /**
     *  Indicates if the item is cached
     * 
     *  @param itemName
     * 
     *  @return 
     * 
     */

    @Override
    public boolean isCached(final String itemName) {

        final String ext = getCachedImageExtension(itemName);

        if (ext != null) {
            return true;
        }

        return false;

    }

    /**
     *  Any image extension could exist in the cache.
     *  Return the first one found, otherwise null
     * 
     *  @param itemName
     * 
     *  @return 
     * 
     */

    protected String getCachedImageExtension(final String itemName) {

        for ( final String ext : CACHE_IMAGE_EXTENSIONS ) {
            if ( isCached(itemName,ext) ) {
                return ext;
            }
        }

        return null;

    }

    /**
     *  Convenience method to add some cover art to the cache
     * 
     *  @param cover
     * 
     *  @throws CacheException 
     * 
     */

    public void addToCache(final CoverArt cover) throws CacheException {
        
        final CachedObject obj = new CachedObject(cover, -1);

        writeRaw(cover.getItemName(), obj);

    }

    /**
     *  Convenience method to fetch cover art from cache
     *  
     *  @param itemName
     * 
     *  @return
     * 
     *  @throws CacheException 
     * 
     */

    public CoverArt getCoverArt(String itemName) throws CacheException {
        
        final CachedObject obj = readRaw(itemName);
        final CoverArt cover = (CoverArt) obj.getValue();

        return cover;

    }

    /**
     *  returns the absolute path of the cache file
     *
     *  @param name
     *  @param ext
     *
     *  @return the cache file path
     *
     */

    protected File getCoverCacheFile(final String itemName, final String extension) {

        return new File(Utils.getCoversDirectory() + File.separator + itemName + "." + extension);
    }

    /**
     *  Implements raw reading of image cache files
     * 
     *  @param key
     * 
     *  @return
     * 
     *  @throws CacheException 
     * 
     */

    @Override
    protected CachedObject readRaw( String key ) throws CacheException {
        
        final String ext = getCachedImageExtension(key);

        if (ext != null) {

            final File coverFile = getCoverCacheFile(key, ext);

            try {
                final BufferedImage image = ImageIO.read(coverFile);
                final CoverArt cover = new CoverArt(key, image);
                return new CachedObject(cover, -1);
            } 

            catch(IOException e) {
                throw new CacheException("Error reading image: " + coverFile.toString(), e );
            }

        }

        return null;

    }

    /**
     *  Implements raw writing of data to cache
     * 
     *  @param key
     * 
     *  @param object
     * 
     *  @throws CacheException 
     * 
     */

    @Override
    protected void writeRaw( String key, CachedObject object ) throws CacheException {

        final CoverArt cover = (CoverArt) object.getValue();
        final BufferedImage image = cover.getImage();
        final String extension = DEFAULT_IMAGE_TYPE;
        final File imageFile = getCoverCacheFile(key, extension);

        try {
            ImageIO.write(image, extension, imageFile);
        } 
        
        catch ( final IOException e ) {
            throw new CacheException("Error w\riting image to file: " + imageFile.toString(), e);
        }

    }

}
