package com.pugh.sockso.music.indexing;

import com.pugh.sockso.Constants;
import com.pugh.sockso.music.CoverArt;
import com.pugh.sockso.music.CoverArtCache;
import com.pugh.sockso.Properties;

import java.io.IOException;

import org.apache.log4j.Logger;

public class CoverArtIndexer {

    private final Properties p;
    private static final Logger log = Logger.getLogger(CoverArtIndexer.class);

    public CoverArtIndexer(Properties properties) {
        this.p = properties;
    }

    public void indexCover(final CoverArt coverArt) {

        CoverArtCache coverCache = new CoverArtCache();
        // Check if the cover art has already been cached
        if (!coverCache.isCached(coverArt.getItemName())) {

            // Found a cover, now let's resize it
            coverArt.scale((int) p.get(Constants.DEFAULT_ARTWORK_WIDTH, 115),
                    (int) p.get(Constants.DEFAULT_ARTWORK_HEIGHT, 115));

            // Now let's cache it
            String imgExt = p.get(Constants.DEFAULT_ARTWORK_TYPE, "jpg");
            try {
                coverCache.addToCache(coverArt);
            } catch (IOException e) {
                log.error("Could not create cover image file " + coverArt.getItemName() + ": " + e.getMessage());
            }
        }
    }
}