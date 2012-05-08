
package com.pugh.sockso.cache;

import com.google.inject.Inject;

import java.util.Set;

import org.apache.log4j.Logger;

public class ObjectCacheGC extends Thread {
    
    private static final int GC_INTERVAL_IN_MINUTES = 5;
    
    private static final Logger log = Logger.getLogger( ObjectCacheGC.class );
    
    private ObjectCache objectCache;
    
    @Inject
    public ObjectCacheGC( final ObjectCache objectCache ) {
        
        this.objectCache = objectCache;
        
    }
    
    /**
     *  Starts the thread to periodically check the cache for expired keys
     *  and remove them.
     * 
     */
    
    @Override
    public void run() {
        
        while ( true ) {
            
            try {
                
                Thread.sleep( (1000 * 60) * GC_INTERVAL_IN_MINUTES );
                
                log.debug( "Running cleanup" );
                
                cleanCache();
                
            }

            catch ( final InterruptedException e ) {}
            
        }
        
    }
    
    /**
     *  Clean the object cache
     * 
     */
    
    protected void cleanCache() {
        
        final Set<String> keySet = objectCache.getKeys();
        final String[] keys = keySet.toArray( new String[] {} );
        
        for ( final String key : keys ) {
            
            if ( !objectCache.isCached(key) ) {
                log.debug( "Found stale key, '" +key+ "', deleting..." );
                objectCache.delete( key );
            }
            
        }
        
    }
    
}
