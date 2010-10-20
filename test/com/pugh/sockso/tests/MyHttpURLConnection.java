
package com.pugh.sockso.tests;

import java.io.InputStream;
import java.io.IOException;

import java.net.HttpURLConnection;

/**
 *  a simple implementation of a HttpURLConnection which will read back some data
 * 
 */

public class MyHttpURLConnection extends HttpURLConnection {
    
    private final String data;
    
    public MyHttpURLConnection( final String data ) {
        super( null );
        this.data = data;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return data != null
            ? TestUtils.getInputStream( data )
            : super.getInputStream();
    }
    
    public boolean usingProxy() { return false; }
    
    public void disconnect() {}
    
    public void connect() {}
    
}
