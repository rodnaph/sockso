
package com.pugh.sockso;

import com.pugh.sockso.tests.SocksoTestCase;

public class ObjectCacheGCTest extends SocksoTestCase {

    public void testCleanCacheDeletesExpiredCacheKeys() throws Exception {
        ObjectCache oc = new ObjectCache();
        oc.write( "foo", "bar", 2 );
        ObjectCacheGC ogc = new ObjectCacheGC( oc );
        Thread.sleep( 3000 );
        ogc.cleanCache();
        assertFalse( oc.getKeys().contains("foo") );
    }
    
    public void testCleancacheDoesNotDeleteValidKeys() throws Exception {
        ObjectCache oc = new ObjectCache();
        oc.write( "foo", "bar", 2 );
        ObjectCacheGC ogc = new ObjectCacheGC( oc );
        ogc.cleanCache();
        assertTrue( oc.getKeys().contains("foo") );
    }
    
}
