
package com.pugh.sockso.web.action;

import com.pugh.sockso.StringProperties;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.templates.web.TXspfPlayer;
import com.pugh.sockso.templates.web.TFlexPlayer;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestResponse;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.action.playlist.XspfPlayer;

import java.util.Vector;

import javax.swing.JSplitPane;

import static org.easymock.EasyMock.*;

public class PlayerTest extends SocksoTestCase {

    public void testGetXspfPlayer() {

        final Request req = createMock( Request.class );
        expect( req.getArgument("player") ).andReturn( "" ).times( 1 );
        replay( req );
        
        final Player player = new Player();
        player.setRequest( req );
        final XspfPlayer p1 = player.getXspfPlayer();
        
        assertEquals( TXspfPlayer.class, p1.getClass() );
        
        verify( req );
        
    }

    public void testGetFlexPlayer() {
        
        final Request req = createMock( Request.class );
        expect( req.getArgument("player") ).andReturn( "flexPlayer" ).times( 1 );
        replay( req );
        
        final Player player = new Player();
        player.setRequest( req );
        final XspfPlayer p1 = player.getXspfPlayer();
        
        assertEquals( TFlexPlayer.class, p1.getClass() );
        
        verify( req );
        
    }
    
    public void testShowXspfPlayer() throws Exception {
        
        final TestResponse res = new TestResponse();
        final Player player = new Player();
        final String extraArgs = "foo=bar";
        final String[] playArgs = new String[] {};
        final TXspfPlayer tpl = new TXspfPlayer();
        
        player.setProperties( new StringProperties() );
        player.setResponse( res );
        player.showXspfPlayer( tpl, extraArgs, playArgs );
        
        final String data = res.getOutput();
        
        assertTrue( data.length() > 0 );
        
    }
    
    public void testShowXspfPlayerFlex() throws Exception {
        
        final TestResponse res = new TestResponse();
        final Player player = new Player();
        final String extraArgs = "foo=bar";
        final String[] playArgs = new String[] {};
        final TFlexPlayer tpl = new TFlexPlayer();
        
        player.setProperties( new StringProperties() );
        player.setResponse( res );
        player.showXspfPlayer( tpl, extraArgs, playArgs );
        
        final String data = res.getOutput();
        
        assertTrue( data.length() > 0 );
        
    }

    public void testShowHtml5Player() throws Exception {
        
        final TestResponse res = new TestResponse();
        final Player player = new Player();
        final Vector<Track> tracks = new Vector<Track>();
        
        player.setProperties( new StringProperties() );
        player.setResponse( res );
        player.showHtml5Player( tracks, false );
        
        final String data = res.getOutput();
        
        assertTrue( data.length() > 0 );
        
    }

}

