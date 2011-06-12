
package com.pugh.sockso.gui.action;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;

public class RequestLogClearTest extends SocksoTestCase {

    protected static boolean eventFired;
    
    private RequestLogClear action;
    
    private TestDatabase db;

    @Override
    protected void setUp() throws Exception {
        db = new TestDatabase();
        db.fixture( "requestLogs" );
        action = new RequestLogClear( null, db, null );
    }
    
    public void testRequestLogIsCleared() throws Exception {
        assertTableSize( db, "request_log", 3 );
        action.clearRequestLog();
        assertTableSize( db, "request_log", 0 );
    }
    
    public void testChangeEventFiredAfterRequestLogCleared() throws Exception {
        eventFired = false;
        action.addListener(new RequestLogChangeListener() {
            public void requestLogChanged() {
                eventFired = true;
            }
        });
        action.clearRequestLog();
        assertTrue( eventFired );
    }
    
}
