/*
 * PropertiesTest.java
 *
 * Created on June 2, 2007, 10:09 PM
 * 
 */

package com.pugh.sockso;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.tests.SocksoTestCase;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import static org.easymock.EasyMock.*;

public class DBPropertiesTest extends SocksoTestCase {
    
    private static Logger log = Logger.getLogger( DBPropertiesTest.class  );
    
    public void testSave() {
        try {

            final PreparedStatement st = createNiceMock( PreparedStatement.class );
            expect( st.execute() ).andReturn( true );
            replay( st );
            
            Database db = createMock( Database.class );
            expect( db.prepare((String)anyObject()) ).andReturn( st ).anyTimes();
            
            replay( db );
            
            final Properties p = new DBProperties( db );
            p.save();
            
        }
        catch ( SQLException e ) {
            fail( e.getMessage() );
        }
    }

}
