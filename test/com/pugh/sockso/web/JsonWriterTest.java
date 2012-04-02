
package com.pugh.sockso.web;

import com.pugh.sockso.tests.SocksoTestCase;

import java.io.StringWriter;

public class JsonWriterTest extends SocksoTestCase {

    public void testWhiteSpaceIsRemovedFromJson() throws Exception {
        String json = "{  id: 123, name: \"foo \\\"bar\" }";
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter( writer );
        jsonWriter.write( json );
        assertEquals( "{id:123,name:\"foo \\\"bar\"}", writer.toString() );
    }
    
}
