
package com.pugh.sockso.web.action;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.tests.TestResponse;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestLocale;
import com.pugh.sockso.tests.TestRequest;
import com.pugh.sockso.tests.TestUtils;
import com.pugh.sockso.web.*;

import java.io.File;
import java.io.IOException;

import java.util.Vector;
import java.util.ArrayList;

public class JsonerTest extends SocksoTestCase {

    private TestDatabase db;
    
    private Jsoner js;

    private Properties p;

    private TestResponse res;

    private TestRequest req;

    @Override
    public void setUp() {
        p = new StringProperties();
        p.set( Constants.WWW_BROWSE_FOLDERS_ENABLED, p.YES );
        db = new TestDatabase();
        res = new TestResponse();
        req = new TestRequest( "GET / HTTP/1.1" );
        js = new Jsoner( null );
        js.setRequest( req );
        js.setResponse( res );
        js.setLocale( new TestLocale() );
        js.setProperties( p );
        js.setDatabase( db );
    }
    
    public void testConvertPath() {
        
        // check forward slashes are preserved
        System.setProperty( "file.separator", "/" );
        String path = "/Users/test/some/path";
        
        assertEquals( path, Jsoner.convertPath(path) );
        
        // check backslashes are converted back
        System.setProperty( "file.separator", "\\" );
        String path2 = "c:/users/test/";
        
        assertEquals( "c:\\users\\test\\", Jsoner.convertPath(path2) );
        
    }
    
    public void testShowTracks() throws IOException {
        
        final Vector<Track> tracks = new Vector<Track>();
        final Jsoner j = new Jsoner( null );
        final Response res = new TestResponse();
        final Track track = TestUtils.getTrack();
        
        tracks.add( track );
        
        j.setResponse( res );
        j.showTracks( tracks );

    }
 
    public void testShowSimilarArtists() throws IOException {
        
        final ArrayList<Artist> artists = new ArrayList<Artist>();
        final Jsoner j = new Jsoner( null );
        final Response res = new TestResponse();
        final Artist artist = TestUtils.getArtist();
        
        artists.add( artist );
        
        j.setResponse( res );
        j.showSimilarArtists( artists );
        
    }
 
    public void testGetOrderedFiles() throws Exception {
        
        final Jsoner j = new Jsoner( null );
        final File[] unordered = new File[] {
            new File( "first.mp3" ),
            new File( "second.mp3" ),
            new File( "abba.mp3" ),
            new File( "1 - a.mp3" ),
            new File( "10 - a.mp3" )
        };
        final File[] ordered = j.getOrderedFiles( unordered );
        
        assertEquals( ordered[0], unordered[3] );
        assertEquals( ordered[1], unordered[4] );
        assertEquals( ordered[2], unordered[2] );
        assertEquals( ordered[3], unordered[0] );
        assertEquals( ordered[4], unordered[1] );

    }

    public void testReturningJsonServerInfoIncludesAllRequiredFields() throws Exception {

        Properties p = new StringProperties();
        p.set( Constants.WWW_TITLE, "THEtitle" );
        p.set( Constants.WWW_TAGLINE, "THEtagline" );

        TestResponse res = new TestResponse();
        Jsoner j = new Jsoner( null );
        j.setProperties( p );
        j.setResponse( res );
        j.serverinfo();

        String data = res.getOutput();

        assertContains( data, "title" );
        assertContains( data, "THEtitle" );

        assertContains( data, "tagline" );
        assertContains( data, "THEtagline" );

        assertContains( data, "version" );
        //assertContains( data, Sockso.VERSION ); // ?

        assertContains( data, "requiresLogin" );
        assertContains( data, "0" );

    }

    public void testServerInfoReturnsRequireLoginAsOneWhenItIsEnabled() throws Exception {

        Properties p = new StringProperties();
        p.set( Constants.WWW_USERS_REQUIRE_LOGIN, Properties.YES );

        TestResponse res = new TestResponse();
        Jsoner j = new Jsoner( null );
        j.setResponse( res );
        j.setProperties( p );
        j.serverinfo();

        String data = res.getOutput();

        assertContains( data, "requiresLogin\": \"1\"" );

    }

    public void testDoubleQuotesAreEscapedInServerInfoStrings() throws Exception {

        Properties p = new StringProperties();
        p.set( Constants.WWW_TITLE, "THE\"title" );
        p.set( Constants.WWW_TAGLINE, "THE\"tagline" );

        TestResponse res = new TestResponse();
        Jsoner j = new Jsoner( null );
        j.setProperties( p );
        j.setResponse( res );
        j.serverinfo();

        String data = res.getOutput();

        assertContains( data, "THE\\\"title" );
        assertContains( data, "THE\\\"tagline" );


    }

    public void testLoginNotRequiredWhenServerInfoRequested() {
        
        Request req = new TestRequest( "GET /json/serverinfo HTTP/1.1" );
        Jsoner j = new Jsoner( null );
        j.setRequest( req );

        assertFalse( j.requiresLogin() );

    }

    protected String getTracksForPath() throws Exception {
        db.fixture( "tracksForPath" );
        req.setArgument( "path", "/music/" );
        js.tracksForPath();
        return res.getOutput();
    }

    public void testGettingTracksForAPathOutputsAllThatMatch() throws Exception {
        String json = getTracksForPath();
        assertContains( json, "'1'" );
        assertContains( json, "'2'" );
    }

    public void testGettingTracksForAPathDoesntReturnTracksThatDontMatch() throws Exception {
        String json = getTracksForPath();
        assertNotContains( json, "'3'" );
    }

    public void testGettingTracksForAPathReturnsTheTracksOrderedByTheFullPath() throws Exception {
        String json = getTracksForPath();
        if ( json.indexOf("'2'") > json.indexOf("'1'") ) {
            fail( "Track 2 should have been ordered before track 1" );
        }
    }

}
