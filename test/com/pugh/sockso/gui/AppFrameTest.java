
package com.pugh.sockso.gui;

import com.pugh.sockso.web.Server;
import com.pugh.sockso.tests.TestLocale;

import junit.framework.TestCase;

import static org.easymock.EasyMock.*;

public class AppFrameTest extends TestCase {

    public void testUpdateUrlLabel() {

        final Server sv = createMock( Server.class );
        expect( sv.getProtocol() ).andReturn( "" ).times( 1 );
        expect( sv.getHost() ).andReturn( "MYHOST" ).times( 1 );
        replay( sv );
        
        final AppFrame a = new AppFrame( null, null, sv, null, new TestLocale() );
        a.updateUrlLabel();
        
        assertTrue( a.getUrlLabel().getText().contains("MYHOST") );
        
    }
}
