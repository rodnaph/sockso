
package com.pugh.sockso.gui.action;

import java.awt.event.ActionEvent;
import junit.framework.TestCase;

public class RequestLogActionTest extends TestCase {

    public static boolean fired;
    
    private RequestLogAction action;
    
    @Override
    protected void setUp() {
        action = new MyRequestLogAction();
    }
    
    public void testListenersCanBeAddedThatReceiveUpdateEvents() {
        fired = false;
        action.addListener(new RequestLogChangeListener() {
            public void requestLogChanged() {
                fired = true;
            }
        });
        action.fireRequestLogChanged();
        assertTrue( fired );
    }
    
}

class MyRequestLogAction extends RequestLogAction {
    public void actionPerformed( ActionEvent evt ) {}
}
