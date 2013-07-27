
package com.pugh.sockso.music;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.Utils;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

public class Track extends MusicItem {

    private static final Logger log = Logger.getLogger( Track.class );
    
    private final Artist artist;
    private final Album album;
    private final Genre genre;
    private final String path;
    private final int number;
    private final Date dateAdded;
    
    private int playCount = 0;

    /**
     *  constructor
     * 
     *  @param Builder builder
     * 
     */

    public Track( Builder builder ) {
        super(MusicItem.TRACK, builder.id, builder.name);

        this.artist = builder.artist;
        this.album = builder.album;
        this.genre = builder.genre;
        this.path = builder.path;
        this.number = builder.number;
        this.dateAdded = builder.dateAdded;
    }


    public static class Builder {

        private int id;
        private String name;
        private Artist artist;
        private Album album;
        private Genre genre;
        private String path;
        private int number;
        private Date dateAdded;
        // private int playCount = 0;

        public Builder artist( Artist artist ) {
            this.artist = artist;
            return this;
        }

        public Builder album( Album album ) {
            this.album = album;
            return this;
        }

        public Builder genre( Genre genre ) {
            this.genre = genre;
            return this;
        }

        public Builder path( String path ) {
            this.path = path;
            return this;
        }

        public Builder number( int number ) {
            this.number = number;
            return this;
        }

        public Builder dateAdded( Date dateAdded ) {
            this.dateAdded = dateAdded;
            return this;
        }

        public Builder id( int id ) {
            this.id = id;
            return this;
        }

        public Builder name( String name ) {
            this.name = name;
            return this;
        }

        public Track build() {
            return new Track(this);
        }

    }


    public Artist getArtist() { return artist; }
    public Album getAlbum() { return album; }
    public Genre getGenre() { return genre; }
    public String getPath() { return path; }
    public int getNumber() { return number; }
    public int getPlayCount() { return playCount; }
    public Date getDateAdded() { return dateAdded; }
    
    public void setPlayCount( final int playCount ) {
        this.playCount = playCount;
    }
    
    /**
     *  creates a new track from a result set row
     * 
     *  @param rs the result set
     *  @return Track
     * 
     *  @throws SQLException
     * 
     */
    
    public static Track createFromResultSet( final ResultSet rs ) throws SQLException {

        final Artist artist = new Artist( rs.getInt("artistId"), rs.getString("artistName") );
        final Album album   = new Album( artist, rs.getInt("albumId"), rs.getString("albumName"), rs.getString("albumYear") );
        final Genre genre   = new Genre( rs.getInt("genreId"), rs.getString("genreName") );

        final Builder builder = new Track.Builder();
        builder.artist(artist)
                .album(album)
                .genre(genre)
                .id(rs.getInt("trackId"))
                .name(rs.getString("trackName"))
                .path(rs.getString("trackPath"))
                .number(rs.getInt("trackNo"))
                .dateAdded(rs.getDate("dateAdded"));

        return builder.build();
    }

    /**
     *  creates a list of tracks from a result set
     * 
     *  @param rs the result set to use
     *  @return list of Tracks
     * 
     *  @throws SQLException
     * 
     */
    
    public static List<Track> createListFromResultSet( final ResultSet rs ) throws SQLException {
        
        final List<Track> tracks = new ArrayList<Track>();
        
        while ( rs.next() )
            tracks.add( Track.createFromResultSet(rs) );

        return tracks;
        
    }
    
    /**
     *  returns the sql to use to select the right information
     *  for creating a new track object
     * 
     *  @return the sql
     * 
     */
    
    public static String getSelectSql() {
        return " select ar.id as artistId, ar.name as artistName, " +
               " al.id as albumId, al.name as albumName, al.year as albumYear, " +
               " t.id as trackId, t.name as trackName, t.path as trackPath, " +
               " t.track_no as trackNo, t.date_added as dateAdded, " +
               " g.id as genreId, g.name as genreName";
    }

    /**
     *  returns the sql to use to select the right information
     *  for creating a new track object
     * 
     *  @return the sql
     * 
     */
    
    public static String getSelectFromSql() {
        return getSelectSql() +
                " from tracks t " +
                            " inner join artists ar " +
                            " on ar.id = t.artist_id " +
                            " inner join albums al " +
                            " on al.id = t.album_id " +
                            " inner join genres g " +
                            " on g.id = t.genre_id ";
    }
    
    /**
     *  returns the sql to query for the tracks to add to a playlist, this can be
     *  either by artist, album or the track itself
     *
     *  @param type the type to filter on (ar = artist, etc...)
     *  @param id the id of the type to filter
     *  @return the select sql
     * 
     *  @throws BadRequestException
     * 
     */
        
    private static String getPlaylistSql( final String type, final int id, final String orderBySql ) throws BadRequestException {
        
        final String selectSql = Track.getSelectFromSql();
        
        if ( type.equals("tr") )
            return selectSql + " where t.id = '" + id + "' " + orderBySql;

        else if ( type.equals("al") )
            return selectSql + " where t.album_id = '" + id + "' " +
                (orderBySql.equals("") ? " order by t.track_no asc " : orderBySql);

        else if ( type.equals("ar") )
            return selectSql + " where t.artist_id = '" + id + "' " +
                (orderBySql.equals("") ? " order by al.name asc, t.track_no asc " : orderBySql);
        
        else if ( type.equals("pl") )
            return Playlist.getSelectTracksSql( id, orderBySql.equals("") ? " order by pt.id asc " : orderBySql );
        
        else throw new BadRequestException( "unknown play type: " + type, 400 );

    }

    /**
     *  returns a list of tracks based on the type and id criteria
     *
     *  @param db the database connection
     *  @param type the filter type
     *  @param id the filter id
     * 
     *  @return list of tracks found
     * 
     *  @throws SQLException
     *  @throws BadRequestException
     *  
     */
    
    public static List<Track> getTracks( final Database db, final String type, final int id  ) throws SQLException, BadRequestException {
        return getTracks( db, type, id, "" );
    }
    
    public static List<Track> getTracks( final Database db, final String type, final int id, final String orderBySql  ) throws SQLException, BadRequestException {

        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {

            final String sql = getPlaylistSql( type, id, orderBySql );
            final List<Track> songs = new ArrayList<Track>();
            
            st = db.prepare( sql );
            rs = st.executeQuery();
            
            while ( rs.next() )
                songs.add( Track.createFromResultSet(rs) );

            return songs;
            
        }

        finally {
            Utils.close( rs );
            Utils.close( st );
        }

    }

    /**
     *  returns a list of track objects that are loaded from an array
     *  of custom url arguments of the form "tr123/al456/ar789"
     * 
     *  @param db the database connection
     *  @param args the custom arguments
     *  @return list of track objects
     * 
     *  @throws SQLException
     *  @throws BadRequestException
     * 
     */

    public static List<Track> getTracksFromPlayArgs( final Database db, final String[] args ) throws SQLException, BadRequestException {
        return getTracksFromPlayArgs( db, args, "" );
    }
    
    public static List<Track> getTracksFromPlayArgs( final Database db, final String[] args, final String orderBySql ) throws SQLException, BadRequestException {

        final List<Track> tracks = new ArrayList<Track>();
        
        for ( final String arg : args ) {
            
            final String type = arg.substring( 0, 2 );
            final int id = Integer.parseInt( arg.substring(2,arg.length()) );
            
            tracks.addAll( Track.getTracks(db,type,id,orderBySql) );

        }
        
        return tracks;

    }
    
    /**
     *  Returns all tracks found where their path is below the one specified
     * 
     *  @param db
     *  @param path
     * 
     *  @return
     * 
     *  @throws SQLException 
     * 
     */
    
    public static List<Track> getTracksFromPath( final Database db, final String path ) throws SQLException {

        ResultSet rs = null;
        PreparedStatement st = null;
        
        try {
        
            final String sql = getSelectFromSql() +
                                " where t.path like ? " +
                                " order by t.path asc ";
            
            st = db.prepare( sql );
            st.setString( 1, path+ "%" );

            rs = st.executeQuery();
            
            return createListFromResultSet( rs );
            
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
    }

    /**
     *  Returns the URL to use to stream this track, with things like the users
     *  session on if that is required, etc...
     *
     *  @param p
     *  @param user
     *
     *  @return
     *
     */

    public String getStreamUrl( final Properties p, final User user ) {

        final String description = removeSpecialChars( getArtist().getName() ) +
                                    "-" +
                                    removeSpecialChars( getName() );
        
        final String sessionArgs =
            p.get(Constants.WWW_USERS_REQUIRE_LOGIN).equals(Properties.YES)
                && p.get(Constants.STREAM_REQUIRE_LOGIN).equals(Properties.YES)
                && user != null
            ? "?sessionId=" +user.getSessionId()+ "&sessionCode=" +user.getSessionCode()
            : "";

        return p.getUrl( "/stream/" + getId() + "/" + description + sessionArgs );
        
    }

    /**
     *  Removes any non alpha-numeric characters from a string
     *
     *  @param string
     *
     *  @return
     *
     */

    private String removeSpecialChars( final String string ) {

        return string.replaceAll( "[^A-Za-z0-9]", "" );
        
    }
    
    /**
     *  Finds a track by ID
     * 
     *  @param db
     *  @param id
     * 
     *  @return
     * 
     *  @throws SQLException 
     * 
     */
    
    public static Track find( final Database db, final int id ) throws SQLException {
        
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
            
            final String sql = getSelectFromSql() +
                               " where t.id = ? ";
            
            st = db.prepare( sql );
            st.setInt( 1, id );
            rs = st.executeQuery();
            
            if ( rs.next() ) {
                return Track.createFromResultSet( rs );
            }
            
        }
        
        finally {
            Utils.close( st );
            Utils.close( rs );
        }
        
        return null;
        
    }

    /**
     *  Find all tracks, with optional limit and offset since the given datetime
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

    public static List<Track> findAll( final Database db, final int limit, final int offset, final Date fromDate  ) throws SQLException {

        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
            
            String sql = getSelectFromSql();
            
            if ( fromDate != null ) {
                Timestamp timestamp = new Timestamp( fromDate.getTime() );
                sql += " where t.date_added >= '" + timestamp + "' ";
            }
            
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
     *  Find all tracks, with optional limit and offset
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
    
    public static List<Track> findAll( final Database db, final int limit, final int offset ) throws SQLException {
        
        return findAll( db, limit, offset, null );
    }
    
    /**
     *  A track is equal to another track if they have the same ID
     * 
     *  @param object
     * 
     *  @return 
     * 
     */

    @Override
    public boolean equals( final Object object ) {

        if ( !object.getClass().equals(Track.class) ) {
            return false;
        }
        
        final Track track = (Track) object;

        return getId() == track.getId();

    }

}
