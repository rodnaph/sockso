
package com.pugh.sockso.web;

import java.io.DataOutput;

/**
 *  allows writing data to a stream implementation that can be returned
 *  later as a string
 * 
 */

public class StringOutputStream implements DataOutput {
    
    private final StringBuffer buffer;
    
    /**
     *  creates a new StringOutputStream
     * 
     */
    
    public StringOutputStream() {
        
        buffer = new StringBuffer();
        
    }
    
    /**
     *   adds a byte to the buffer
     * 
     *  @param c
     * 
     */
    
    public void writeByte( final int c ) {
        
        buffer.append( (char) c );
        
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
    
    public void writeUTF( final String str ) {}
    public void writeChars( final String str ) {}
    public void writeBytes( final String str ) {}
    public void writeDouble( final double dbl ) {}
    public void writeFloat( final float flt ) {}
    public void writeLong( final long lng ) {}
    public void writeInt( final int i ) {}
    public void writeChar( final int c ) {}
    public void writeShort( final int s ) {}
    public void writeBoolean( final boolean bool ) {}
    public void write( final byte[] bytes, final int i, final int j ) {}
    public void write( final byte[] bytes ) {}
    public void write( final int i ) {}

}
