
package com.pugh.sockso.music;

import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

public class Artist extends MusicItem {
    
    private static final Logger log = Logger.getLogger( Artist.class );

    private final int albumCount;
    private final int trackCount;
    private final int playCount;
    private final Date dateAdded;

    public Artist( final Builder builder ) {
        super( MusicItem.ARTIST, builder.id, builder.name );

        this.albumCount = builder.albumCount;
        this.trackCount = builder.trackCount;
        this.playCount  = builder.playCount;
        this.dateAdded  = ( builder.dateAdded != null ) ? new Date( builder.dateAdded.getTime() ) : null;
    }

    public static class Builder {

        private int id;
        private String name;
        private int albumCount = -1;
        private int trackCount = -1;
        private int playCount = -1;
        private Date dateAdded;

        public Builder id( int id ) {
            this.id = id;
            return this;
        }

        public Builder name( String name ) {
            this.name = name;
            return this;
        }

        public Builder albumCount( int albumCount ) {
            this.albumCount = albumCount;
            return this;
        }

        public Builder trackCount( int trackCount ) {
            this.trackCount = trackCount;
            return this;
        }

        public Builder playCount( int playCount ) {
            this.playCount = playCount;
            return this;
        }

        public Builder dateAdded( Date dateAdded ) {
            this.dateAdded = dateAdded;
            return this;
        }

        public Artist build() {
            return new Artist(this);
        }
    }

    /**
     *  Getters for artist info
     * 
     */
    
    public Date getDateAdded() { 
        return dateAdded == null ? null : new Date(dateAdded.getTime());
    }
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
            
            final String sql = " select ar.id, ar.name, ar.date_added " +
                               " from artists ar " +
                               " where id = ? ";
            
            st = db.prepare( sql );
            st.setInt( 1, id );
            rs = st.executeQuery();
            
            if ( rs.next() ) {
                return new Artist.Builder()
                        .id(rs.getInt( "id" ))
                        .name(rs.getString( "name" ))
                        .dateAdded(rs.getDate("date_added"))
                        .build();
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
     *  since the given datetime
     * 
     *  @param db
     *  @param limit
     *  @param offset
     *  @param fromDate
     * 
     *  @throws SQLException
     * 
     *  @return 
     * 
     */

    public static List<Artist> findAll( final Database db, final int limit, final int offset, final Date fromDate  ) throws SQLException {

        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
            
            String sql = " select ar.id, ar.name, ar.date_added " +
                         " from artists ar ";
            
            if ( fromDate != null ) {
                Timestamp timestamp = new Timestamp( fromDate.getTime() );
                sql += " where ar.date_added >= '" + timestamp + "' ";
            }
            
            sql += " order by ar.name asc ";
            
            if ( limit != -1 ) {
                sql += " limit " +limit+ " " +
                       " offset " +offset;
            }            
                        
            st = db.prepare( sql );
            rs = st.executeQuery();

            final List<Artist> artists = new ArrayList<Artist>();
            
            while ( rs.next() ) {

                Artist artist = new Artist.Builder()
                        .id(rs.getInt( "id" ))
                        .name(rs.getString( "name" ))
                        .dateAdded(rs.getTimestamp("date_added"))
                        .build();
                artists.add( artist );
            }
            
            return artists;

        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
           
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
        
    public static List<Artist> findAll( final Database db, final int limit, final int offset ) throws SQLException {
        
        return findAll(db, limit, offset, null);
    }
    
}
