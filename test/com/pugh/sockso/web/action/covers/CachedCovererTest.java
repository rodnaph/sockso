
package com.pugh.sockso.web.action.covers;

import com.pugh.sockso.music.CoverArt;
import com.pugh.sockso.music.CoverArtCache;
import com.pugh.sockso.tests.SocksoTestCase;

import static org.easymock.EasyMock.*;


public class CachedCovererTest extends SocksoTestCase {


    public void testServeCover() throws Exception {

        String id = "ar123";
        CoverArt cachedCoverArt = new CoverArt(id);

        // TODO: Need interface for CoverArtCache?
        final CoverArtCache cache = createNiceMock(CoverArtCache.class);
        expect(cache.isCached(id)).andReturn(Boolean.TRUE);
        expect(cache.getCoverArt(id)).andReturn(cachedCoverArt);
        replay(cache);

        CachedCoverer coverer = new CachedCoverer();
        boolean result = coverer.serveCover(id);

        assertEquals(true, result);
    }

}
