
package com.pugh.sockso.cache;

import org.apache.log4j.Logger;

/**
 *  Timed cached implements simple cache with optional timeout for keys.  At
 *  the moment this is implemented through extension, but might be a good idea
 *  at some point to refactor to use composition instead.
 * 
 */
abstract public class TimedCache implements Cache {
    
    private static final Logger log = Logger.getLogger( TimedCache.class );

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

        log.debug( "TimedCache " +(isCached ? "hit" : "miss")+ " for " +key );

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

        writeRaw( key, new CachedObject( value, timeout ) );
        
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
     *  Does a raw read on the internal data store, returns null on cache miss
     *
     *  @param key
     *
     *  @return
     *
     */

    abstract protected CachedObject readRaw( final String key );

    /**
     *  Performs a raw write for a cached object
     * 
     *  @param key
     *  @param object 
     * 
     */

    abstract protected void writeRaw( final String key, final CachedObject object );
    
}
