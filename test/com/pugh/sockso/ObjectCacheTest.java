
package com.pugh.sockso;

import com.pugh.sockso.tests.SocksoTestCase;

public class ObjectCacheTest extends SocksoTestCase {

    private ObjectCache cache;

    private String[] data = { "1", "2" };

    private String key = "foo";

    @Override
    public void setUp() {
        cache = new ObjectCache();
    }

    public void testObjectsAreNotCachedInitially() {
        assertFalse( cache.isCached(key) );
    }

    public void testObjectIsCachedWhenSet() {
        cache.write( key, data );
        assertTrue( cache.isCached(key) );
    }

    public void testObjectIsReturnedFromCacheWhenItsSet() {
        cache.write( key, data );
        assertSame( data, cache.read(key) );
    }

    public void testReadReturnsNullWhenTheObjectIsNotCached() {
        assertNull( cache.read(key) );
    }

    public void testDataCanBeWrittenToExpireInACertainNumberOfSeconds() throws Exception {
        cache.write( key, data, 1 );
        assertTrue( cache.isCached(key) );
        Thread.sleep( 2000 );
        assertFalse( cache.isCached(key) );
    }
    
    public void testCacheIsEmptyByDefault() {
        assertEquals( 0, cache.getKeys().size() );
    }
    
    public void testAllCacheKeysCanBeFetched() {
        cache.write( "foo", "1" );
        assertEquals( 1, cache.getKeys().size() );
        assertEquals( "foo", cache.getKeys().toArray()[0] );
    }
    
    public void testCacheValueCanBeDeletedByKey() {
        cache.write( key, data );
        cache.delete( key );
        assertFalse( cache.isCached(key) );
    }
    
}
