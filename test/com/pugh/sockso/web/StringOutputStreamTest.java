
package com.pugh.sockso.web;

import junit.framework.TestCase;

public class StringOutputStreamTest extends TestCase {

    private StringOutputStream out;
    
    @Override
    protected void setUp() throws Exception {
        out = new StringOutputStream();
    }

    public void testWriteByteToStreamRecordsIt() {
        out.writeByte( 65 );
        out.writeByte( 66 );
        out.writeByte( 67 );
        assertEquals( "ABC", out.toString() );
    }

    public void testWritingAnIntToTheStreamRecordsIt() {
        out.write( 65 );
        out.write( 66 );
        out.write( 67 );
        assertEquals( "ABC", out.toString() );
    }

    public void testWritingAByteArrayToTheStreamRecordsIt() {
        out.write( new byte[] { 65, 66, 67 }, 0, 3 );
        assertEquals( "ABC", out.toString() );
    }

}
