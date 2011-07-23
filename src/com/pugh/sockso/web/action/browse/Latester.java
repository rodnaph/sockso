
package com.pugh.sockso.web.action.browse;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.music.Album;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.templates.web.browse.TLatest;
import com.pugh.sockso.web.action.BaseAction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.io.IOException;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 *  shows the latest tracks/artists added to the collection
 * 
 */

public class Latester extends BaseAction {
    
    private static final Logger log = Logger.getLogger( Latester.class  );
        
    /**
     *  shows the latest music added to the collection
     * 
     *  @param req the request object
     *  @param res the response object
     * 
     *  @throws SQLException
     *  @throws IOException
     * 
     */
    
    public void handleRequest() throws SQLException, IOException {
        
        final Properties p = getProperties();
        final Vector<Track> tracks = getLatestTracks( (int) p.get(Constants.WWW_BROWSE_LATEST_TRACKS_COUNT,20) );
        final Vector<Artist> artists = getLatestArtists();
        final Vector<Album> albums = getLatestAlbums();
        
        showLatest( tracks, artists, albums );
        
    }

    /**
     *  shows the page with the latest tracks and artists that have been
     *  added to the collection
     * 
     *  @param tracks
     *  @param artists
     * 
     *  @throws java.io.IOException
     * 
     */
    
    protected void showLatest( final Vector<Track> tracks, final Vector<Artist> artists, final Vector<Album> albums ) throws IOException, SQLException {

        final TLatest tpl = new TLatest();
        
        tpl.setTracks( tracks );
        tpl.setArtists( artists );
        tpl.setAlbums( albums );
        
        getResponse().showHtml( tpl );
        
    }

    /**
     *  returns the latest artists.  the number returned is set in properties
     * 
     *  @return
     * 
     *  @throws java.sql.SQLException
     * 
     */
    
    protected Vector<Artist> getLatestArtists() throws SQLException {
        
        final ResultSet rs = getLatestMusic( "artist", Constants.WWW_BROWSE_LATEST_ARTISTS_COUNT );
        final Vector<Artist> artists = new Vector<Artist>();

        while ( rs.next() )
            artists.addElement(
                new Artist( rs.getInt("id"), rs.getString("name") )
            );

        Utils.close( rs );
        
        return artists;

    }

    /**
     *  queries the database for the latest music of the specified type.  the property
     *  name is the name of the property that controls how many results are returned.
     * 
     *  @param type
     *  @param totalPropertyName
     * 
     *  @return
     * 
     *  @throws java.sql.SQLException
     * 
     */
    
    private ResultSet getLatestMusic( final String type, final String totalPropertyName ) throws SQLException {
    
        PreparedStatement st = null;
        
        final Properties p = getProperties();
        final Database db = getDatabase();
        final String sql = " select ar.id, ar.name, max(t.date_added) as mostRecent " +
                    " from tracks t " +
                        " inner join " +type+ "s ar " +
                        " on ar.id = t." +type+ "_id " +
                    " group by ar.id, ar.name " +
                    " order by mostRecent desc " +
                    " limit ? ";
        st = db.prepare( sql );
        st.setInt( 1, (int) p.get(totalPropertyName,10) );

        return st.executeQuery();

    }
    
    /**
     *  returns the latest albums that have been added
     * 
     *  @return
     * 
     *  @throws java.sql.SQLException
     * 
     */
    
    protected Vector<Album> getLatestAlbums() throws SQLException {
        
        final ResultSet rs = getLatestMusic( "album", Constants.WWW_BROWSE_LATEST_ALBUMS_COUNT );
        final Vector<Album> albums = new Vector<Album>();

        while ( rs.next() )
            albums.addElement(
                new Album( null, rs.getInt("id"), rs.getString("name"), "" )
            );

        return albums;
        
    }
    
}
