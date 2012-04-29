
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
        
        serveCover( getNoCoverArt(), "noCover", false );
        
        return true;
        
    }

    /**
     *  Returns the image to use to indicate no cover art was found
     * 
     *  @return
     * 
     *  @throws IOException 
     * 
     */

    protected CoverArt getNoCoverArt() throws IOException {

        final Locale locale = getLocale();
        final String noCoverId = "nocover-" + locale.getLangCode();

        return coverCache.isCached( noCoverId )
            ? coverCache.getCoverArt(noCoverId)
            : new CoverArt(noCoverId, CoverArt.createNoCoverImage(locale));

    }
    
}
