
package com.pugh.sockso.web.action.covers;

import com.pugh.sockso.music.CoverArt;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestLocale;


public class DefaultCovererTest extends SocksoTestCase {

    
    public void testGetNoCoverArt() throws Exception {
        DefaultCoverer coverer = new DefaultCoverer();
        Locale locale = new TestLocale();
        String id = "nocover-" + locale.getLangCode();

        CoverArt expResult = new CoverArt(id, CoverArt.createNoCoverImage(locale));
        CoverArt result = coverer.getNoCoverArt();
        assertEquals(expResult, result);
    }

}
