
package com.pugh.sockso.music;

import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Date;
import java.util.Vector;

import org.apache.log4j.Logger;

public class Artist extends MusicItem {
    
    private static Logger log = Logger.getLogger( CollectionManager.class );

    private final int albumCount, trackCount, playCount;
    
    private final Date dateAdded;

    /**
     *  Constructors for creating artists with carying amount of info
     * 
     */
    
    public Artist( final int id, final String name ) {
        this( id, name, null, -1, -1 );
    }
    
    public Artist( final int id, final String name, final int playCount ) {
        this( id, name, null, -1, -1, playCount );
    }
    
    public Artist( final int id, final String name, final Date dateAdded, final int albumCount, final int trackCount ) {
        this( id, name, dateAdded, albumCount, trackCount, -1 );
    }
    
    public Artist( final int id, final String name, final Date dateAdded, final int albumCount, final int trackCount, final int playCount ) {
        super( MusicItem.ARTIST, id, name );
        this.dateAdded = dateAdded;
        this.albumCount = albumCount;
        this.trackCount = trackCount;
        this.playCount = playCount;
    }

    /**
     *  Getters for artist info
     * 
     */
    
    public Date getDateAdded() { return dateAdded; }
    public int getTrackCount() { return trackCount; }
    public int getAlbumCount() { return albumCount; }
    public int getPlayCount() { return playCount; }

    /**
     *  Find an artist by ID
     * 
     *  @param db
     *  @param id
     * 
     *  @throws SQLException
     * 
     *  @return 
     * 
     */
    
    public static Artist find( final Database db, final int id ) throws SQLException {
        
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
            
            final String sql = " select ar.id, ar.name " +
                               " from artists ar " +
                               " where id = ? ";
            
            st = db.prepare( sql );
            st.setInt( 1, id );
            rs = st.executeQuery();
            
            if ( rs.next() ) {
                return new Artist(
                    rs.getInt( "id" ),
                    rs.getString( "name" )
                );
            }
            
        }
        
        finally {
            Utils.close( st );
            Utils.close( rs );
        }
        
        return null;
        
    }
    
    /**
     *  Find all artists, listed alphabetically, with the specified offset and limit
     * 
     *  @param db
     *  @param limit
     *  @param offset
     * 
     *  @throws SQLException
     * 
     *  @return 
     * 
     */
    
    public static Vector<Artist> findAll( final Database db, final int limit, final int offset ) throws SQLException {
        
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
            
            String sql = " select ar.id, ar.name, ar.date_added " +
                               " from artists ar " +
                               " order by ar.name asc ";
            
            if ( limit != -1 ) {
                sql += " limit " +limit+ " " +
                       " offset " +offset;
            }

            st = db.prepare( sql );
            rs = st.executeQuery();

            final Vector<Artist> artists = new Vector<Artist>();
            
            while ( rs.next() ) {
                Artist artist = new Artist(
                    rs.getInt( "id" ),
                    rs.getString( "name" ),
                    rs.getDate( "date_added" ),
                    -1,
                    -1
                );
                artists.add( artist );
            }
            
            return artists;

        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
    }
    
}
