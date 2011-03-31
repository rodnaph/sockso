
package com.pugh.sockso.web.action;

import java.util.Date;
import java.util.Hashtable;

public class ObjectCache {

    private Hashtable<String,CachedObject> data;

    /**
     * Internal cache object that can be time expired
     *
     */
    class CachedObject {

        private final Object value;

        private final int expiresAt;

        public CachedObject( final Object value, final int timeout ) {

            this.value = value;
            
            expiresAt = ( timeout != -1 )
                ? getTime() + timeout
                : -1;

        }

        private int getTime() {

            return ( (int) new Date().getTime() );

        }

        public boolean isExpired() {

            return expiresAt != -1 && getTime() >= expiresAt;
            
        }

        public Object getValue() {
            return value;
        }

    }

    /**
     *  Create a new object cache
     *
     */
    
    public ObjectCache() {

        data = new Hashtable<String,CachedObject>();
        
    }

    /**
     *  Indicates if the key is cached
     *
     *  @param key
     *
     *  @return
     *
     */

    public boolean isCached( final String key ) {

        final CachedObject object = readRaw( key );

        return ( object != null && !object.isExpired() );
        
    }

    /**
     *  Writes an object to the cache, will never expire
     *
     *  @param key
     *
     *  @param value
     *
     */

    public void write( final String key, final Object value ) {

        write( key, value, -1 );

    }

    /**
     *  Writes an object to expire in the specified number of seconds
     *
     *  @param key
     *  @param value
     *  @param timeout
     *
     */
    
    public void write( final String key, final Object value, final int timeout ) {

        data.put( key, new CachedObject( value, timeout ) );
        
    }

    /**
     *  Tries to read an object from the cache, returns null on cache miss
     *
     *  @param key
     *
     *  @return
     *
     */
    
    public Object read( final String key ) {
        
        final CachedObject object = data.get( key );

        return isCacheOk( object )
            ? object.getValue()
            : null;
        
    }

    /**
     *  Indicates if an internal cache object is still fresh (might be null)
     *
     *  @param object
     *
     *  @return
     *
     */

    private boolean isCacheOk( final CachedObject object ) {

        return ( object != null && !object.isExpired() );

    }

    /**
     *  Does a raw read on the internal data store
     *
     *  @param key
     *
     *  @return
     *
     */

    private CachedObject readRaw( final String key ) {

        return data.get( key );

    }

}
