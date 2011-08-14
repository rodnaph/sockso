
package com.pugh.sockso.web.action;

import com.pugh.sockso.ObjectCache;
import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.web.BadRequestException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

public class AudioScrobbler {

    private final int ONE_HOUR_IN_SECONDS = 60 * 60;
    private final int CACHE_TIMEOUT_IN_SECONDS = ONE_HOUR_IN_SECONDS;

    private final Logger log = Logger.getLogger( AudioScrobbler.class );

    private Database db;

    private ObjectCache cache;

    /**
     *  Creates a new AudioScrobbler object
     *
     *  @param db
     *
     */

    @Inject
    public AudioScrobbler( final Database db, final ObjectCache cache ) {
        
        this.db = db;
        this.cache = cache;

    }

    /**
     *  Fetches related artist info
     *
     *  @param artistId
     *
     *  @return
     *
     *  @throws IOException
     *  @throws SQLException
     *  @throws BadRequestException
     *
     */

    public String[] getSimilarArtists( final int artistId ) throws IOException, SQLException, BadRequestException {

        final String cacheKey = "web.action.AudioScrobbler.similar." +artistId;

        if ( !cache.isCached(cacheKey) ) {
            cache.write(
                cacheKey,
                getSimilarArtists( getArtistName(artistId) ),
                CACHE_TIMEOUT_IN_SECONDS
            );
        }

        return (String[]) cache.read( cacheKey );

    }

    /**
     *  Fetches similar artists by artist name
     *
     *  @param artistName
     *
     *  @return
     *
     *  @throws IOException
     *
     */
    
    protected String[] getSimilarArtists( final String artistName ) throws IOException {

        BufferedReader in = null;

        try {

            log.debug( "Fetching similar artists for: " +artistName );

            final String url = "http://ws.audioscrobbler.com/1.0/artist/" +Utils.URLEncode(artistName)+ "/similar.txt";
            final HttpURLConnection cnn = getHttpURLConnection( url );
            final ArrayList<String> artists = new ArrayList<String>();

            String s = "";

            in = new BufferedReader(new InputStreamReader(cnn.getInputStream()) );

            while ( (s = in.readLine()) != null ) {
                final String[] info = s.split( "," );
                artists.add( info[2] );
            }

            return artists.toArray( new String[] {} );

        }

        finally {
            Utils.close( in );
        }

    }

    /**
     *  Resolves an artist id to name, or throws an exception
     *
     *  @param artistId
     *
     *  @return
     *
     *  @throws SQLException
     *  @throws BadRequestException
     *
     */

    protected String getArtistName( final int artistId ) throws SQLException, BadRequestException {

        ResultSet rs = null;
        PreparedStatement st = null;

        try {

            final String sql = " select name " +
                               " from artists " +
                               " where id = ? ";

            st = db.prepare( sql );
            st.setInt( 1, artistId );
            rs = st.executeQuery();

            if ( !rs.next() )
                throw new BadRequestException( "unknown artist", 404 );

            return rs.getString( "name" );

        }

        finally {
            Utils.close( rs );
            Utils.close( st );
        }

    }

    /**
     *  Returns a connection object for the specified URL (seam for testing)
     *
     *  @param url
     *
     *  @return
     *
     *  @throws IOException
     *
     */

    protected HttpURLConnection getHttpURLConnection( final String url ) throws IOException {

        return (HttpURLConnection) new URL( url ).openConnection();

    }

}
