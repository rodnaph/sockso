
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

    private boolean isCached(final String itemName, final String extension) {

        File coverFile = getCoverCacheFile(itemName, extension);
        return (coverFile.isFile() && coverFile.exists());
    }

    @Override
    public boolean isCached(final String itemName) {

        String ext = getCachedImageExtension(itemName);
        if (ext != null) {
            return true;
        }

        return false;
    }

    // Any image extension could exist in the cache.
    // Return the first one found, otherwise null
    protected String getCachedImageExtension(final String itemName) {

        for (String ext : CACHE_IMAGE_EXTENSIONS) {
            if (isCached(itemName, ext)) {
                return ext;
            }
        }

        return null;
    }

    public void addToCache(final CoverArt cover) throws IOException {
        
        CachedObject obj = new CachedObject(cover, -1);
        writeRaw(cover.getItemName(), obj);
    }

    public CoverArt getCoverArt(String itemName) throws IOException {
        
        CoverArt cover = null;
        CachedObject obj = readRaw(itemName);
        if(obj != null){
            cover = (CoverArt) obj.getValue();
        }
        else {
            throw new IOException("Couldn't read object from cache");
        }

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

    @Override
    protected CachedObject readRaw( String key ) {
        
        CachedObject obj = null;
        CoverArt cover = null;
        
        String ext = getCachedImageExtension(key);
        if (ext != null) {
            File coverFile = getCoverCacheFile(key, ext);
            try {
                BufferedImage image = ImageIO.read(coverFile);
                cover = new CoverArt(key, image);
                obj = new CachedObject(cover, -1);
            } 
            catch(IOException e) {
                // TODO
            }
        }

        return obj;
    }

    @Override
    protected void writeRaw( String key, CachedObject object ) {

        CoverArt cover = (CoverArt) object.getValue();
        BufferedImage image = cover.getImage();
        String extension = DEFAULT_IMAGE_TYPE;
        File imageFile = getCoverCacheFile(key, extension);
        try {
            ImageIO.write(image, extension, imageFile);
        } catch ( IOException e ) {
            // TODO
        }
    }
}
