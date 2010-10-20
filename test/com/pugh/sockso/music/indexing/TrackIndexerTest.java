
package com.pugh.sockso.music.indexing;

import com.pugh.sockso.tests.IntegrationTestCase;

import java.sql.ResultSet;

import java.io.File;
import java.io.FileFilter;

public class TrackIndexerTest extends IntegrationTestCase {

    public void testPerformance() throws Exception {

        final BaseIndexer indexer = new TrackIndexer( getDatabase() );
        final long start = System.currentTimeMillis();
        final long target = 7000;

        indexer.scan();

        final long total = System.currentTimeMillis() - start;

        System.out.println( "Target: " +target+ ", Actual: " +total );

        if ( total > target ) {
            fail( "Did not meet target: " +target+ ", actual: " +total );
        }

    }

    public void testGetIndex() throws Exception {

        final BaseIndexer indexer = new TrackIndexer( getDatabase() );
        final ResultSet rs = indexer.getFiles();

        assertNotNull( rs );

    }

    public void testGetDirectories() throws Exception {

        final BaseIndexer indexer = new TrackIndexer( getDatabase() );
        final ResultSet rs = indexer.getDirectories();

        assertNotNull( rs );

    }

}
