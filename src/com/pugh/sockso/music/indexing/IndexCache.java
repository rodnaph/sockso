
package com.pugh.sockso.music.indexing;

import java.util.Hashtable;

public class IndexCache {

    private final Hashtable<String,Integer> cache;

    /**
     *  Constructor
     * 
     */

    public IndexCache() {

        cache = new Hashtable<String,Integer>();

    }

    /**
     *  Adds a string to the cache
     * 
     *  @param str
     * 
     */

    public void add( final String str ) {
        
        cache.put( getHash(str), 1 );

    }
    
    /**
     *  Tests if a string exists in the cache
     * 
     *  @param str
     * 
     *  @return
     * 
     */

    public boolean exists( final String str ) {

        return cache.containsKey( getHash(str) );

    }

    /**
     *  Returns a hashed version of the string
     *
     *  @param str
     *
     *  @return
     *
     */

    private String getHash( String str ) {

        // no hashing is fastest for now
        return str;
        
    }

    /**
     *  Clears the cache
     * 
     */

    public void clear() {
        
        cache.clear();

    }

}
