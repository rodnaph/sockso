
package com.pugh.sockso.tests;

import com.pugh.sockso.web.StringOutputStream;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.net.HttpURLConnection;

/**
 *  a simple implementation of a HttpURLConnection which will read back some data
 * 
 */

public class MyHttpURLConnection extends HttpURLConnection {
    
    private final String data;

    private final OutputStream out;
    
    public MyHttpURLConnection( final String data ) {
        super( null );
        this.data = data;
        this.out = new StringOutputStream();
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return data != null
            ? TestUtils.getInputStream( data )
            : super.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() {
        return out;
    }

    public String getOutputData() {
        return out.toString();
    }

    public boolean usingProxy() { return false; }
    
    public void disconnect() {}
    
    public void connect() {}
    
}
