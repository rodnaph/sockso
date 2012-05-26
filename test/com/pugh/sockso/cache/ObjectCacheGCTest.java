
package com.pugh.sockso.cache;

import com.pugh.sockso.tests.SocksoTestCase;

public class ObjectCacheGCTest extends SocksoTestCase {

    private ObjectCache oc;
    
    private ObjectCacheGC ogc;
    
    @Override
    protected void setUp() throws CacheException {
        oc = new ObjectCache();
        oc.write( "foo", "bar", 2 );
        ogc = new ObjectCacheGC( oc );
    }
    
    public void testCleanCacheDeletesExpiredCacheKeys() throws Exception {
        Thread.sleep( 3000 );
        ogc.cleanCache();
        assertFalse( oc.getKeys().contains("foo") );
    }
    
    public void testCleancacheDoesNotDeleteValidKeys() throws Exception {
        ogc.cleanCache();
        assertTrue( oc.getKeys().contains("foo") );
    }
    
}
