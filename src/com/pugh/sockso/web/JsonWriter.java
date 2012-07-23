
package com.pugh.sockso.web;

import java.io.Writer;
import java.io.IOException;

public class JsonWriter extends Writer {
    
    /**
     * Wrapped writer object
     */
    private final Writer writer;

    /**
     * Indicates if we're currently in a string literal
     */
    private boolean inString;

    /**
     * Last char written to stream
     */
    private int lastChar;

    /**
     *  Create a JSON writer that wraps the specified standard writer
     * 
     *  @param writer 
     * 
     */

    public JsonWriter( final Writer writer ) {
        this.writer = writer;
        this.inString = false;
        this.lastChar = -1;
    }

    /**
     *  Remove whitespace from JSON as we write it
     * 
     *  @param cs
     *  @param x
     *  @param y
     * 
     *  @throws IOException 
     * 
     */
    
    @Override
    public void write( int c ) throws IOException {

        if ( c == '"' && lastChar != '\\' ) {
            inString = !inString;
        }

        if ( inString || !Character.isWhitespace(c) ) {
            writer.write( c );
        }

        lastChar = c;

    }

    /**
     *  Pass on to write(int) to make sure we intercept all char writes
     * 
     *  @param chars
     *  @param offset
     *  @param length
     * 
     *  @throws IOException 
     * 
     */

    public void write( char[] chars, int offset, int length ) throws IOException {

        for ( char c : chars ) {
            write( c );
        }
        
    }

    /**
     *  Proxy on to wrapped writer object
     */

    public void close() throws IOException { this.writer.close(); }
    public void flush() throws IOException { this.writer.flush(); }

}
