
package com.pugh.sockso.web.action;

import java.awt.image.BufferedImage;

/**
 *  an interface for classes that implement cover searching
 *
 */

public interface CoverSearch {
    
    /**
     *  fetches a cover for a given itemName (eg. ar3456).  if nothing is
     *  found then null is returned.
     * 
     *  @param itemName
     * 
     *  @return
     * 
     */
    
    public BufferedImage getCover( final String itemName );

}
