
package com.pugh.sockso.web.action.browse;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Playlist;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestResponse;
import com.pugh.sockso.web.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlaylistserTest extends SocksoTestCase {
    
    public void testGetSitePlaylists() throws SQLException {
        
        final ResultSet rs = createNiceMock( ResultSet.class );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( false );
        replay( rs );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        expect( st.executeQuery() ).andReturn( rs ).times( 1 );
        st.close();
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );
        
        final Playlistser b = new Playlistser();
        b.setDatabase( db );
        
        final List<Playlist> sitePlaylists = b.getSitePlaylists();
        
        assertNotNull( sitePlaylists );
        assertEquals( 1, sitePlaylists.size() );
        
        verify( db );
        verify( st );
        verify( rs );
        
    }
    
    public void testGetUserPlaylists() throws SQLException {
        
        final ResultSet rs = createNiceMock( ResultSet.class );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( false );
        replay( rs );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        expect( st.executeQuery() ).andReturn( rs ).times( 1 );
        st.close();
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );
        
        final Playlistser b = new Playlistser();
        b.setDatabase( db );
        
        final List<Playlist> userPlaylists = b.getUserPlaylists();
        
        assertNotNull( userPlaylists );
        assertEquals( 2, userPlaylists.size() );
        
        verify( db );
        verify( st );
        verify( rs );
        
    }
    
    public void testGetUserPlaylistsQuery() throws Exception {
        
        final Database db = new TestDatabase();
        final Playlistser b = new Playlistser();
        
        b.setDatabase( db );
        b.getUserPlaylists();
        
    }
    
    public void testGetSitePlaylistsQuery() throws Exception {
        
        final Database db = new TestDatabase();
        final Playlistser b = new Playlistser();
        
        b.setDatabase( db );
        b.getSitePlaylists();
        
    }

    public void testShowPlaylists() throws Exception {

        final TestResponse res = new TestResponse();
        final Playlistser b = new Playlistser();
        final List<Playlist> sitePlaylists = new ArrayList<Playlist>();
        final List<Playlist> userPlaylists = new ArrayList<Playlist>();
        final User user = new User( 12323, "my user" );
        final Playlist sitePlaylist = new Playlist( 1, "site Play list" );
        final Playlist userPlaylist = new Playlist( 1, "USER Play list", 1, user );

        sitePlaylists.add( sitePlaylist );
        userPlaylists.add( userPlaylist );
        
        b.setResponse( res );
        b.showPlaylists( sitePlaylists, userPlaylists );
        
        final String data = res.getOutput();

        assertTrue( data.contains(sitePlaylist.getName()) );
        assertTrue( data.contains(userPlaylist.getName()) );
        assertTrue( data.contains(user.getName()) );

    }

}
