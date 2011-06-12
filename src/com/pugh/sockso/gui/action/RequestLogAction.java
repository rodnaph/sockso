
package com.pugh.sockso.gui.action;

import javax.swing.AbstractAction;

import java.util.ArrayList;

public abstract class RequestLogAction extends AbstractAction {
    
    private final ArrayList<RequestLogChangeListener> listeners = new ArrayList<RequestLogChangeListener>();
    
    /**
     *  Adds a listener for request log change events
     * 
     *  @param listener 
     * 
     */
    
    public void addListener( RequestLogChangeListener listener ) {
        
        listeners.add( listener );
        
    }
    
    /**
     *  Fires a change event to all listeners
     * 
     */
    
    protected void fireRequestLogChanged() {
        
        for ( final RequestLogChangeListener listener : listeners ) {
            listener.requestLogChanged();
        }
        
    }

}
