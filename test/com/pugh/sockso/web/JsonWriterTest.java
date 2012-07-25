
package com.pugh.sockso.web;

import com.pugh.sockso.music.Album;
import com.pugh.sockso.templates.api.TAlbums;
import com.pugh.sockso.tests.SocksoTestCase;

import java.io.StringWriter;
import java.util.Vector;

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
        TAlbums tpl = new TAlbums();
        Vector<Album> albums = new Vector<Album>();
        albums.add( new Album( 1, "Artist\"", 2, "Album\"", "1980") );
        tpl.setAlbums(albums);
        tpl.makeRenderer().renderTo(jsonWriter);

        String expected = "[{\"id\":2,\"name\":\"Album\\\"\",\"artist\":{\"id\":1,\"name\":\"Artist\\\"\"}}]";
        String actual = stringWriter.toString();

        assertEquals( expected, actual );
    }
    
}
