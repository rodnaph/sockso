
package com.pugh.sockso.web;

import junit.framework.TestCase;

public class StringOutputStreamTest extends TestCase {
    
    public StringOutputStreamTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testConstructor() {
        
        final StringOutputStream out = new StringOutputStream();
        
        assertNotNull( out );
        
    }
    
    public void testWriteByte() {
        
        final StringOutputStream out = new StringOutputStream();
        final int i = 91;
        
        out.writeByte( 91 );
        
    }

    public void testToString() {
        
        final StringOutputStream out = new StringOutputStream();

        out.writeByte( 65 );
        out.writeByte( 66 );
        out.writeByte( 67 );

        assertEquals( "ABC", out.toString() );
        
    }
    
}
