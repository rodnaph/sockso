
package com.pugh.sockso.tests;

import com.pugh.sockso.web.HttpRequest;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.UploadFile;

import java.io.InputStream;

public class TestRequest extends HttpRequest implements Request {

    private String resource;

    public TestRequest( final String resource ) {
        super( null );
        this.resource = resource;
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

    public String getUrlParam( int index ) {
        return "";
    }

    public int getParamCount() {
        return 0;
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
        return "";
    }

    public boolean hasArgument( String name ) {
        return false;
    }

    public UploadFile getFile( String file ) {
        return null;
    }

    public String getPreferredLangCode() {
        return "en";
    }

}
