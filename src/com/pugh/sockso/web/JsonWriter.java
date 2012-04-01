
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
     *  Create a JSON writer that wraps the specified standard writer
     * 
     *  @param writer 
     * 
     */

    public JsonWriter( final Writer writer ) {
        this.writer = writer;
        this.inString = false;
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
    
    public void write( final char[] cs, int x, int y ) throws IOException {

        char[] newChars = new char[ y ];
        int size = 0;
        
        for ( int i=x; i<y; i++ ) {
            char c = cs[ i ];
            if ( c == '"' && (i == x || cs[i-1] != '\\') ) { inString = !inString; }
            if ( inString || !Character.isWhitespace(c) ) {
                newChars[ size++ ] = c;
            }
        }

        writer.write( newChars, 0, size );

    }

    /**
     *  Proxy on to wrapped writer object
     */

    public void close() throws IOException { this.writer.close(); }
    public void flush() throws IOException { this.writer.flush(); }

}
