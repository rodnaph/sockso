
package com.pugh.sockso.web.action.covers;

import com.google.inject.Inject;
import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.music.CoverArt;
import com.pugh.sockso.web.action.CoverSearch;

import java.io.IOException;

public class RemoteCoverer extends BaseCoverer {

    CoverSearch search;

    @Inject
    public RemoteCoverer( CoverSearch cs ) {
        this.search = cs;
    }

    /**
     *  Try and fetch a cover from a remote source (Amazon)
     * 
     *  @param itemName
     * 
     *  @return
     * 
     *  @throws IOException 
     * 
     */

    public boolean serveCover( final String itemName ) throws IOException {

        if ( !getProperties().get(Constants.COVERS_DISABLE_REMOTE_FETCHING).equals(Properties.YES) ) {

            final CoverArt cover = search.getCover(itemName);

            if ( cover != null ) {
                serveCover( cover, itemName, true );
                return true;
            }

        }

        return false;

    }

}
