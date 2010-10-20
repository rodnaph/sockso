
package com.pugh.sockso.web;

import com.pugh.sockso.Utils;
import com.pugh.sockso.tests.TestUtils;

import java.io.IOException;
import java.io.DataInputStream;

import junit.framework.TestCase;

public class InputBufferTest extends TestCase {
    
    public InputBufferTest(String testName) {
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
        final InputBuffer ib = new InputBuffer( getData("") );
        assertNotNull( ib );
    }

    private DataInputStream getData( final String data ) {
        return new DataInputStream(
            TestUtils.getInputStream( data )
        );
    }

    public void testReadLine() throws IOException {
        
        final InputBuffer cb = new InputBuffer( getData("foo\r\n"));
        final String nextLine = cb.readLine();

        assertEquals( "foo", nextLine );
        
    }

    public void testReadLineEmptyBuffer() throws IOException {
        
        final InputBuffer cb = new InputBuffer( getData(""));
        final String nextLine = cb.readLine();

        assertEquals( "", nextLine );
        
    }

    public void testReadLineJustNewLine() throws IOException {
        
        final InputBuffer cb = new InputBuffer( getData("foo\n") );
        final String nextLine = cb.readLine();

        assertEquals( "foo", nextLine );
        
    }

    public void testReadLineEndOfBuffer() throws IOException {
        
        final InputBuffer cb = new InputBuffer( getData("foo") );
        final String nextLine = cb.readLine();

        assertEquals( "foo", nextLine );
        
    }

    public void testReadLineTwoLines() throws IOException {
        
        final InputBuffer cb = new InputBuffer( getData("foo\r\nbar\n") );
        final String firstLine = cb.readLine();
        final String secondLine = cb.readLine();

        assertEquals( "foo", firstLine );
        assertEquals( "bar", secondLine );
        
    }

    public void testReadString() throws IOException {

        final InputBuffer cb = new InputBuffer( getData("foo\r\nbar\n") );

        cb.readLine();
        
        assertEquals( "bar\n", cb.readString() );

    }

    public void testReadStringWithLength() throws IOException {
        
        final InputBuffer cb = new InputBuffer( getData("foob") );
        final String data = cb.readString( 3 );
        
        assertEquals( "foo", data );
        
    }

    public void testReadStringAllWithLength() throws IOException {
        
        final InputBuffer cb = new InputBuffer( getData("foob") );
        final String data = cb.readString( -1 );
        
        assertEquals( "foob", data );
        
    }

    public void testSkip() throws IOException {

        final InputBuffer cb = new InputBuffer( getData("foo") );

        cb.skip( 2 );

        assertEquals( "o", cb.readLine() );
        
    }

    public void testSkipWithPutBack() throws IOException {

        final InputBuffer cb = new InputBuffer( getData("foo") );

        cb.putBack( 'b' );
        cb.putBack( 'a' );
        cb.skip( 2 );

        assertEquals( "foo", cb.readLine() );
        
    }

    public void testReadDirectly() throws IOException {

        final InputBuffer ib = new InputBuffer( getData("f") );
        final int i = 123;
        
        ib.putBack( i );
        
        assertEquals( 'f', (char) ib.readDirectly() );

    }
    
    public void testPutBack() throws IOException {
        
        final InputBuffer ib = new InputBuffer( getData("f") );
        final int i = 123, j = 456;
        
        ib.putBack( i );
        ib.putBack( j );
        
        assertEquals( i, ib.read() );
        assertEquals( j, ib.read() );
        assertEquals( 'f', (char) ib.read() );
        
    }
 
    // NB. PERFORMANCE TEST

    public void testReadLotsOfData() throws IOException {

        final int bytes = 1024 * 1024 * 10; // 10Mb
        final long start = System.currentTimeMillis();
        final InputBuffer ib = new InputBuffer( getData(Utils.getRandomString(bytes)) );

        while ( true ) {
            if ( ib.read() == -1 ) {
                break;
            }
        }
        
        final long target = 4000;
        final long total = System.currentTimeMillis() - start;

        System.out.println( "Target: " +target+ ", Total: " +total );

        assertTrue( total < target );

    }

}
