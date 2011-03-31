
package com.pugh.sockso;

import java.util.Date;

/**
 *  Cache object that can be time expired
 *
 */

class CachedObject {

    private final Object value;
    
    private final int expiresAt;

    /**
     *  Create a new cached object, that optionally expires after the given
     *  number of seconds.
     *
     *  @param value
     *  @param timeout
     *
     */

    public CachedObject(final Object value, final int timeout ) {

        this.value = value;

        expiresAt = ( timeout != -1 )
            ? getTime() + timeout
            : -1;

    }

    /**
     *  Indicates if this object has expired since it was created
     *
     *  @return
     *
     */
    
    public boolean isExpired() {

        return expiresAt != -1 && getTime() >= expiresAt;

    }

    /**
     *  Returns the value of this object
     *
     *  @return
     *
     */

    public Object getValue() {

        return value;

    }

    /**
     *  Returns the current unix timestamp in seconds
     *
     *  @return
     *
     */

    private int getTime() {

        return (int) (new Date().getTime() / 1000);

    }

}
