
package com.pugh.sockso.cache;

import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 *  The object cache implements an in memory data store
 * 
 */
@Singleton
public class ObjectCache extends TimedCache {

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
     *  Does a raw read on the internal data store
     *
     *  @param key
     *
     *  @return
     *
     */

    protected synchronized CachedObject readRaw( final String key ) {

        return data.get( key );

    }

    /**
     *  Puts a cached object in the data cache
     * 
     *  @param key
     *  @param object 
     * 
     */

    protected void writeRaw( final String key, final CachedObject object ) {

        data.put( key, object );

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
