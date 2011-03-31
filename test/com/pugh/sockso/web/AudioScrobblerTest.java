
package com.pugh.sockso.web;

import com.pugh.sockso.ObjectCache;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.tests.MyHttpURLConnection;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.web.action.AudioScrobbler;

import java.net.HttpURLConnection;

public class AudioScrobblerTest extends SocksoTestCase {

    private Database db;

    private MyAudioScrobbler as;

    @Override
    public void setUp() throws Exception {
        db = new TestDatabase();
        as = new MyAudioScrobbler( db );
    }

    public void testSimilarArtistsCanBeFetchedFromArtistId() throws Exception {
        db.update( " insert into artists ( id, name, date_added ) values ( 1, 'Test', '2011-01-01 00:00:00' )" );
        as.setUrlResponse( "1,a9044915-8be3-4c7e-b11f-9e2d2ea0a91e,Megadeth\n" +
                           "0.995878,bdacc37b-8633-4bf8-9dd5-4662ee651aec,Slayer\n" +
                           "0.798057,ca891d65-d9b0-4258-89f7-e6ba29d83767,Iron Maiden" );
        String[] related = as.getSimilarArtists( 1 );
        assertEquals( "Megadeth", related[0] );
        assertEquals( "Slayer", related[1] );
        assertEquals( "Iron Maiden", related[2] );
    }

    public void testExceptionThrownOnInvalidArtistIdSpecifiedWhenGettingSimilarArtists() throws Exception {
        boolean gotException = false;
        try {
            as.getSimilarArtists( 999 );
        }
        catch ( BadRequestException e ) {
            gotException = true;
        }
        if ( !gotException ) {
            fail( "Expected BadRequestException for unknown artist id" );
        }
    }
    
}

class MyAudioScrobbler extends AudioScrobbler {
    
    private String data;

    public MyAudioScrobbler( Database db ) {
        super( db, new ObjectCache() );
    }

    public void setUrlResponse( String data ) {
        this.data = data;
    }

    protected HttpURLConnection getHttpURLConnection( String url ) {
        return new MyHttpURLConnection( data );
    }

}
