
package com.pugh.sockso.db;

import com.pugh.sockso.tests.SocksoTestCase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import joptsimple.OptionSet;

import static org.easymock.EasyMock.*;

public class JDBCDatabaseTest extends SocksoTestCase {

    public void testUpdate() throws Exception {
        
        final Statement st = createMock( Statement.class );
        expect( st.executeUpdate((String)anyObject()) ).andReturn( 0 );
        st.close();
        replay( st );
        
        final Connection cnn = createMock( Connection.class );
        expect( cnn.createStatement() ).andReturn( st );
        replay( cnn );

        final MyJDBCDatabase db = new MyJDBCDatabase( cnn );

        assertTrue( db.update(" some sql... ") == 0 );
        
        verify( cnn );
        verify( st );
        
    }

    public void testUpdateFailed() throws Exception {
        
        final Statement st = createMock( Statement.class );
        expect( st.executeUpdate((String)anyObject()) ).andThrow( new SQLException() );
        st.close();
        replay( st );
        
        final Connection cnn = createMock( Connection.class );
        expect( cnn.createStatement() ).andReturn( st );
        replay( cnn );

        final MyJDBCDatabase db = new MyJDBCDatabase( cnn );
        boolean gotException = false;
        
        try {
            db.update(" some sql... " );
        }
        catch ( final SQLException e ) {
            gotException = true;
        }
        
        assertTrue( gotException );
        
        verify( cnn );
        verify( st );
        
    }

    public void testPrepare() throws Exception {
        
        final PreparedStatement st1 = createMock( PreparedStatement.class );
        replay( st1 );
        
        final Connection cnn = createMock( Connection.class );
        expect( cnn.prepareStatement((String)anyObject()) ).andReturn( st1 );
        replay( cnn );
        
        final MyJDBCDatabase db = new MyJDBCDatabase( cnn );
        final PreparedStatement st2 = db.prepare( " some sql " );
        
        assertEquals( st1, st2 );
        
        verify( cnn );
        verify( st1 );
        
    }
    
    public void testPropertyExists() throws Exception {

        final ResultSet rs = createMock( ResultSet.class );
        expect( rs.next() ).andReturn( true );
        rs.close();
        replay( rs );
        
        final PreparedStatement st = createNiceMock( PreparedStatement.class );
        expect( st.executeQuery() ).andReturn( rs );
        st.close();
        replay( st );
        
        final Connection cnn = createMock( Connection.class );
        expect( cnn.prepareStatement((String)anyObject()) ).andReturn( st );
        replay( cnn );

        final MyJDBCDatabase db = new MyJDBCDatabase( cnn );
        
        assertTrue( db.propertyExists("someProp") );
        
        verify( cnn );
        verify( st );
        verify( rs );
        
    }
    
    public void testUpdateProperty() throws Exception {

        final PreparedStatement st = createNiceMock( PreparedStatement.class );
        expect( st.execute() ).andReturn( true );
        st.close();
        replay( st );
        
        final Connection cnn = createMock( Connection.class );
        expect( cnn.prepareStatement((String)anyObject()) ).andReturn( st );
        replay( cnn );

        final MyJDBCDatabase db = new MyJDBCDatabase( cnn );
        
        assertTrue( db.updateProperty( "foo", "bar" ) );
        
        verify( cnn );
        verify( st );

    }
    
    public void testUpdatePropertyFailed() throws Exception {

        final PreparedStatement st = createNiceMock( PreparedStatement.class );
        expect( st.execute() ).andReturn( false );
        st.close();
        replay( st );
        
        final Connection cnn = createMock( Connection.class );
        expect( cnn.prepareStatement((String)anyObject()) ).andReturn( st );
        replay( cnn );

        final MyJDBCDatabase db = new MyJDBCDatabase( cnn );
        
        assertFalse( db.updateProperty( "foo", "bar" ) );
        
        verify( cnn );
        verify( st );

    }
    
    public void testCreateProperty() throws Exception {

        final PreparedStatement st = createNiceMock( PreparedStatement.class );
        expect( st.execute() ).andReturn( true );
        st.close();
        replay( st );
        
        final Connection cnn = createMock( Connection.class );
        expect( cnn.prepareStatement((String)anyObject()) ).andReturn( st );
        replay( cnn );

        final MyJDBCDatabase db = new MyJDBCDatabase( cnn );
        
        assertTrue( db.createProperty( "foo", "bar" ) );
        
        verify( cnn );
        verify( st );

    }
    
    public void testCreatePropertyFailed() throws Exception {

        final PreparedStatement st = createNiceMock( PreparedStatement.class );
        expect( st.execute() ).andReturn( false );
        st.close();
        replay( st );
        
        final Connection cnn = createMock( Connection.class );
        expect( cnn.prepareStatement((String)anyObject()) ).andReturn( st );
        replay( cnn );

        final MyJDBCDatabase db = new MyJDBCDatabase( cnn );
        
        assertFalse( db.createProperty( "foo", "bar" ) );
        
        verify( cnn );
        verify( st );

    }
    
    class MyJDBCDatabase extends JDBCDatabase {
        
        private final Connection cnn;
        
        public MyJDBCDatabase( final Connection cnn ) {
            this.cnn = cnn;
        }
        
        public Connection getConnection() {
            return cnn;
        }
        
        public String escape( final String str ) { return ""; }
        public void close() {}
        public void connect( final OptionSet options ) {}
        public String getRandomFunction() { return "rand"; }
        
    }
    
}
