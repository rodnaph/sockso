
package com.pugh.sockso.commands;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.CollectionManager;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;

import java.io.File;

import java.sql.SQLException;

import static org.easymock.EasyMock.*;

public class ColScanTest extends SocksoTestCase {

    private ColScan cmd;
    private CollectionManager cm;
    private Database db;

    @Override
    public void setUp() throws Exception {
        db = new TestDatabase();
        db.update( "insert into collection ( id, path ) values ( 1, '/home/user/music' )" );
        cm = createMock( CollectionManager.class );
        cmd = new ColScan( cm, db );
    }

    public void testColScanCommandChecksEntireCollectionByDefault() throws SQLException {
        cm.checkCollection();
        replay( cm );
        cmd.execute(new String[] { "colscan" } );
        verify( cm );
    }

    public void testDirectoryToScanCanBeSpecified() throws SQLException {
        cm.scanDirectory( 1, new File("/home/user/music/") );
        replay( cm );
        cmd.execute(new String[] { "colscan", "/home/user/music" });
        verify( cm );
    }

    public void testErrorReturnedWhenInvalidDirectoryToScanSpecified() throws SQLException {
        assertEquals( "Invalid directory", cmd.execute(new String[] { "colscan", "/music/folder" }) );
    }

}
