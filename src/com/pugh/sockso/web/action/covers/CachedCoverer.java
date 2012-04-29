
package com.pugh.sockso.web.action.covers;

import java.io.IOException;

public class CachedCoverer extends BaseCoverer {
    
    /**
     *  Try and serve a cached cover if there is one
     * 
     *  @param itemName
     * 
     *  @return
     * 
     *  @throws IOException 
     * 
     */

    public boolean serveCover( final String itemName ) throws IOException {

        if ( coverCache.isCached(itemName) ) {
            serveCover( 
                coverCache.getCoverArt( itemName ),
                itemName,
                false
            );
            return true;
        }

        return false;

    }

}
