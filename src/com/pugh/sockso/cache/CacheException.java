
package com.pugh.sockso.cache;

/**
 *
 * @author Nathan Perrier
 */
public class CacheException extends Exception {

    public CacheException( String msg, Throwable t ) {
        super( msg, t );
    }
    
    public CacheException( String msg ) {
        super( msg );
    }
    
    public CacheException( Throwable t ){
        super( t );
    }

}
