package com.pugh.sockso.web.action.covers;

import com.pugh.sockso.music.CoverArt;
import com.pugh.sockso.web.action.CoverSearch;
import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.tests.SocksoTestCase;

import java.io.IOException;

import static org.easymock.EasyMock.*;

public class RemoteCovererTest extends SocksoTestCase {

    public void testServeCover() throws Exception {
	
	final CoverSearch coverSearch = createNiceMock( CoverSearch.class );
	final CoverArt coverArt = new CoverArt("ar123");
        expect( coverSearch.getCover("ar123") ).andReturn( coverArt );
        replay( coverSearch );
	
        RemoteCoverer coverer = new NeuteredRemoteCoverer( coverSearch );
	coverer.setProperties( new StringProperties() );

        assertTrue( coverer.serveCover("ar123") );
        verify( coverSearch );	
    }

    public void testFalseReturnedWhenRemoteCoversDisabled() throws Exception {
	
	final CoverSearch coverSearch = createMock( CoverSearch.class );
	
	RemoteCoverer coverer = new NeuteredRemoteCoverer( coverSearch );
        coverer.setProperties( new StringProperties() );

        coverer.getProperties()
               .set( Constants.COVERS_DISABLE_REMOTE_FETCHING, Properties.YES );
        assertFalse( coverer.serveCover("ar123") );
    }

    public void testServeCoverNotFound() throws Exception {
	
	final CoverSearch coverSearch = createNiceMock( CoverSearch.class );
        expect( coverSearch.getCover("ar123") ).andReturn( null );
        replay( coverSearch );
	
        RemoteCoverer coverer = new NeuteredRemoteCoverer( coverSearch );
	coverer.setProperties( new StringProperties() );

        assertFalse( coverer.serveCover("ar123") );
        verify( coverSearch );
    }
    
    // does not do any IO
    class NeuteredRemoteCoverer extends RemoteCoverer {
	
	NeuteredRemoteCoverer( CoverSearch cs ){
	    super(cs);
	}
	
	@Override
        protected void serveCover( final CoverArt cover, final String itemName, final boolean addToCache) throws IOException {}
    }
    
}
