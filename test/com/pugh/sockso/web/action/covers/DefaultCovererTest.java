
package com.pugh.sockso.web.action.covers;

import com.pugh.sockso.music.CoverArt;
import com.pugh.sockso.music.CoverArtCache;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestLocale;
import java.io.IOException;

public class DefaultCovererTest extends SocksoTestCase {

    private DefaultCoverer coverer;

    @Override
    protected void setUp() {
        coverer = new NeuteredDefaultCoverer();
        coverer.setLocale( new TestLocale() );
        coverer.coverCache = new CoverArtCache();
    }

    public void testDefaultCovererReturnsTrue() throws Exception {
        assertTrue( coverer.serveCover("") );
    }

    public void testCoverIsAddedToResponse() throws Exception {
    }

}

class NeuteredDefaultCoverer extends DefaultCoverer {
    protected void serveCover( final CoverArt cover, final String itemName, final boolean addToCache) throws IOException {}
}