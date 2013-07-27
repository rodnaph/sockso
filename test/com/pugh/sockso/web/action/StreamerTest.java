
package com.pugh.sockso.web.action;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestRequest;
import com.pugh.sockso.tests.TestResponse;
import com.pugh.sockso.tests.TestUtils;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.Response;
import com.pugh.sockso.web.User;

import java.io.DataInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

import static org.easymock.EasyMock.*;

public class StreamerTest extends SocksoTestCase {

    private TestDatabase db;

    private TestResponse res;

    private Streamer s;

    @Override
    protected void setUp() {
        db = new TestDatabase();
        res = new TestResponse();
        s = new Streamer();
        s.setDatabase( db );
        s.setResponse( res );
        s.setProperties( new StringProperties() );
    }

    public void testLogTrackPlayed() throws SQLException {
        
        final int trackId = 123;

        Track.Builder builder = new Track.Builder();
        builder.artist(null)
                .album(null)
                .genre(null)
                .id(trackId)
                .name("")
                .number(1)
                .path("")
                .dateAdded(null);
        final Track track = builder.build();
        
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
        
        Track.Builder builder = new Track.Builder();
        builder.artist(null)
                .album(null)
                .genre(null)
                .id(trackId)
                .name("")
                .number(1)
                .path("")
                .dateAdded(null);
        final Track track = builder.build();
        
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

        final Artist artist = new Artist( -1, "" );

        assertTrue( s.playMusicStream(ms) );
        
        verify( res );
        
    }

    public void testGettingAValidTrackDoesntThrowAnException() throws SQLException, IOException {
        
        final TestRequest req = new TestRequest( "GET /stream/1 HTTP/1.1" );

        db.fixture( "singleTrack" );
        db.update( " update tracks set path = '" +System.getProperty("user.dir")+ "/test/data/empty.mp3' " );
        
        try {
            s.setRequest( req );
            s.handleRequest();
        }
        catch ( BadRequestException e ) {
            fail();
        }
        
    }
    
    public void testGettingAnInvalidTrackThrowsAnException() throws SQLException, IOException {
        
        final TestRequest req = new TestRequest( "GET /stream/1 HTTP/1.1" );
        boolean gotException = false;

        try {
            s.setRequest( req );
            s.handleRequest();
        }
        catch ( final BadRequestException e ) {
            gotException = true;
        }
        
        assertTrue( gotException );
        
    }
    
    public void testSendTrackHeaders() {
        
        final Response res = createMock( Response.class );
        res.addHeader( matches("Content-Type"), (String) anyObject() );
        res.addHeader( matches("Content-Length"), (String) anyObject() );
        res.addHeader( matches("Content-Disposition"), (String) anyObject() );
        res.sendHeaders();
        replay( res );

        final Artist artist = new Artist( -1, "" );
        
        Track.Builder builder = new Track.Builder();
        builder.artist(artist)
                .album(null)
                .genre(null)
                .id(-1)
                .name("")
                .number(-1)
                .path("")
                .dateAdded(null);
        final Track track = builder.build();

        final Streamer s = new Streamer();
        final String mimeType = "foo/bar";
        
        s.setResponse( res );
        s.sendTrackHeaders( track, mimeType );
        
        verify( res );
        
    }
    
}
