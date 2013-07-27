/*
 * Homer.java
 * 
 * Created on Aug 8, 2007, 9:23:17 PM
 * 
 * Shows the home page.
 */

package com.pugh.sockso.web.action;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.music.Album;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.templates.web.TMain;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.List;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class Homer extends BaseAction {

    private static final Logger log = Logger.getLogger( Homer.class );

    /**
     *  this method shows the home page
     * 
     *  @throws SQLException
     *  @throws IOException
     * 
     */
    
    public void handleRequest() throws SQLException, IOException {
        
        final Properties p = getProperties();
        final int totalRecentTracks = (int) p.get( Constants.WWW_BROWSE_RECENT_TRACKS_COUNT, 10 );
        final int totalTopArtists = (int) p.get( Constants.WWW_BROWSE_TOP_ARTISTS_COUNT, 10 );
        final List<Track> recentlyPlayedTracks = getRecentlyPlayedTracks( totalRecentTracks );
        final List<Artist> topArtists = getTopArtists( totalTopArtists );
        final List<Album> recentlyPlayedAlbums = getRecentlyPlayedAlbums( 5 );

        showMain( recentlyPlayedTracks, topArtists, recentlyPlayedAlbums );
        
    }
    
    /**
     *  shows the main page
     * 
     *  @param recentlyPlayedTracks
     *  @param topArtists
     *  @param recentlyPlayedAlbums
     * 
     *  @throws java.io.IOException
     * 
     */
    
    protected void showMain( final List<Track> recentlyPlayedTracks,
            final List<Artist> topArtists,
            final List<Album> recentlyPlayedAlbums ) throws IOException, SQLException {
        
        final TMain tpl = new TMain();

        tpl.setRecentTracks( recentlyPlayedTracks );
        tpl.setTopArtists( topArtists );
        tpl.setRecentAlbums( recentlyPlayedAlbums );

        getResponse().showHtml( tpl );

    }

    /**
     *  returns the most played artists (limit set by property)
     * 
     *  @return
     * 
     *  @throws java.sql.SQLException
     * 
     */
    
    protected List<Artist> getTopArtists( final int total ) throws SQLException {
        
        PreparedStatement st = null;
        ResultSet rs = null;

        try {

            final Database db = getDatabase();
            final String sql = " select ar.id as id, ar.name as name, count(t.id) as playCount " +
                    " from play_log l " +
                        " inner join tracks t " +
                        " on t.id = l.track_id " +
                        " inner join artists ar " +
                        " on ar.id = t.artist_id " +
                    " group by ar.id, ar.name " +
                    " order by playCount desc " +
                    " limit ? ";
            
            st = db.prepare( sql );
            st.setInt( 1, total );
            rs = st.executeQuery();
            
            final List<Artist> topArtists = new ArrayList<Artist>();
            
            while ( rs.next() )
                topArtists.add( new Artist(
                    rs.getInt("id"), rs.getString("name"), rs.getInt("playCount") )
                );
            
            return topArtists;
            
        }
        
        finally {
            Utils.close( rs );            
            Utils.close( st );            
        }

    }
    
    /**
     *  returns recently played tracks (limit set by property)
     * 
     *  @param total
     * 
     *  @return
     * 
     */
    
    protected List<Track> getRecentlyPlayedTracks( final int total ) throws SQLException {

        PreparedStatement st = null;
        ResultSet rs = null;

        try {

            final Database db = getDatabase();
            final String sql = Track.getSelectSql() +
                    " from ( select l.track_id " +
                            " from play_log l " +
                            " order by l.date_played desc ) r " +
                        " inner join tracks t " +
                        " on r.track_id = t.id " +
                        " inner join genres g " +
                        " on g.id = t.genre_id " +
                        " inner join artists ar " +
                        " on ar.id = t.artist_id " +
                        " inner join albums al " +
                        " on al.id = t.album_id " +
                        " group by artistId, artistName, albumId, albumName, albumYear, " +
                        "trackId, trackName, trackPath, trackNo, genreId, genreName, dateAdded " +
                    " limit ? ";

            st = db.prepare( sql );
            st.setInt( 1, total );
            rs = st.executeQuery();
            
            return Track.createListFromResultSet( rs );

        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
    }
    
    /**
     *  returns a list of the "total" most recent albums to have been played
     * 
     *  @param total
     * 
     *  @return
     * 
     *  @throws SQLException
     * 
     */
    
    protected List<Album> getRecentlyPlayedAlbums( final int total ) throws SQLException {

        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
            
            final Database db = getDatabase();
            final String sql = " select al.id as albumId, al.name as albumName, al.year as albumYear, " +
                        " ar.id as artistId, ar.name as artistName, " +
                        " max(l.date_played) as mostRecent " +
                  " from play_log l " +
                      " inner join tracks t " +
                      " on t.id = l.track_id " +
                      " inner join albums al " +
                      " on al.id = t.album_id " +
                      " inner join artists ar " +
                      " on ar.id = al.artist_id " +
                  " group by albumId, albumName, albumYear, artistId, artistName " +
                  " order by mostRecent desc " +
                  " limit ? ";

            st = db.prepare( sql );
            st.setInt( 1, total );
            rs = st.executeQuery();

            final List<Album> recentAlbums = new ArrayList<Album>();
            while ( rs.next() )
                recentAlbums.add( new Album(
                        rs.getInt("artistId"), rs.getString("artistName"),
                        rs.getInt("albumId"), rs.getString("albumName"),
                        rs.getString("albumYear")
                ));
            
            return recentAlbums;

        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }

    }
    
}
