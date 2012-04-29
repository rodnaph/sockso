package com.pugh.sockso.web.action.covers;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.tests.SocksoTestCase;

public class RemoteCovererTest extends SocksoTestCase {

    private RemoteCoverer coverer;

    @Override
    protected void setUp() {
        coverer = new RemoteCoverer();
        coverer.setProperties( new StringProperties() );
    }

    public void testServeCover() throws Exception {
    }

    public void testFalseReturnedWhenRemoteCoversDisabled() throws Exception {
        coverer.getProperties()
               .set( Constants.COVERS_DISABLE_REMOTE_FETCHING, Properties.YES );
        assertFalse( coverer.serveCover("ar123") );
    }

    public void testServeCoverNotFound() throws Exception {
    }

}
