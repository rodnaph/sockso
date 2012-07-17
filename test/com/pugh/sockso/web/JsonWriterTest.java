
package com.pugh.sockso.web;

import com.pugh.sockso.tests.SocksoTestCase;

import java.io.StringWriter;

public class JsonWriterTest extends SocksoTestCase {

    private StringWriter stringWriter;
    private JsonWriter jsonWriter;

    @Override
    protected void setUp() {
        stringWriter = new StringWriter();
        jsonWriter = new JsonWriter( stringWriter );
    }

    protected String write( String json ) throws Exception {
        jsonWriter.write( json );
        return stringWriter.toString();
    }

    public void testWhiteSpaceIsRemovedFromJson() throws Exception {
        String input = "{  id: 123, name: \"foo \\\"bar\" }";
        assertEquals( "{id:123,name:\"foo \\\"bar\"}", write(input) );
    }

    public void testIssue109() throws Exception {
        String input = "{  \"name\":   \"vinyl 12\\\"\",    \n  \"id\"    :    123 }";
        assertEquals( "{\"name\":\"vinyl 12\\\"\",\"id\":123}", write(input) );
    }
    
}
