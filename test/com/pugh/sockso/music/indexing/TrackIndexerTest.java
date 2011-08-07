
package com.pugh.sockso.music.indexing;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;

import java.sql.ResultSet;

public class TrackIndexerTest extends SocksoTestCase {

    public void testPerformance() throws Exception {

        final BaseIndexer indexer = new TrackIndexer( new TestDatabase() );
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

        final BaseIndexer indexer = new TrackIndexer( new TestDatabase() );
        final ResultSet rs = indexer.getFiles();

        assertNotNull( rs );

    }

    public void testGetDirectories() throws Exception {

        final BaseIndexer indexer = new TrackIndexer( new TestDatabase() );
        final ResultSet rs = indexer.getDirectories();

        assertNotNull( rs );

    }

}
