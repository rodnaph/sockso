
package com.pugh.sockso.web;

import java.io.DataOutput;
import java.io.OutputStream;

/**
 *  allows writing data to a stream implementation that can be returned
 *  later as a string
 * 
 */

public class StringOutputStream extends OutputStream implements DataOutput {
    
    private final StringBuffer buffer;
    
    /**
     *  creates a new StringOutputStream
     * 
     */
    
    public StringOutputStream() {
        
        buffer = new StringBuffer();
        
    }
    
    /**
     *  Adds a byte to the buffer
     * 
     *  @param c
     * 
     */
    
    public void writeByte( final int c ) {
        
        write( c );

    }

    /**
     *  Handle writing an int to the stream
     *
     *  @param c
     *
     */

    public void write( final int c ) {
        
        buffer.append( (char) c );

    }

    /**
     *  Writes data from the byte array to the stream
     *
     *  @param bytes
     *  @param start
     *  @param length
     *
     */
    
    @Override
    public void write( final byte[] bytes, final int start, final int length ) {

        for ( int i=start; i<start+length; i++ ) {
            write( bytes[i] );
        }

    }

    /**
     *  returns the contents of the buffer as a string
     * 
     *  @return
     * 
     */
    
    @Override
    public String toString() {
        
        return buffer.toString();
        
    }
    
    // unimplemented methods, not needed yet...
    
    public void writeUTF( final String str ) { System.out.println("UNSUPPORTED writeUTF()"); }
    public void writeChars( final String str ) { System.out.println("UNSUPPORTED writeChars()"); }
    public void writeBytes( final String str ) { System.out.println("UNSUPPORTED writeBytes()"); }
    public void writeDouble( final double dbl ) { System.out.println("UNSUPPORTED writeDouble()"); }
    public void writeFloat( final float flt ) { System.out.println("UNSUPPORTED writeFloat()"); }
    public void writeLong( final long lng ) { System.out.println("UNSUPPORTED writeLong()"); }
    public void writeInt( final int i ) { System.out.println("UNSUPPORTED writeInt()"); }
    public void writeChar( final int c ) { System.out.println("UNSUPPORTED writeChar()"); }
    public void writeShort( final int s ) { System.out.println("UNSUPPORTED writeShort()"); }
    public void writeBoolean( final boolean bool ) { System.out.println("UNSUPPORTED writeBoolean()"); }
    public void write( final byte[] bytes ) { System.out.println("UNSUPPORTED write(bytes)"); }

}
