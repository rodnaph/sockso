
package com.pugh.sockso.gui.action;

/**
 *  Interface for objects that want to listen for changes in the request log
 * 
 */
public interface RequestLogChangeListener {
    
    /**
     *  Signals that the request log has been exported
     * 
     */
    
    public void requestLogChanged();
    
}
