
package com.pugh.sockso.tests;

import com.pugh.sockso.web.*;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.tests.TestUtils;

import java.io.OutputStream;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import static org.easymock.EasyMock.*;

/**
 *  simulates a response, and allows analysing the data sent
 * 
 */

public class TestResponse extends HttpResponse {
    
    final StringBuffer sb;

    /**
     *  constructors
     * 
     */

    public TestResponse() {

        this( null );

    }
        
    public TestResponse( final Database db ) {

        super( null, db, TestUtils.getProperties(), TestUtils.getLocale(), new User(1,"foo"), false );

        if ( db == null ) {

            try {

                final ResultSet rs = createNiceMock( ResultSet.class );
                expect( rs.next() ).andReturn( false );
                replay( rs );

                final PreparedStatement st = createNiceMock( PreparedStatement.class );
                expect( st.executeQuery() ).andReturn( rs );
                replay( st );

                final Database mockDb = createNiceMock( Database.class );
                expect( mockDb.prepare((String)anyObject()) ).andReturn( st );
                replay( mockDb );

                setDatabase( mockDb );

            }

            catch ( final Exception e ) {}

        }
        
        sb = new StringBuffer();

        setOutputStream( new OutputStream() {
            public void write( int i ) {
                sb.append( (char) i );
            }
        });

    }

    /**
     *  returns the data that has been output
     * 
     *  @return
     * 
     */

    public String getOutput() {

        return sb.toString();

    }
    
}
