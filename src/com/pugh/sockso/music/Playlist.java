
package com.pugh.sockso.music;

import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Playlist extends MusicItem {
    
    private final int trackCount;
    private final User user;
    
    public Playlist( final int id, final String name ) {
        this( id, name, -1 );
    }
    
    public Playlist( final int id, final String name, final int trackCount ) {
        this( id, name, trackCount, null );
    }

    public Playlist( final int id, final String name, final int trackCount, User user ) {
        super( MusicItem.PLAYLIST, id, name );
        this.trackCount = trackCount;
        this.user = user;
    }

    public User getUser() {
        return user;
    }
    
    public int getTrackCount() {
        return trackCount;
    }
    
    /**
     *  Returns the tracks for the playlist
     * 
     *  @param db
     * 
     *  @return 
     * 
     *  @throws SQLException
     *  @throws BadRequestException
     * 
     */
    
    public List<Track> getTracks( final Database db ) throws SQLException, BadRequestException {
        
        return Track.getTracks( db, "pl", getId() );
        
    }
    
    /**
     *  returns the sql to select the tracks from a playlist
     * 
     *  @param playlistId the id of the playlist
     *  @return the sql
     * 
     */

    public static String getSelectTracksSql( final int playlistId, final String orderBySql ) {
                
        return Track.getSelectSql() + 
                    " from playlists p " +
                    
                        " left outer join playlist_tracks pt " +
                        " on pt.playlist_id = p.id " +
                        
                        " inner join tracks t " +
                        " on t.id = pt.track_id " +
                        
                        " inner join artists ar " +
                        " on ar.id = t.artist_id " +
                        
                        " inner join albums al " +
                        " on al.id = t.album_id " +
                        
                        " inner join genres g " +
                        " on g.id = t.genre_id " +

                   " where p.id = '" + playlistId + "' " +
                   orderBySql;
        
    }
    
    /**
     *  Returns a list of playlists for the user with given limit and offset for the results.
     *  
     *  @param db database object to use
     *  @param limit max number of elements in the result
     *  @param offset offset for pagination
     * 
     *  @throws SQLException
     *  
     */
    
    public static List<Playlist> findAll( final Database db, int limit, int offset ) throws SQLException {
        
        return findPlaylistsForSql( db, limit, offset,  "" );
                    
    }
    
    /**
     *  Finds and returns playlists that match some specified sql where clause
     * 
     *  @param db
     *  @param limit
     *  @param offset
     *  @param whereSql
     * 
     *  @return
     * 
     *  @throws SQLException 
     * 
     */
    
    protected static List<Playlist> findPlaylistsForSql( final Database db, final int limit, final int offset, final String whereSql ) throws SQLException {
    	
        PreparedStatement st = null;
        ResultSet rs = null;

    	try {

            final List<Playlist> lists = new ArrayList<Playlist>();
            
            String sql = getSelectFromSql() +
                        whereSql +
                         " order by p.id desc ";

            if ( limit != -1 ) {
                sql += " limit " +limit+
                       " offset " +offset;
            }

            st = db.prepare( sql );
            rs = st.executeQuery();
            
            while ( rs.next() ) {
                lists.add( createFromResultSet(rs) );
            }
            
            return lists;
    	
    	}
        
        finally {
            Utils.close( rs );
            Utils.close( st );
    	}
    	
    }
    
    /**
     *  Finds all playlists for a user
     * 
     *  @param db
     *  @param user
     *  @param limit
     *  @param offset
     * 
     *  @return
     * 
     *  @throws SQLException 
     * 
     */
    
    public static List<Playlist> findAllForUser( final Database db, final User user, final int limit, final int offset ) throws SQLException {
        
        return user != null
            ? findPlaylistsForSql( db, limit, offset, " where p.user_id = '" +user.getId()+ "' " )
            : new ArrayList<Playlist>();
        
    }
    
    /**
     *  Finds all site playlists
     * 
     *  @param db
     *  @param limit
     *  @param offset
     * 
     *  @return
     * 
     *  @throws Exception 
     * 
     */
    
    public static List<Playlist> findAllForSite( final Database db, final int limit, final int offset ) throws SQLException {
        
        return findPlaylistsForSql( db, limit, offset, " where p.user_id = -1 " );
        
    }
    
    /**
     *  Returns the 'SELECT (fields) FROM (joins) ' sql snippet for querying playlists
     * 
     *  @return 
     * 
     */
    
    protected static String getSelectFromSql() {
        
        return " select p.id, p.name, u.id as userId, u.name as userName " +
               " from playlists p " +
                   " left outer join users u " +
                   " on u.id = p.user_id ";
        
    }
    
    /**
     *  Creates a playlist object from a result set created using standard sql
     *  select snippet
     * 
     *  @param rs
     * 
     *  @return
     * 
     *  @throws SQLException 
     * 
     */
    
    protected static Playlist createFromResultSet( final ResultSet rs ) throws SQLException {
        
        final User user = rs.getString( "userId" ) != null
            ? new User( rs.getInt("userId"), rs.getString("userName") )
            : null;
        
        return new Playlist(
            rs.getInt( "id" ),
            rs.getString( "name" ),
            -1,
            user
        );

    }

    /**
     *  Finds a playlist by id, or returns null if it doesn't exist
     * 
     *  @param db
     *  @param id
     * 
     *  @return 
     * 
     */
    
    public static Playlist find( final Database db, final int id ) throws SQLException {
        
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
            
            final String sql = getSelectFromSql() +
                               " where p.id = ? ";
            
            st = db.prepare( sql );
            st.setInt( 1, id );
            rs = st.executeQuery();
            
            if ( rs.next() ) {
                return createFromResultSet( rs );
            }
            
        }
        
        finally {
            Utils.close( st );
            Utils.close( rs );
        }
        
        return null;
        
    }
    
}
