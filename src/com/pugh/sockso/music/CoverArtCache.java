/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pugh.sockso.music;

import com.pugh.sockso.Utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.google.inject.Singleton;

@Singleton
public class CoverArtCache {

    public static final String[] CACHE_IMAGE_EXTENSIONS = {"jpg", "gif", "png"};
    public static final String DEFAULT_IMAGE_TYPE = "jpg";

    private boolean isCached(final String itemName, final String extension) {

        File coverFile = getCoverCacheFile(itemName, extension);
        return (coverFile.isFile() && coverFile.exists());
    }

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

        BufferedImage image = cover.getImage();
        String extension = DEFAULT_IMAGE_TYPE;
        File imageFile = getCoverCacheFile(cover.getItemName(), extension);
        ImageIO.write(image, extension, imageFile);
    }

    public CoverArt getCoverArt(String itemName) throws IOException {

        CoverArt cover = null;
        String ext = getCachedImageExtension(itemName);
        if (ext != null) {
            File coverFile = getCoverCacheFile(itemName, ext);
            BufferedImage image = ImageIO.read(coverFile);
            cover = new CoverArt(itemName, image);
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
}
