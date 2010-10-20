
package com.pugh.sockso;

import com.pugh.sockso.tests.SocksoTestCase;

import joptsimple.OptionParser;

public class OptionsTest extends SocksoTestCase {

    public void testGetParser() {
        
        final String[] args = new String[] {};
        final OptionParser parser = Options.getParser();
        
        assertNotNull( parser );
        
    }
    
}
