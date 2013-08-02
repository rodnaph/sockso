
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

public class Album extends MusicItem {

    private final Artist artist;
    private final int trackCount;
    private final int playCount;
    private final Date dateAdded;
    private final String year;


    /**
     *  constructor
     *
     *  @param Builder builder
     *
     */

    public Album( final Builder builder ) {
        super( MusicItem.ALBUM, builder.id, builder.name);

        this.artist = builder.artist;
        this.dateAdded = ( builder.dateAdded != null ) ? new Date( builder.dateAdded.getTime() ) : null;
        this.year = ( builder.year != null ) ? builder.year : "";
        this.trackCount = builder.trackCount;
        this.playCount = builder.playCount;
    }

    public static class Builder {

        private int id;
        private String name;
        private Artist artist;
        private Date dateAdded;
        private String year = "";
        private int playCount = -1;
        private int trackCount = -1;

        public Builder id( int id ) {
            this.id = id;
            return this;
        }

        public Builder artist( Artist artist ) {
            this.artist = artist;
            return this;
        }

        public Builder name( String name ) {
            this.name = name;
            return this;
        }

        public Builder dateAdded( Date dateAdded ) {
            this.dateAdded = dateAdded;
            return this;
        }

        public Builder year( String year ) {
            this.year = year;
            return this;
        }

        public Builder playCount( int playCount) {
            this.playCount = playCount;
            return this;
        }

        public Builder trackCount( int trackCount) {
            this.trackCount = trackCount;
            return this;
        }

        public Album build() {
            return new Album(this);
        }
    }

    /**
     * Returns the year for this album
     *
     * @return
     */
    public String getYear() {

        return ( year.length() > 4 )
            ? year.substring( 0, 4 )
            : year;
    }

    public Artist getArtist() { return artist; }
    public int getTrackCount() { return trackCount; }
    public Date getDateAdded() { return new Date(dateAdded.getTime()); }
    public int getPlayCount() { return playCount; }

    /**
     *  Find albums for the specified artist
     * 
     *  @param db
     *  @param artistId
     * 
     *  @throws SQLException
     * 
     *  @return 
     * 
     */
    
    public static List<Album> findByArtistId( final Database db, final int artistId ) throws SQLException {
        
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
            
            final String sql = getSelectAllFromSql() +
                               " where al.artist_id = ? ";
            
            st = db.prepare( sql );
            st.setInt( 1, artistId );
            rs = st.executeQuery();
            
            return createListFromResultSet( rs );
            
        }
        
        finally {
            Utils.close( st );
            Utils.close( rs );
        }
        
    }
    
    /**
     *  Returns the SELECT X FROM Y to select albums
     * 
     *  @return 
     * 
     */
    
    protected static String getSelectAllFromSql() {
        
        return " select al.id, al.name, al.year, " +
                   " ar.id as artist_id, ar.name as artist_name " +
               " from albums al " +
                   " inner join artists ar " +
                   " on ar.id = al.artist_id ";
        
    }
    
    /**
     *  Creates an album from the current position of the result set
     * 
     *  @param rs
     * 
     *  @return
     * 
     *  @throws SQLException 
     * 
     */
    
    protected static Album createFromResultSet( final ResultSet rs ) throws SQLException {

        final Artist artist = new Artist.Builder()
                .id(rs.getInt( "artist_id" ))
                .name(rs.getString( "artist_name" ))
                .build();

        return new Album.Builder()
                .artist(artist)
                .id(rs.getInt( "id" ))
                .name(rs.getString( "name" ))
                .year(rs.getString( "year" ))
                .build();
    }
    
    /**
     *  Creates a list of albums from the result set
     * 
     *  @param rs
     * 
     *  @return
     * 
     *  @throws SQLException 
     * 
     */
    
    protected static List<Album> createListFromResultSet( final ResultSet rs ) throws SQLException {
        
        final List<Album> albums = new ArrayList<Album>();
        
        while ( rs.next() ) {
            albums.add( createFromResultSet(rs) );
        }

        return albums;

    }
        
    /**
     *  Finds an album by id, returns null if not found
     * 
     *  @param db
     *  @param id
     * 
     *  @throws SQLException
     * 
     *  @return 
     * 
     */
    
    public static Album find( final Database db, final int id ) throws SQLException {
        
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
            
            final String sql = getSelectAllFromSql() +
                               " where al.id = ? ";
            
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
    
    /**
     *  Finds all albums, returns listed alphabetically, with the specified offset and limit
     *  since the given datetime
     * 
     *  @param db
     *  @param limit
     *  @param offset
     *  @param fromDate
     * 
     *  @return
     * 
     *  @throws SQLException 
     * 
     */

    public static List<Album> findAll( final Database db, final int limit, final int offset, final Date fromDate  ) throws SQLException {

        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
            
            String sql = getSelectAllFromSql();
            
            if ( fromDate != null ) {
                Timestamp timestamp = new Timestamp( fromDate.getTime() );
                sql += " where al.date_added >= '" + timestamp + "' ";
            }
              
            sql += " order by al.name asc ";
            
            if ( limit != -1 ) {
                sql += " limit " +limit+
                       " offset " +offset;
            }
          
            st = db.prepare( sql );
            rs = st.executeQuery();
            
            return createListFromResultSet( rs );
            
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
    }
    
    /**
     *  Finds all albums, returns listed alphabetically
     * 
     *  @param db
     *  @param limit
     *  @param offset
     * 
     *  @return
     * 
     *  @throws SQLException 
     * 
     */
    
    public static List<Album> findAll( final Database db, final int limit, final int offset ) throws SQLException {
        
        return findAll( db, limit, offset, null );
    }

}
