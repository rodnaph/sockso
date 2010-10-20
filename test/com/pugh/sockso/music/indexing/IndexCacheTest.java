
package com.pugh.sockso.music.indexing;

import com.pugh.sockso.Utils;
import com.pugh.sockso.tests.SocksoTestCase;

public class IndexCacheTest extends SocksoTestCase {

    public void testAddExistsAndClear() {

        final IndexCache cache = new IndexCache();

        assertFalse( cache.exists("foo") );
        cache.add( "bar" );
        assertFalse( cache.exists("foo") );
        cache.add( "foo" );
        assertTrue( cache.exists("foo") );
        cache.clear();
        assertFalse( cache.exists("foo") );
        assertFalse( cache.exists("bar") );

    }

    public void testPerformance() {
        
        final IndexCache cache = new IndexCache();
        final long start = System.currentTimeMillis();
        final long target = 2500;
        final int loop = 50000;
        
        for ( int i=0; i<loop; i++ ) {
            cache.add( Utils.getRandomString(130) );
        }

        System.out.println( "Memory usage: " + getMemoryUsage() );

        for ( int i=0; i<loop; i++ ) {
            cache.exists( Utils.getRandomString(30) );
        }

        final long total = System.currentTimeMillis() - start;

        System.out.println( "Target: " +target+ ", Actual: " +total );

        assertTrue( total < target );

    }

    private long getMemoryUsage() {

        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

    }

}
