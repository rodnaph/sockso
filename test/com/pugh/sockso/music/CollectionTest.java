
package com.pugh.sockso.music;

import com.pugh.sockso.tests.SocksoTestCase;

public class CollectionTest extends SocksoTestCase {
    
    public void testConstructors() {
        
        final int id = 123;
        final String path = "/home/me/music";
        
        assertNotNull( new Collection() );
        assertNotNull( new Collection(id,path) );
        
    }

    public void testGetters() {

        final int id = 123;
        final String path = "/home/me/music";
        final Collection col =  new Collection(id,path);

        assertEquals( id, col.getId() );
        assertEquals( path, col.getPath() );
        
    }
    
}
