
package com.pugh.sockso.music.indexing;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;

import java.io.File;
import java.io.FileFilter;

import java.sql.SQLException;
import java.sql.Date;

public class BaseIndexerTest extends SocksoTestCase implements IndexListener {

    private BaseIndexer indexer;
    private IndexEvent indexEvent;
    private TestDatabase db;

    @Override
    public void setUp() throws Exception {
        db = new TestDatabase();
        indexer = new TrackIndexer( db );
        indexer.addIndexListener( this );
        indexEvent = null;
    }

    @Override
    public void tearDown() {
        indexer = null;
    }

    public void indexChanged( final IndexEvent evt ) {
        indexEvent = evt;
    }

    public void testCheckExists() throws Exception {

        final File exists = new File( "./build.xml" );
        final File doesntExist = new File( "/this/does/not/exist/i/hope" );

        indexEvent = null;
        assertTrue( indexer.checkExists(exists,1) );
        assertNull( indexEvent );

        indexEvent = null;
        assertFalse( indexer.checkExists(doesntExist,1) );
        assertNotNull( indexEvent );
        assertEquals( indexEvent.getType(), IndexEvent.MISSING );

    }
    
    public void testCheckModified() throws SQLException {

        final File exists = new File( "./build.xml" );
        final Date noChange = new Date( exists.lastModified() );
        final Date hasChange = new Date( exists.lastModified() - 1000 );

        indexEvent = null;
        assertFalse( indexer.checkModified( exists, 1, noChange ) );
        assertNull( indexEvent );

        indexEvent = null;
        assertTrue( indexer.checkModified( exists, 134, hasChange ) );
        assertNotNull( indexEvent );
        assertEquals( indexEvent.getType(), IndexEvent.CHANGED );
        assertEquals( indexEvent.getFileId(), 134 );

        indexEvent = null;
        assertTrue( indexer.checkModified( exists, 143, null ) );
        assertNotNull( indexEvent );
        assertEquals( indexEvent.getType(), IndexEvent.CHANGED );
        assertEquals( indexEvent.getFileId(), 143 );

    }

    public void testMarkFileModified() throws Exception {

        db.update( " delete from indexer where id = -1" );

        assertTrue( indexer.markFileModified(-1,0) ); // new file
        assertTrue( indexer.markFileModified(-1,1) ); // update existing

    }

    public void testScanDirectory() throws Exception {

        final BaseIndexer i = new TrackIndexer( null );
        final File newFile = new File( "/not/in/collecsstion.mp3" );

        i.addIndexListener( this );

        final File dir = new File( "" ) {
            @Override
            public boolean canRead() {
                return true;
            }
            @Override
            public boolean exists() {
                return true;
            }
            @Override
            public File[] listFiles( final FileFilter filter ) {
                return new File[] { newFile };
            }
        };

        indexEvent = null;
        i.scan( 123, dir );
        assertNotNull( indexEvent );
        assertTrue( indexEvent.getFile().getAbsolutePath().equals( newFile.getAbsolutePath() ) );
        assertEquals( indexEvent.getFileId(), 123 );
        assertEquals( indexEvent.getType(), IndexEvent.UNKNOWN );

    }

    public void testUpdateCache() throws Exception {
        
        indexer.updateCache();
        
    }

    public void testCompleteEventFiredAfterScan() {
        final Indexer i = new TrackIndexer( new TestDatabase() );
        i.addIndexListener( this );
        i.scan();
        assertEquals( IndexEvent.COMPLETE, this.indexEvent.getType() );
    }

}
