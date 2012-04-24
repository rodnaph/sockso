package com.pugh.sockso.web.action.covers;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.tests.SocksoTestCase;

import static org.easymock.EasyMock.*;


public class RemoteCovererTest extends SocksoTestCase {

    // remote fetching found cover
    public void testServeCover() throws Exception {
        // TODO
    }

    // remote fetching disabled
    public void testServeCoverDisabled() throws Exception {

        RemoteCoverer coverer = new RemoteCoverer();
        
        final Properties p = createNiceMock(Properties.class);
        expect(p.get(Constants.COVERS_DISABLE_REMOTE_FETCHING)).andReturn(Properties.NO);
        replay(p);

        coverer.setProperties(p);

        boolean result = coverer.serveCover("ar123");
        assertFalse(result);
    }

    // remote fetching enabled, but did not return a cover
    public void testServeCoverNotFound() throws Exception {
        // TODO
    }
}
