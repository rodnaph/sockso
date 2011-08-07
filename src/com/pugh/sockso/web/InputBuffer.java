
package com.pugh.sockso.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;

/**
 *  a buffer over the input that provides some convenience methods
 *  for accessing it's data
 * 
 */

public class InputBuffer {
    
    private static final int BUFFER_MAX_SIZE = 1024;
    private static final int DEFAULT_END_OF_STREAM_WAIT = 100;
    
    private final DataInputStream in;
    private final int endOfStreamWait;

    private int[] queue;
    private int head, tail, read;

    /**
     *  Creates a new InputBuffer
     * 
     *  @param in
     * 
     */

    public InputBuffer( final InputStream in ) {

        this( in, DEFAULT_END_OF_STREAM_WAIT );
        
    }

    /**
     *  creates a new InputBuffer that pauses at the end of the stream for the
     *  specified number of millis before giving up.
     * 
     *  @param in
     * 
     */
    
    public InputBuffer( final InputStream in, int endOfStreamWait ) {
        
        this.in = new DataInputStream( in );
        this.endOfStreamWait = endOfStreamWait;
        
        queue = new int[ BUFFER_MAX_SIZE ];
        head = 0;
        tail = 0;
        read = 0;

    }

    /**
     *  reads an int from the input stream, returns -1 if no more data
     * 
     *  @return
     * 
     *  @throws java.io.IOException
     * 
     */
    
    public int read() throws IOException {

        return head != tail
               ? queue[ head++ ]
               : readDirectly();

    }

    /**
     *  reads from the buffer directly bypassing the internal stack
     * 
     *  @return
     * 
     *  @throws java.io.IOException
     * 
     */

    public int readDirectly() throws IOException {

        // if we've read some data, but it looks like we may
        // be at the end of the stream, wait a little to check...
        if ( read > 0 && in.available() == 0 ) {
            try { Thread.sleep( endOfStreamWait ); }
            catch ( final InterruptedException e ) {}
        }

        return ( read++ > 0 && in.available() == 0 )
           ? -1
           : in.read();

    }

    /**
     *  puts an int back onto the input buffer, this will then be re-read by
     *  the read() method.
     * 
     *  @param i
     * 
     */
    
    public void putBack( final int i ) {
        
        if ( tail >= queue.length ) {
            
            int[] newQueue = new int[ queue.length + BUFFER_MAX_SIZE ];
            
            System.arraycopy( queue, head, newQueue, 0, tail - head );
            
            queue = newQueue;
            tail -= head;
            head = 0;

        }

        queue[ tail++ ] = i;
        
    }
    
    /**
     *  tries to fetch a "line" from the buffer, where a line is a sequence of
     *  characters terminated by either "\n", "\r\n", or the end of the buffer
     * 
     *  @param stream
     * 
     *  @return
     * 
     */

    public String readLine() throws IOException {

        final StringBuffer sb = new StringBuffer();
        
        char l = '\0';

        while ( true ) {

            final int i = read();

            // end of stream?
            if ( i == -1 ) {
                return sb.toString();
            }

            final char c = (char) i;

            // reached end of line?
            if ( c == '\n' ) {

                final String s = sb.toString();

                // if the last but one character is a \r then we've got
                // a proper HTTP EOL, goodness.
                return s.substring( 0, s.length() - (l=='\r'?1:0) );

            }

            sb.append( c );

            l = c;

        }
   
    }

    /**
     *  returns whatever is left to be read from the buffer as a String
     * 
     *  @return
     * 
     */
    
    public String readString() throws IOException {
        
        return readString( -1 );
        
    }

    /**
     *  tries to return the specified amount of data from the buffer, but will
     *  return less if it hits the end.  -1 will mean all rest of the buffer
     *  is returned.
     * 
     *  @param length
     * 
     *  @return
     * 
     */
    
    public String readString( final int length ) throws IOException {

        final StringBuffer sb = new StringBuffer();

        int lengthRead = 0;
        int i = read();

        while ( i != -1 && (length == -1 || lengthRead++ < length) ) {
            sb.append( (char) i );
            i = read();
        }

        return sb.toString();

    }

    /**
     *  can be used to skip come int in the buffer
     * 
     *  @param charCount
     * 
     */
    
    public void skip( final int amount ) throws IOException {
        
        int removeAmount = amount;
        
        if ( head != tail ) {
            while ( true ) {
                if ( removeAmount > 0 ) {
                    removeAmount--;
                    if ( head != tail ) {
                        head++;
                    }
                    else break;
                } 
                else break;
            }
        }

        in.skip( removeAmount );

    }
    
}
