/*
 * Tests the exporter class
 * 
 */

package com.pugh.sockso.db;

import com.pugh.sockso.tests.SocksoTestCase;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.apache.log4j.Logger;

import static org.easymock.EasyMock.*;

public class DBExporterTest extends SocksoTestCase {

    private static Logger log = Logger.getLogger( DatabaseTest.class );

    public void testExportXml() throws SQLException {
        
        DBExporter exporter = new DBExporter( getTestDatabase() );
        String eol = System.getProperty("line.separator");
        String expected = "<?xml version=\"1.0\"?>" +eol+eol+
                            "<results><row><name>value</name><another>" +
                            "some &amp; value &lt; &gt;</another></row></results>";        
        String actual = exporter.export( "", DBExporter.Format.XML );

        assertEquals( expected, actual );

    }
    
    public void testExportCsv() throws SQLException {
        
        DBExporter exporter = new DBExporter( getTestDatabase() );
        String expected = "name,another\n" +
                          "value,some & value < >\n";        
        String actual = exporter.export( "", DBExporter.Format.CSV );

        assertEquals( expected, actual );

    }
    
    public void testExportJson() throws SQLException {
        
        DBExporter exporter = new DBExporter( getTestDatabase() );
        String expected = "[\r\n" +
                          "{\"name\": \"value\",\"another\": \"some & value < >\"}]";
        String actual = exporter.export( "", DBExporter.Format.JSON );

        System.out.println( "Expected: " + expected + "END" );
        System.out.println( "Actual: " + actual + "END" );

        assertEquals( expected, actual );

    }
    
    private Database getTestDatabase() throws SQLException {
        
        Database db = createMock( Database.class );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        expect( st.executeQuery() ).andReturn( getTestResultSet() );
        st.close();
        replay( st );
        
        expect( db.prepare("") ).andReturn( st );
        replay( db );
        
        return db;

    }
    
    private ResultSet getTestResultSet() throws SQLException {
        
        ResultSetMetaData rsm = createMock( ResultSetMetaData.class );
        expect( rsm.getColumnCount() ).andReturn( 2 );
        expect( rsm.getColumnName(1) ).andReturn("name");
        expect( rsm.getColumnName(2) ).andReturn("another");
        replay( rsm );
        
        ResultSet rs = createMock( ResultSet.class );
        expect( rs.next() ).andReturn( true );
        expect( rs.getMetaData() ).andReturn( rsm ).times( 1 );
        expect( rs.getString("name") ).andReturn("value").times( 1 );
        expect( rs.getString("another") ).andReturn("some & value < >").times( 1 );
        expect( rs.next() ).andReturn( false );
        rs.close();
        replay( rs );
        
        return rs;
        
    }
    
}
