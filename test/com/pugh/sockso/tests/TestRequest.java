
package com.pugh.sockso.tests;

import com.pugh.sockso.web.HttpRequest;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.UploadFile;
import java.io.IOException;

import java.io.InputStream;

import java.util.Hashtable;

public class TestRequest extends HttpRequest implements Request {

    private String resource;

    private Hashtable<String,String> args;

    public TestRequest( final String resource ) {
        super( null );
        this.args = new Hashtable<String,String>();
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

    public String[] getPlayParams( boolean skipOne ) {
        return new String[] {};
    }

    public String[] getPlayParams( int skip ) {
        return new String[] {};
    }

    public String[] getPlayParams() {
        return new String[] {};
    }

    public String getArgument( String name ) {
        return hasArgument( name )
            ? args.get( name )
            : "";
    }

    public boolean hasArgument( String name ) {
        return args.containsKey( name );
    }

    public void setArgument( final String name, final String value ) {
        args.put( name, value );
    }

    public UploadFile getFile( String file ) {
        return null;
    }

    public String getPreferredLangCode() {
        return "en";
    }

}
