
package com.pugh.sockso.web.action.browse;

import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Playlist;
import com.pugh.sockso.templates.web.browse.TPlaylists;
import com.pugh.sockso.web.User;
import com.pugh.sockso.web.action.BaseAction;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *  shows the list of site and user playlists
 * 
 */

public class Playlistser extends BaseAction {

    /**
     *  shows the playlists
     * 
     *  @param req the request object
     *  @param res the response object
     * 
     *  @throws IOException
     *  @throws SQLException
     * 
     */
    
    @Override
    public void handleRequest() throws IOException, SQLException {
        
        final List<Playlist> sitePlaylists = getSitePlaylists();
        final List<Playlist> userPlaylists = getUserPlaylists();
        
        showPlaylists( sitePlaylists, userPlaylists );
           
    }

    /**
     *  shows the page listing site and user playlists
     * 
     *  @param sitePlaylists
     *  @param userPlaylists
     * 
     *  @throws java.io.IOException
     * 
     */
    
    protected void showPlaylists( final List<Playlist> sitePlaylists, final List<Playlist> userPlaylists ) throws IOException, SQLException {

        final TPlaylists tpl = new TPlaylists();

        tpl.setSitePlaylists( sitePlaylists );
        tpl.setUserPlaylists( userPlaylists );

        getResponse().showHtml( tpl );
        
    }

    /**
     *  returns all the user created playlists
     * 
     *  @return
     * 
     *  @throws java.sql.SQLException
     * 
     */
    
    protected List<Playlist> getUserPlaylists() throws SQLException {

        ResultSet rs = null;
        PreparedStatement st = null;
        
        try {
                        
            final Database db = getDatabase();
            final String sql = " select p.id as id, p.name as name, count(t.id) as trackCount, " +
                            " u.id as userId, u.name as userName " +
                        " from playlists p " +
                            " left outer join playlist_tracks pt " +
                            " on pt.playlist_id = p.id " +
                            " inner join tracks t " +
                            " on t.id = pt.track_id " +
                            " inner join users u " +
                            " on u.id = p.user_id " +
                        " group by p.id, p.name, p.date_created " +
                        " order by p.date_created desc ";
            st = db.prepare( sql );
            rs = st.executeQuery();
            
            final List<Playlist> userPlaylists = new ArrayList<Playlist>();
            while ( rs.next() )
                userPlaylists.add( new Playlist(
                    rs.getInt("id"), rs.getString("name"), rs.getInt("trackCount"),
                    new User( rs.getInt("userId"), rs.getString("userName") )
                ));

            return userPlaylists;
            
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
            
    }
    
    /**
     *  returns all the playlists created by the site admin
     * 
     *  @return
     * 
     *  @throws java.sql.SQLException
     * 
     */
    
    protected List<Playlist> getSitePlaylists() throws SQLException {

        ResultSet rs = null;
        PreparedStatement st = null;
        
        try {
            
            final Database db = getDatabase();
            final String sql = " select p.id as id, p.name as name, count(pt.id) as trackCount " +
                        " from playlists p " +
                            " left outer join playlist_tracks pt " +
                            " on pt.playlist_id = p.id " +
                        " where p.user_id is null " + 
                        " group by p.id, p.name, p.date_created " +
                        " order by p.date_created desc ";
            st = db.prepare( sql );
            rs = st.executeQuery();
            
            final List<Playlist> sitePlaylists = new ArrayList<Playlist>();

            while ( rs.next() )
                sitePlaylists.add( new Playlist(
                    rs.getInt("id"), rs.getString("name"), rs.getInt("trackCount")
                ));
            
            return sitePlaylists;
            
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
    }
    
}
