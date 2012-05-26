
package com.pugh.sockso.web.action.covers;

import com.pugh.sockso.music.CoverArt;
import com.pugh.sockso.music.CoverArtCache;
import com.pugh.sockso.tests.SocksoTestCase;

import java.io.IOException;

public class CachedCovererTest extends SocksoTestCase {

    private CachedCoverer coverer;

    @Override
    protected void setUp() {
        coverer = new NeuteredCachedCoverer();
        coverer.coverCache = new FakeCoverArtCache();
    }
    
    public void testServeCoverReturnsTrueWhenCoverIsCached() throws Exception {
        assertTrue( coverer.serveCover("123") );
    }

    public void testServeCoverReturnsFalseWhenCoverIsNotCached() throws Exception {
        ( (FakeCoverArtCache) coverer.coverCache ).isCached = false;
        assertFalse( coverer.serveCover("123") );
    }

}

class FakeCoverArtCache extends CoverArtCache {
    public boolean isCached = true;
    @Override
    public boolean isCached( String name ) {
        return isCached;
    }
    @Override
    public CoverArt getCoverArt(String item){
        return new CoverArt(item);
    }
}

// does not do any IO
class NeuteredCachedCoverer extends CachedCoverer {
    @Override
    protected void serveCover( final CoverArt cover, final String itemName, final boolean addToCache) throws IOException {}
}
