
package com.pugh.sockso.web.action;

import com.pugh.sockso.Properties;
import com.pugh.sockso.Constants;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.web.User;
import com.pugh.sockso.web.Response;
import com.pugh.sockso.tests.TestUtils;
import com.pugh.sockso.tests.SocksoTestCase;

import com.pugh.sockso.web.BadRequestException;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.sql.ResultSet;

import java.io.IOException;
import java.io.DataInputStream;

import java.util.Date;

import static org.easymock.EasyMock.*;

public class StreamerTest extends SocksoTestCase {

    public void testLogTrackPlayed() throws SQLException {
        
        final int trackId = 123;
        final Track track = new Track( null, null, trackId, "", "", 1, new Date() );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        st.setInt( 1, trackId );
        st.setNull( 2, Types.INTEGER );
        expect( st.execute() ).andReturn( true );
        st.close();
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );
        
        final Streamer s = new Streamer();
        s.setDatabase( db );
        
        s.logTrackPlayed( track );
        
        verify( db );
        verify( st );
        
    }
    
    public void testLogTrackPlayedWithUser() throws SQLException {
        
        final User user = new User( 1, "foo" );
        
        final int trackId = 123;
        final Track track = new Track( null, null, trackId, "", "", 1, new Date() );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        st.setInt( 1, trackId );
        st.setInt( 2, user.getId() );
        expect( st.execute() ).andReturn( true );
        st.close();
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );
        
        final Streamer s = new Streamer();
        s.setDatabase( db );
        s.setUser( user );
        
        s.logTrackPlayed( track );
        
        verify( db );
        verify( st );
        
    }
    
    public void testRequiresLoginFalse() {
        
        final Properties p = createMock( Properties.class );
        expect( p.get(Constants.STREAM_REQUIRE_LOGIN) ).andReturn( "" ).times( 1 );
        replay( p );
        
        final Streamer s = new Streamer();
        s.setProperties( p );
        
        assertFalse( s.requiresLogin() );
        
    }

    public void testRequiresLoginTrue() {
        
        final Properties p = createMock( Properties.class );
        expect( p.get(Constants.STREAM_REQUIRE_LOGIN) ).andReturn( p.YES ).times( 1 );
        replay( p );
        
        final Streamer s = new Streamer();
        s.setProperties( p );
        
        assertTrue( s.requiresLogin() );
        
    }

    public void testPlayMusicStream() throws IOException {
        
        final Response res = createMock( Response.class );
        expect( res.getOutputStream() ).andReturn( null ).times( 1 );
        replay( res );
        
        final MusicStream ms = new MusicStream( new DataInputStream(TestUtils.getInputStream("QWE")), "" );
        
        final Streamer s = new Streamer();
        s.setResponse( res );

        assertTrue( s.playMusicStream(ms) );
        
        verify( res );
        
    }

    public void testGetTrack() throws SQLException, BadRequestException {
        
        final int trackId = 123;
        
        final ResultSet rs = createNiceMock( ResultSet.class );
        expect( rs.next() ).andReturn( true ).times( 1 );
        rs.close();
        replay( rs );

        final PreparedStatement st = createMock( PreparedStatement.class );
        st.setInt( 1, trackId );
        expect( st.executeQuery() ).andReturn( rs ).times( 1 );
        st.close();
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );
        
        final Streamer s = new Streamer();
        s.setDatabase( db );
        final Track t = s.getTrack( trackId );
        
        verify( db );
        verify( st );
        verify( rs );
        
    }
    
    public void testGetTrackNotFound() throws SQLException, BadRequestException {
        
        final int trackId = 123;
        
        final ResultSet rs = createMock( ResultSet.class );
        expect( rs.next() ).andReturn( false ).times( 1 );
        rs.close();
        replay( rs );

        final PreparedStatement st = createMock( PreparedStatement.class );
        st.setInt( 1, trackId );
        expect( st.executeQuery() ).andReturn( rs ).times( 1 );
        st.close();
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );
        
        final Streamer s = new Streamer();
        s.setDatabase( db );
        
        boolean gotException = false;
        
        try {
            s.getTrack( trackId );
        }
        catch ( final BadRequestException e ) {
            gotException = true;
        }
        
        assertTrue( gotException );
        
        verify( db );
        verify( st );
        verify( rs );
        
    }
    
    public void testSendTrackHeaders() {
        
        final Response res = createMock( Response.class );
        res.addHeader( matches("Content-Type"), (String) anyObject() );
        res.addHeader( matches("Content-Length"), (String) anyObject() );
        res.addHeader( matches("Content-Disposition"), (String) anyObject() );
        res.sendHeaders();
        replay( res );
        
        final Streamer s = new Streamer();
        final Artist artist = new Artist( -1, "" );
        final Track track = new Track( artist, null, -1, "", "", -1, null );
        final String mimeType = "foo/bar";
        
        s.setResponse( res );
        s.sendTrackHeaders( track, mimeType );
        
        verify( res );
        
    }
    
}
