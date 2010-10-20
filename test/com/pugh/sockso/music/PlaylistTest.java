/*
 * PlaylistTest.java
 * 
 * Created on Aug 9, 2007, 8:44:41 PM
 * 
 * Tests the Playlist class
 * 
 */

package com.pugh.sockso.music;

import com.pugh.sockso.web.User;
import com.pugh.sockso.tests.SocksoTestCase;

public class PlaylistTest extends SocksoTestCase {

    public void testConstructor() {
        assertNotNull( new Playlist(1,"foo") );
        assertNotNull( new Playlist(1,"foo",1) );
        assertNotNull( new Playlist(1,"foo",1,null) );
    }

    public void testGetUser() {
        final User user = new User( 1, "foo" );
        final Playlist p = new Playlist( 1, "foo", 1, user );
        assertSame( user, p.getUser() );
    }
    
    public void testGetTrackCount() {
        final Playlist p = new Playlist( 1, "foo", 99 );
        assertEquals( 99, p.getTrackCount() );
    }

    public void testGetSelectTracksSql() {
        
        final int playlistId = 123;
        final String orderBySql = " where myfield = 1 ";
        final String sql = Playlist.getSelectTracksSql( playlistId, orderBySql );
        
        assertTrue( sql.matches(".*"+playlistId+".*") );
        assertTrue( sql.matches(".*"+orderBySql+".*") );
        
    }
    
}
