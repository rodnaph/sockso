
package com.pugh.sockso.music;

import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;

import com.google.inject.Singleton;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

/**
 *  a class for searching for music
 * 
 */

@Singleton
public class MusicSearch {

    private final Database db;
    
    /**
     *  constructor
     * 
     *  @param db
     * 
     */
    
    public MusicSearch( final Database db ) {
        
        this.db = db;
        
    }

    /**
     *  searches the database for music matching the specified query
     * 
     *  @param query
     * 
     *  @return
     * 
     *  @throws java.sql.SQLException
     * 
     */
    
    public List<MusicItem> search( final String query ) throws SQLException {

        ResultSet rs = null;
        PreparedStatement st = null;

        try {
        
            final String sql = createQuery( query );

            st = db.prepare( sql );
            rs = st.executeQuery();
            
            return createResults( rs );
            
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }

    }
    
    /**
     *  Create the complete query for finding music
     * 
     *  @param query
     * 
     *  @return 
     * 
     */
    
    protected String createQuery( final String query ) {
        
        return " select '" + MusicItem.TRACK + "' as type, t.id as id, t.name as name, " +
                    " ar.id as artist_id, ar.name as artist_name, " +
                    " al.id as album_id, al.name as album_name, " +
                    " g.id as genre_id, g.name as genre_name " +
                " from tracks t " +
                    " inner join artists ar " +
                    " on ar.id = t.artist_id " +
                    " inner join albums al " +
                    " on al.id = t.album_id " +
                    " inner join genres g " +
                    " on g.id = t.genre_id " +
                " where t.name like '%" + db.escape(query) + "%' " +

                " union " +

                " select '" + MusicItem.ALBUM + "', al.id, al.name, ar.id, ar.name, -1, '', -1, '' " +
                " from albums al " +
                    " inner join artists ar " +
                    " on ar.id = al.artist_id " +
                " where al.name like '%" + db.escape(query) + "%' " +

                " union " +

                " select '" + MusicItem.ARTIST + "', ar.id, ar.name, -1, '', -1, '', -1, '' " +
                " from artists ar " +
                " where ar.name like '%" + db.escape(query) + "%' " +

                " union " +

                " select '" + MusicItem.PLAYLIST + "', p.id, p.name, -1, '', -1, '', -1, '' " +
                " from playlists p " +
                " where p.name like '%" + db.escape(query) + "%' " +

                " order by name asc " +
                " limit 15 ";
        
    }
    
    /**
     *  Create the music item results from the search results
     * 
     *  @param rs
     * 
     *  @return
     * 
     *  @throws SQLException 
     * 
     */
    
    protected List<MusicItem> createResults( final ResultSet rs ) throws SQLException {
        
        final List<MusicItem> items = new ArrayList<MusicItem>();
            
        while ( rs.next() ) {

            final String type = rs.getString( "type" );

            if ( type.equals(MusicItem.TRACK) ) {
                Track.Builder track = new Track.Builder();
                track.artist( new Artist(rs.getInt("artist_id"), rs.getString("artist_name")) )
                        .album( new Album(null, rs.getInt("album_id"), rs.getString("album_name"), "") )
                        .genre( new Genre(rs.getInt("genre_id"), rs.getString("genre_name")) )
                        .id( rs.getInt("id") )
                        .name( rs.getString("name") )
                        .number(-1)
                        .path("")
                        .dateAdded(null);
                items.add( track.build() );
            }

            else if ( type.equals(MusicItem.ALBUM) ) {
                final Album album = new Album(
                    rs.getInt("artist_id"), rs.getString("artist_name"),
                    rs.getInt("id"), rs.getString("name"), ""
                );
                items.add( album );
            }
                    
            else if ( type.equals(MusicItem.PLAYLIST) ) {
                final Playlist playlist = new Playlist(
                    rs.getInt("id"),
                    rs.getString("name")
                );
                items.add( playlist );
            }

            else {
                items.add( new MusicItem(rs.getString("type"),rs.getInt("id"),rs.getString("name")) );
            }

        }

        return items;

    }
    
}
