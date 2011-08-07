
package com.pugh.sockso.music;

import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Vector;

import com.google.inject.Singleton;

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
    
    public Vector<MusicItem> search( final String query ) throws SQLException {

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
                    " al.id as album_id, al.name as album_name " +
                " from tracks t " +
                    " inner join artists ar " +
                    " on ar.id = t.artist_id " +
                    " inner join albums al " +
                    " on al.id = t.album_id " +
                " where t.name like '%" + db.escape(query) + "%' " +

                " union " +

                " select '" + MusicItem.ALBUM + "', al.id, al.name, ar.id, ar.name, -1, '' " +
                " from albums al " +
                    " inner join artists ar " +
                    " on ar.id = al.artist_id " +
                " where al.name like '%" + db.escape(query) + "%' " +

                " union " +

                " select '" + MusicItem.ARTIST + "', ar.id, ar.name, -1, '', -1, '' " +
                " from artists ar " +
                " where ar.name like '%" + db.escape(query) + "%' " +

                " union " +

                " select '" + MusicItem.ALBUM + "', p.id, p.name, -1, '', -1, '' " +
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
    
    protected Vector<MusicItem> createResults( final ResultSet rs ) throws SQLException {
        
        final Vector<MusicItem> items = new Vector<MusicItem>();
            
        while ( rs.next() ) {

            final String type = rs.getString( "type" );

            if ( type.equals(MusicItem.TRACK) ) {
                final Track track = new Track(
                    new Artist( rs.getInt("artist_id"), rs.getString("artist_name") ),
                    new Album( null, rs.getInt("album_id"), rs.getString("album_name"), "" ),
                    rs.getInt("id"), rs.getString("name"), "", -1, null
                );
                items.addElement( track );
            }

            else if ( type.equals(MusicItem.ALBUM) ) {
                final Album album = new Album(
                    rs.getInt("artist_id"), rs.getString("artist_name"),
                    rs.getInt("id"), rs.getString("name"), ""
                );
                items.addElement( album );
            }

            else {
                items.addElement( new MusicItem(rs.getString("type"),rs.getInt("id"),rs.getString("name")) );
            }

        }

        return items;

    }
    
}
