
package com.pugh.sockso.cache;

import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.Logger;

@Singleton
public class ObjectCache implements Cache {

    private static final Logger log = Logger.getLogger( ObjectCache.class );

    private HashMap<String,CachedObject> data;

    /**
     *  Create a new object cache
     *
     */
    
    public ObjectCache() {

        data = new HashMap<String,CachedObject>();
        
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
        final boolean isCached = isCacheOk( object );

        log.debug( "Cache " +(isCached ? "hit" : "miss")+ " for " +key );

        return isCached;
        
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
    
    public synchronized void write( final String key, final Object value, final int timeout ) {

        log.debug(
            "Write key " +key+
            ( timeout != -1 ? " expires in " +timeout+ " seconds" : "" )
        );

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
        
        final CachedObject object = readRaw( key );

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

    private synchronized CachedObject readRaw( final String key ) {

        return data.get( key );

    }
    
    /**
     *  Returns an enumeration of all the keys in the cache
     * 
     *  @return 
     * 
     */
    
    public Set<String> getKeys() {
        
        return data.keySet();
        
    }
    
    /**
     *  Deletes a key and it's value from the cache
     * 
     *  @param key 
     * 
     */
    
    public void delete( final String key ) {
        
        data.remove( key );
        
    }

}
