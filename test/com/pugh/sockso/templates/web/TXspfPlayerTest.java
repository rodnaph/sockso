
package com.pugh.sockso.templates.web;

import com.pugh.sockso.StringProperties;
import com.pugh.sockso.tests.SocksoTestCase;

public class TXspfPlayerTest extends SocksoTestCase {
    
    public void testRendering() {
        TXspfPlayer tpl = new TXspfPlayer();
        tpl.setProperties( new StringProperties() );
        tpl.setPlayArgs( new String[] {} );
        tpl.makeRenderer().asString();
    }
    
}
