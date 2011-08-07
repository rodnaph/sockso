
package com.pugh.sockso;

import com.google.inject.Singleton;

import java.util.Date;
import java.util.Hashtable;

import org.apache.log4j.Logger;

@Singleton
public class ObjectCache {

    private static final Logger log = Logger.getLogger( ObjectCache.class );

    private Hashtable<String,CachedObject> data;

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
        final boolean isCached = ( object != null && !object.isExpired() );

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
    
    public void write( final String key, final Object value, final int timeout ) {

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
