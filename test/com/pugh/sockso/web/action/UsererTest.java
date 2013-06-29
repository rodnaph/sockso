/*
 * UsererTest.java
 * 
 * Created on Aug 4, 2007, 10:38:33 AM
 * 
 * Tests the Userer class
 * 
 */

package com.pugh.sockso.web.action;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.Utils;
import com.pugh.sockso.auth.DBAuthenticator;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Album;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.music.Genre;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestLocale;
import com.pugh.sockso.tests.TestRequest;
import com.pugh.sockso.tests.TestResponse;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.HttpResponseCookie;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.Response;
import com.pugh.sockso.web.Submission;
import com.pugh.sockso.web.User;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static junit.framework.Assert.assertTrue;
import static org.easymock.EasyMock.*;

public class UsererTest extends SocksoTestCase {

    private Properties testProperties;
    private Locale testLocale;
    private User testUser;

    private Database db;
    private Properties p;
    private TestRequest req;
    private TestResponse resp;
    private Userer u;
    
    @Override
    public void setUp() {

        testLocale = createNiceMock( Locale.class );
        replay( testLocale );
        
        testProperties = createNiceMock( Properties.class );
        expect( testProperties.get((String)anyObject()) ).andReturn( "" ).anyTimes();
        expect( testProperties.getProperties() ).andReturn( new String[] {} );
        replay( testProperties );
        
        testUser = new User( -1, "foo" );

        db = new TestDatabase();
        p = new StringProperties();
        req = new TestRequest( "" );
        resp = new TestResponse();
        u = new Userer();
        u.addAuthenticator( new DBAuthenticator(db) );
        u.setDatabase( db );
        u.setRequest( req );
        u.setProperties( p );
        u.setResponse( resp );
        u.setLocale( new TestLocale() );

    }
    
    public void testRegisterUser() throws Exception {

        req.setArgument("name", "foo");
        req.setArgument("name", "foo" );
        req.setArgument("pass1", "p1" );
        req.setArgument("pass2", "p1" );
        req.setArgument("email", "ps@ubm.com" );

        u.registerUser();

        User user = User.find( db, 0 );
        assertEquals("foo", user.getName());
        assertEquals("ps@ubm.com", user.getEmail());
    }

    public void testNewUsersAreCreatedAsActiveByDefault() throws Exception {
        req.setArgument( "name", "foobar" );
        req.setArgument( "pass1", "abc" );
        req.setArgument( "pass2", "abc" );
        req.setArgument( "email", "test@foo.com" );
        u.registerUser();
        User user = User.find( db, 0 );
        assertTrue( user.isActive() );
    }

    public void testUsersAreCreatedAsInactiveWhenActivationIsRequired() throws Exception {
        p.set( Constants.WWW_USERS_REQUIRE_ACTIVATION, Properties.YES );
        req.setArgument( "name", "foobar" );
        req.setArgument( "pass1", "abc" );
        req.setArgument( "pass2", "abc" );
        req.setArgument( "email", "test@foo.com" );
        u.registerUser();
        User user = User.find( db, 0 );
        assertFalse( user.isActive() );
    }

    public void testNoSessionCreatedForUserWhenTheyNeedToBeActivated() throws Exception {
        p.set( Constants.WWW_USERS_REQUIRE_ACTIVATION, Properties.YES );
        req.setArgument( "name", "foobar" );
        req.setArgument( "pass1", "abc" );
        req.setArgument( "pass2", "abc" );
        req.setArgument( "email", "test@foo.com" );
        u.registerUser();
        User user = User.find( db, 0 );
        assertEquals( "", user.getSessionCode() );
        assertEquals( -1, user.getSessionId() );
    }

    public void testUserLoginFailsWhenUserIsNotActive() throws Exception {
        boolean gotException = false;
        try {
            User user = new User( -1, "foo", "bar", "doo@dpp.com" );
            user.setActive( false );
            user.save( db );
            u.loginUser( "foo", "bar" );
        }
        catch ( Exception e ) {
            gotException = true;
        }
        finally {
            assertTrue( gotException );
        }
    }

    public void testLogout() throws IOException {
        
        Database db = createMock( Database.class );
        Request req = new TestRequest( "/" );
        Response res = createMock( Response.class );
        Locale locale = createNiceMock( Locale.class );

        Userer u = new Userer();
        u.setRequest( req );
        u.setResponse( res );
        u.setLocale( locale );
        u.setProperties( new StringProperties() );

        res.addCookie( (HttpResponseCookie) anyObject() );
        res.addCookie( (HttpResponseCookie) anyObject() );
        res.redirect( "/" );
        replay( res );
        
        u.logout();
        
        verify( res );
        
    }
 
    public void testRequireLoginNoRedirect() throws IOException {
        
        final Response res = createMock( Response.class );
        replay( res );
        
        final Userer u = new Userer();
        
        u.setUser( new User(1,"foo") );
        u.setResponse( res );

        u.requireLogin();

        verify( res );
        
    }
    
    public void testRequireLoginWithRedirect() throws IOException {
        
        final Response res = createMock( Response.class );
        res.redirect( "/user/login" );
        replay( res );
        
        final Userer u = new Userer();
        
        u.setUser( null );
        u.setResponse( res );
        u.setProperties( new StringProperties() );
        
        u.requireLogin();
        
        verify( res );
        
    }
 
    public void testUpdateUser() throws SQLException {
        
        final User user = new User( 1, "bar" );
        
        final Request req = createMock( Request.class );
        expect( req.getArgument("email") ).andReturn( "foo@bar.com" );
        expect( req.getArgument("pass1") ).andReturn( "secret" );
        replay( req );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        st.setString( 1, "foo@bar.com" );
        st.setString( 2, Utils.md5("secret") );
        st.setInt( 3, user.getId() );
        expect( st.execute() ).andReturn( true );
        st.close();
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );
        
        final Userer u = new Userer();
        u.setDatabase( db );
        u.setUser( user );
        u.setRequest( req );
        u.updateUser();
        
        verify( db );
        verify( st );
        verify( req );
        
    }
 
    public void testShowUserUpdated() throws Exception {
        
        final User user = new User( 1, "foo" );
        final Userer u = new Userer();
        final TestResponse res = new TestResponse();
        
        u.setResponse( res );
        u.showUserUpdated();
                
        final String data = res.getOutput();
        
        assertTrue( data.length() > 0 );
        
    }
    
    public void testShowUserEdit() throws Exception {
        
        final Userer u = new Userer();
        final TestResponse res = new TestResponse();
        
        u.setResponse( res );
        u.showUserEdit();

        final String data = res.getOutput();
        
        assertTrue( data.length() > 0 );
        
    }

    public void testShowUserRegister() throws Exception {
        
        final Userer u = new Userer();
        final TestResponse res = new TestResponse();
        
        u.setResponse( res );
        u.showUserRegister();

        final String data = res.getOutput();
        
        assertTrue( data.length() > 0 );
        
    }
    
    public void testShowUserLogin() throws Exception {
        
        final Userer u = new Userer();
        final TestResponse res = new TestResponse();
        
        u.setResponse( res );
        u.showUserLogin();

        final String data = res.getOutput();
        
        assertTrue( data.length() > 0 );
        
    }
    
    public void testShowUserRegistered() throws Exception {
        
        final Userer u = new Userer();
        final TestResponse res = new TestResponse();

        res.setUser( null );
        
        u.setResponse( res );
        u.showUserRegistered( testUser );

        final String data = res.getOutput();
        
        assertTrue( data.length() > 0 );
        assertTrue( data.contains(testUser.getName()) );
        
    }
    
    public void testGetUpdateSubmission() {
        
        final Request req = createMock( Request.class );
        expect( req.getArgument("email") ).andReturn( "foo@bar.com" );
        expect( req.getArgument("pass1") ).andReturn( "one" );
        expect( req.getArgument("pass2") ).andReturn( "" );
        replay( req );
        
        final Userer u = new Userer();
        u.setRequest( req );
        u.setLocale( testLocale );
        
        final Submission s = u.getUpdateSubmission();
        
        verify( req );
        
    }

    public void testMatchingPasswordsRequiredIfSpecified() {
        final Userer u = new Userer();
        final Request req = createMock( Request.class );
        expect( req.getArgument("email") ).andReturn( "foo@bar.com" );
        expect( req.getArgument("pass1") ).andReturn( "one" );
        expect( req.getArgument("pass2") ).andReturn( "" );
        replay( req );
        u.setRequest( req );
        u.setLocale( testLocale );
        final Submission s = u.getUpdateSubmission();
        boolean gotException = false;
        try { s.validate(); }
        catch ( Exception e ) { gotException = true; }
        assertTrue( gotException );
    }
    
    public void testGetNonScrobbledTracks() throws SQLException {

        final User user = new User( 1, "foo" );

        final ResultSet rs = createNiceMock( ResultSet.class );
        expect( rs.next() ).andReturn( true ).times( 2 );
        expect( rs.next() ).andReturn( false ).times( 1 );
        rs.close();
        replay( rs );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        st.setInt( 1, user.getId() );
        expect( st.executeQuery() ).andReturn( rs );
        st.close();
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st );
        replay( db );
        
        final Userer u = new Userer();

        u.setDatabase( db );
        final List<Track> tracks = u.getNonScrobbledTracks( user );
        
        assertNotNull( tracks );
        assertEquals( 2, tracks.size() );
        
        verify( db );
        verify( st );
        verify( rs );
        
    }

    public void testGetNonScrobbledTracksQuery() throws Exception {
        
        final User user = new User( -1, "foo" );
        final Userer u = new Userer();
        final Database db = new TestDatabase();
        
        u.setDatabase( db );
        u.getNonScrobbledTracks( user );

    }
    
    public void testMarkUsersTracksScrobbled() throws Exception {
        
        final User user = new User( -1, "foo" );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        st.setInt( 1, user.getId() );
        expect( st.executeUpdate() ).andReturn( 1 ).times( 1 );
        st.close();
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );
        
        final Userer u = new Userer();
        
        u.setDatabase( db );
        u.markUsersTracksScrobbled( user );
        
        verify( db );
        verify( st );
        
    }
    
    public void testMarkUsersTracksScrobbledQuery() throws Exception {
        
        final User user = new User( -1, "foo" );
        final Userer u = new Userer();
        final Database db = new TestDatabase();
        
        u.setDatabase( db );
        u.markUsersTracksScrobbled( user );
        
    }
    
    public void testShowScrobbleLog() throws Exception {
        
        final TestResponse res = new TestResponse();
        final Userer u = new Userer();
        final List<Track> tracks = new ArrayList<Track>();
        
        final Date theDate = new Date();
        final Artist artist = new Artist( 1, "myArtist" );
        final Album album = new Album( artist, 2, "myAlbum", "year" );
        final Genre genre = new Genre( 3, "myGenre" );

        Track.Builder builder = new Track.Builder();
        builder.artist(artist)
                .album(album)
                .genre(genre)
                .id(3)
                .name("myTrack")
                .number(4)
                .path("")
                .dateAdded(theDate);
        final Track track = builder.build();
        tracks.add( track );
        
        u.setResponse( res );
        u.showScrobbleLog( tracks );
        
        final String data = res.getOutput();

        // http headers
        assertTrue( data.contains("Content-Disposition") );
        assertTrue( data.contains(".scrobbler.log") );
        
        // file headers
        assertTrue( data.contains("#AUDIOSCROBBLER/1.1") );
        assertTrue( data.contains("#TZ") );
        assertTrue( data.contains("#CLIENT") );
        
        // track info
        assertTrue( data.contains(artist.getName()) );
        assertTrue( data.contains(album.getName()) );
        assertTrue( data.contains(track.getName()) );
        
    }

    public void testUserIsCreatedIfTheyDontExist() throws Exception {
        final Userer u = new Userer();
        final Database db = new TestDatabase();
        u.setDatabase( db );
        ///////
        assertTableSize( db, "users", 0 );
        u.findOrCreateUser( "foo", "bar" );
        assertTableSize( db, "users", 1 );
    }

    public void testExistingUserIsReturnedWhenDoesntExist() throws Exception {
        final Userer u = new Userer();
        final TestDatabase db = new TestDatabase();
        db.fixture( "singleUser" );
        u.setDatabase( db );
        ///////
        assertTableSize( db, "users", 1 );
        assertNotNull( u.findOrCreateUser( "foo", "q" ) );
        assertTableSize( db, "users", 1 );
    }

    public void testBadRequestExceptionThrownWhenInvalidLogin() throws Exception {
        final Userer u = new Userer();
        u.setLocale( createNiceMock(Locale.class) );
        boolean gotException = false;
        try {
            u.loginUser( "invalid", "user" );
        }
        catch ( final BadRequestException e ) {
            gotException = true;
        }
        assertTrue( gotException );
    }
    
}
