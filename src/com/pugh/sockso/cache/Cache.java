
package com.pugh.sockso.cache;

import java.io.IOException;

public interface Cache<T> {

    /**
     *  checks if the key exists in the cache
     * 
     *  @param key unique key
     * 
     *  @return true if the object is cached, false otherwise
     * 
     */
    public boolean isCached( final String key ) throws CacheException;

    /**
     *  Adds an object to the cache with no timeout (ie. forever)
     * 
     *  @param key The objects unique key
     *  @param object The object to cache
     * 
     *  @throws IOException
     * 
     */
    public void write( final String key, final T object ) throws CacheException;

    /**
     *  Adds an object to the cache
     * 
     *  @param key The objects unique key
     *  @param object The object to cache
     *  @param timeout Timeout in seconds
     * 
     *  @throws IOException
     * 
     */
    public void write( final String key, final T object, final int timeout ) throws CacheException;

    /**
     *  Retrieves an object from the cache
     * 
     *  @param key 
     * 
     *  @return cached object, else null if not found
     * 
     *  @throws IOException
     * 
     */
    public T read( final String key ) throws CacheException;

}
