
package com.pugh.sockso.web.action.covers;

import com.pugh.sockso.music.CoverArt;
import com.pugh.sockso.resources.Locale;

import java.io.IOException;

public class DefaultCoverer extends BaseCoverer {
    
    /**
     *  Server the default cover, this will always return true
     * 
     *  @param itemName
     * 
     *  @return
     * 
     *  @throws IOException 
     * 
     */

    public boolean serveCover( final String itemName ) throws IOException {
        
        final Locale locale = getLocale();
        final String noCoverId = "nocover-" + locale.getLangCode();

        boolean coverIsCached = coverCache.isCached( noCoverId );
        CoverArt cover = getNoCoverArt( noCoverId );

        serveCover( cover, "noCover", coverIsCached );
        return true;
        
    }

    /**
     *  Returns the image to use to indicate no cover art was found
     * 
     *  @param itemName
     *
     *  @return
     * 
     *  @throws IOException 
     * 
     */

    protected CoverArt getNoCoverArt( String noCoverId ) throws IOException {

        final Locale locale = getLocale();

        return coverCache.isCached( noCoverId ) 
                ? coverCache.getCoverArt(noCoverId) 
                : new CoverArt(noCoverId, CoverArt.createNoCoverImage(locale));

    }
    
}
