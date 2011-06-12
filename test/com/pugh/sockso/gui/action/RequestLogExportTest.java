
package com.pugh.sockso.gui.action;

import com.pugh.sockso.db.DBExporter;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;

import java.io.File;

import javax.swing.JComboBox;

public class RequestLogExportTest extends SocksoTestCase {

    protected static boolean eventFired;
    
    private TestDatabase db;
    
    private RequestLogExport action;
    
    private JComboBox formats;
    
    @Override
    protected void setUp() throws Exception {
        db = new TestDatabase();
        db.fixture( "requestLogs" );
        formats = new JComboBox( DBExporter.Format.values() );
        action = new RequestLogExport( null, db, null, formats );
        action.addListener(new RequestLogChangeListener() {
            public void requestLogChanged() { eventFired = true; }
        });
    }
    
    public void testLogIsSavedToFileSpecified() throws Exception {
        File saveFile = File.createTempFile( "testLog", "log" );
        action.exportRequestLog( saveFile );
        assertFileContains( saveFile, "<?xml" );
    }
    
    public void testFormatOfLoggedDataIsDeterminedByComboBox() throws Exception {
        File saveFile1 = File.createTempFile( "testLog1", "log" );
        formats.setSelectedIndex( 1 );
        action.exportRequestLog( saveFile1 );
        File saveFile2 = File.createTempFile( "testLog2", "log" );
        // table cleared after previous log, so need to reset db (can't truncate)
        TestDatabase db2 = new TestDatabase();
        db2.fixture( "requestLogs" );
        action = new RequestLogExport( null, db2, null, formats );
        formats.setSelectedIndex( 2 );
        action.exportRequestLog( saveFile1 );
        assertFilesNotEqual( saveFile1, saveFile2 );
    }
    
    public void testChangeEventIsFiredWhenLogIsExported() throws Exception {
        eventFired = false;
        action.exportRequestLog( File.createTempFile("testLog","log") );
        assertTrue( eventFired );
    }
    
    public void testChangeEventIsNotFiredWhenThereIsAnErrorExporting() {
        eventFired = false;
        try {
            action.exportRequestLog( new File("&*(@//123@!*(£&") );
        }
        catch ( Exception e ) {}
        assertFalse( eventFired );
    }
    
}
