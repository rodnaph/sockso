/*
 * PropertiesListener.java
 * 
 * Created on May 30, 2007, 11:53:56 PM
 * 
 * A listener for property events
 * 
 */

package com.pugh.sockso;

public interface PropertiesListener {
    
    /**
     *  handler for the event fired when properties have been saved
     * 
     *  @param properties the new properties
     * 
     */
    
    public void propertiesSaved( final Properties properties );
    
}
