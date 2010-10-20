
package com.pugh.sockso.gui;

import com.pugh.sockso.web.IpFinder;
import com.pugh.sockso.web.Server;
import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.resources.Locale;

import junit.framework.TestCase;

import static org.easymock.EasyMock.*;

public class AppFrameTest extends TestCase {

    private Resources r;
    
    @Override
    public void setUp() {
        
        final Locale locale = createMock( Locale.class );
        expect( locale.getString((String)anyObject()) ).andReturn( "" ).anyTimes();
        replay( locale );
        
        r = createMock( Resources.class );
        expect( r.getCurrentLocale() ).andReturn( locale ).anyTimes();
        expect( r.getImage((String)anyObject()) ).andReturn( null ).anyTimes();
        replay( r );

    }

    public void testUpdateUrlLabel() {

        final Server sv = createMock( Server.class );
        expect( sv.getProtocol() ).andReturn( "" ).times( 1 );
        expect( sv.getHost() ).andReturn( "MYHOST" ).times( 1 );
        replay( sv );
        
        final AppFrame a = new AppFrame( null, null, sv, null, r, new IpFinder(null) );
        a.updateUrlLabel();
        
        assertTrue( a.getUrlLabel().getText().contains("MYHOST") );
        
        verify( r );
        
    }
}
