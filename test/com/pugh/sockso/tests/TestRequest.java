
package com.pugh.sockso.tests;

import com.pugh.sockso.web.HttpRequest;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.UploadFile;
import java.io.IOException;

import java.io.InputStream;

import java.util.Hashtable;

public class TestRequest extends HttpRequest implements Request {

    private String resource;

    public TestRequest( final String resource ) {
        super( null );
        this.resource = resource;
        try { readStatusLine(resource); }
        catch ( Exception e ) {}
    }

    public void process( InputStream in ) {
    }

    public String getResource() {
        return resource;
    }

    public String getHeader( String name ) {
        return "";
    }

    public String getCookie( String name ) {
        return "";
    }

    public String getHost() {
        return "";
    }

    public void setArgument( final String name, final String value ) {
        arguments.put( name, value );
    }

    public UploadFile getFile( String file ) {
        return null;
    }

    public String getPreferredLangCode() {
        return "en";
    }

}
