
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

    public Album( final int artistId, final String artistName, final int id, final String name, final String year ) {
        this( new Artist(artistId,artistName), id, name, year );
    }
    
    public Album( final Artist artist, final int id, final String name, final String year ) {
        this( artist, id, name, year, -1 );
    }

    public Album( final Artist artist, final int id, final String name, final String year, final int trackCount ) {
        this( artist, id, name, year, null, trackCount, -1 );
    }
    
    public Album( final Artist artist, final int id, final String name, final String year, final Date dateAdded, final int trackCount, int playCount ) {
        super( MusicItem.ALBUM, id, name );
        this.artist = artist;
        this.trackCount = trackCount;
        this.playCount = playCount;
        this.dateAdded = ( dateAdded != null ) ? new Date(dateAdded.getTime()) : null;
        this.year = ( year != null ) ? year : "";
    }

    /**
     * Returns the year for this album
     *
     * @return
     */
    public String getYear() {

        if ( year == null ) {
            return "";
        }

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
        
        return new Album(
            new Artist(
                rs.getInt( "artist_id" ),
                rs.getString( "artist_name" )
            ),
            rs.getInt( "id" ),
            rs.getString( "name" ),
            rs.getString( "year" )
        );
        
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

    public static List<Album> findAll( final Database db, final int limit, final int offset, final long fromDate  ) throws SQLException {

        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
            
            String sql = getSelectAllFromSql();
            
            if ( fromDate > 0 ) {    
                Timestamp timestamp = new Timestamp( fromDate );
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
        
        return findAll( db, limit, offset, 0 );
    }

    
}
