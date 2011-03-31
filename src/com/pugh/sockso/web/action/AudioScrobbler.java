
package com.pugh.sockso.web.action;

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

public class AudioScrobbler {

    private Database db;

    /**
     *  Creates a new AudioScrobbler object
     *
     *  @param db
     *
     */

    public AudioScrobbler( final Database db ) {
        
        this.db = db;
        
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
        
        ResultSet rs = null;
        PreparedStatement st = null;
        BufferedReader in = null;

        try {
            
            final String sql = " select name " +
                               " from artists " +
                               " where id = ? ";
            
            st = db.prepare( sql );
            st.setInt( 1, artistId );
            rs = st.executeQuery();
            
            if ( !rs.next() )
                throw new BadRequestException( "unknown artist", 404 );

            final String url = "http://ws.audioscrobbler.com/1.0/artist/" +Utils.URLEncode(rs.getString("name"))+ "/similar.txt";
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
            Utils.close( rs );
            Utils.close( st );
            Utils.close( in );
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
