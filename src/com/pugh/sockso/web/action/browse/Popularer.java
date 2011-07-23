
package com.pugh.sockso.web.action.browse;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.templates.web.browse.TPopular;
import com.pugh.sockso.web.action.BaseAction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.io.IOException;

import java.util.Vector;

import org.apache.log4j.Logger;

/**
 *  shows popular track information
 * 
 */

public class Popularer extends BaseAction {

    private static final Logger log = Logger.getLogger( Popularer.class  );
    
    /**
     *  shows information about popular things
     * 
     *  @param req the request object
     *  @param res the response object
     * 
     *  @throws IOException
     *  @throws SQLException
     * 
     */

    public void handleRequest() throws IOException, SQLException {
        
        showPopularTracks( getPopularTracks() );
        
    }

    /**
     *  shows the page displaying populat tracks
     * 
     *  @param tracks
     * 
     *  @throws java.io.IOException
     * 
     */
    
    protected void showPopularTracks( final Vector<Track> tracks ) throws IOException, SQLException {
        
        final TPopular tpl = new TPopular();
        
        tpl.setTracks( tracks );
        
        getResponse().showHtml( tpl );

    }

    /**
     *  returns the most popular (most played) tracks.  limit set in properties
     * 
     *  @return
     * 
     *  @throws java.sql.SQLException
     * 
     */
    
    protected Vector<Track> getPopularTracks() throws SQLException {

        ResultSet rs = null;
        PreparedStatement st = null;
        
        try {

            final Properties p = getProperties();
            final Database db = getDatabase();
            final String sql = Track.getSelectSql() + " , count(l.id) as playCount " +
                        " from tracks t " +
                            " inner join artists ar " +
                            " on ar.id = t.artist_id " +
                            " inner join albums al " +
                            " on al.id = t.album_id " +
                            " left outer join play_log l " +
                            " on l.track_id = t.id " +
                        " group by artistId, artistName, albumId, albumName, albumYear, trackId, " +
                            " trackName, trackPath, trackNo, dateAdded " +
                        " having count(l.id) > 0 " +
                        " order by count(l.id) desc " +
                        " limit ? ";
            st = db.prepare( sql );
            
            st.setInt( 1, (int) p.get(Constants.WWW_BROWSE_POPULAR_TRACK_COUNT,20) );
            
            rs = st.executeQuery();

            final Vector<Track> tracks = new Vector<Track>();
            while ( rs.next() ) {
                final Track track = Track.createFromResultSet( rs );
                track.setPlayCount( rs.getInt("playCount") );
                tracks.addElement( track );
            }
            
            return tracks;

        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
            
    }
    
}
