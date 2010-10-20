
package com.pugh.sockso.web.action;

import com.pugh.sockso.tests.TestUtils;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.tests.TestResponse;
import com.pugh.sockso.tests.SocksoTestCase;

import static org.easymock.EasyMock.*;

public class ErrorerTest extends SocksoTestCase {

    public void testConstructor() {
        
        final Errorer err = new Errorer( null, false );
        
        assertNotNull( err );
        
    }
    
    public void testShowBurp() throws Exception {
        
        showBurp( 400, "", "no referer" );
        showBurp( 401, "localhost", "local error" );
        showBurp( 404, "www.mydomain.com", "remote error" );
        
    }
    
    private void showBurp( final int statusCode, final String referer, final String expectContains ) throws Exception {
        
        final Request req = createMock( Request.class );
        expect( req.getHeader("Host") ).andReturn( "localhost" );
        expect( req.getHeader("Referer") ).andReturn( referer );
        replay( req );
        
        final TestResponse res = new TestResponse();
        
        final BadRequestException e = new BadRequestException( "something", statusCode );
        
        final Errorer err = new Errorer( e, false );
        err.setProperties( TestUtils.getProperties() );
        err.setRequest( req );
        err.setResponse( res );
        
        err.showBurp();
        
        verify( req );
        
        final String output = res.getOutput();
        
        assertTrue( output.contains("error: " +expectContains) );
        
    }

}
