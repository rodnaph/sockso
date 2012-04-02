package com.pugh.sockso.music;

import com.pugh.sockso.tests.SocksoTestCase;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class CoverArtCacheTest extends SocksoTestCase {

    CoverArtCache cache;

    @Override
    protected void setUp() throws Exception {
        cache = new CoverArtCache() {
            // Overidden to return a temp dir rather than the application dir
            @Override
            protected File getCoverCacheFile(final String itemName, final String extension) {
                return new File(System.getProperty("java.io.tmpdir") + File.separator + itemName + "." + extension);
            }
        };
    }

    @Override
    protected void tearDown() throws Exception {
        cache = null;
    }

    public void testIsCached() throws IOException {
        String itemName = "al123";
        BufferedImage image = ImageIO.read(new File("test/data/covers/" + itemName + ".jpg"));
        CoverArt coverArt = new CoverArt(itemName, image);

        cache.addToCache(coverArt);

        boolean expResult = true;
        boolean result = cache.isCached(itemName);
        assertEquals(expResult, result);
    }

    public void testGetCachedImageExtension() throws IOException {
        String itemName = "al123";
        BufferedImage image = ImageIO.read(new File("test/data/covers/" + itemName + ".jpg"));
        CoverArt coverArt = new CoverArt(itemName, image);

        String expResult = "jpg";
        String result = cache.getCachedImageExtension(itemName);
        assertEquals(expResult, result);
    }

    public void testGetCoverArt() throws Exception {
        String itemName = "al123";
        BufferedImage image = ImageIO.read(new File("test/data/covers/" + itemName + ".jpg"));
        CoverArt coverArt = new CoverArt(itemName, image);

        cache.addToCache(coverArt);

        CoverArt result = cache.getCoverArt(itemName);
        assertEquals(coverArt.getItemName(), result.getItemName());
        assertNotNull(coverArt.getImage());
        assertNotNull(result.getImage());
    }
}
