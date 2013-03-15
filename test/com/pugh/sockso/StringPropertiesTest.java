
package com.pugh.sockso;

import com.pugh.sockso.tests.SocksoTestCase;

import org.apache.log4j.Logger;

import java.util.Map;

public class StringPropertiesTest extends SocksoTestCase implements PropertiesListener {

    private static Logger log = Logger.getLogger( StringPropertiesTest.class  );
    private boolean propertiesSavedFlag;

    @Override
    public void propertiesSaved( Properties p ) {
        propertiesSavedFlag = true;
    }

    public void testAddPropertiesListener() {

        Properties p = new StringProperties();
        p.addPropertiesListener( this );

        propertiesSavedFlag = false;
        assertFalse( propertiesSavedFlag );
        p.save();
        assertTrue( propertiesSavedFlag );
        
    }

    public void testGetAndSet() {
        final Properties p = new StringProperties();
        String value = "asdasd";
        String name = "asdsd";
        p.set( name, value );
        assertEquals( p.get(name), value );
    }

    public void testGetAndSetInt() {
        final Properties p = new StringProperties();
        String name = "foo.bar";
        int value = 123213;
        p.set( name, value );
        assertEquals( p.get(name,-1), value );
        // test a default
        p.set( name, "string" );
        assertEquals( p.get(name,value), value );
    }

    public void testGetMatches() {
        final Properties p = new StringProperties();
        p.set( "prop.a", "b" );
        p.set( "prop.b", "c" );
        Map<String,String> h = p.getMatches( "prop" );
        assertEquals( "b", h.get("a") );
        assertEquals( "c", h.get("b") );
    }

    public void testDelete() {

        final Properties p = new StringProperties();
        final String name = "foo.bar";

        p.set( name, "baz" );
        assertTrue( p.exists(name) );
        p.delete( name );
        assertFalse( p.exists(name) );

    }

    public void testExists() {

        final Properties p = new StringProperties();
        final String name = "foo.bar";

        p.set( name, "baz" );
        assertTrue( p.exists(name) );
        p.delete( name );
        assertFalse( p.exists(name) );

    }

    public void testGetUrl() {

        final Properties p = new StringProperties();
    
        assertEquals( "/foo", p.getUrl("foo") );
        assertEquals( "/foo", p.getUrl("/foo") );

        assertEquals( "/file/skins/original/foo", p.getUrl("<skin>/foo") );
        assertEquals( "/file/skins/original/foo", p.getUrl("/<skin>/foo") );

        p.set("www.skin","other");

        assertEquals( "/file/skins/other/foo", p.getUrl("<skin>/foo") );

        p.set("server.basepath","other");

        assertEquals( "/other/file/skins/other/foo", p.getUrl("<skin>/foo") );

        p.set("server.basepath","http://other.com");

        assertEquals( "http://other.com/file/skins/other/foo", p.getUrl("<skin>/foo") );

        assertEquals( "http://test.com/foo", p.getUrl("http://test.com/foo") );
        assertEquals( "https://test.com/foo", p.getUrl("https://test.com/foo") );

    }
    
}
