
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

import java.util.Vector;
import java.util.Date;

import org.apache.log4j.Logger;

public class Track extends MusicItem {

    private static final Logger log = Logger.getLogger( Track.class );
    
    private final Artist artist;
    private final Album album;
    private final String path;
    private final int number;
    private final Date dateAdded;
    
    private int playCount = 0;

    /**
     *  constructor
     * 
     *  @param artist
     *  @param album
     *  @param id
     *  @param name
     *  @param path
     *  @param number
     * 
     */
    
    public Track( final Artist artist, final Album album, final int id, final String name,
                  final String path, final int number, final Date dateAdded ) {
        super( MusicItem.TRACK, id, name );
        this.artist = artist;
        this.album = album;
        this.path = path;
        this.number = number;
        this.dateAdded = dateAdded;
    }

    public Artist getArtist() { return artist; }
    public Album getAlbum() { return album; }
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

        return new Track(
            artist, new Album( artist, rs.getInt("albumId"), rs.getString("albumName"), rs.getString("albumYear") ),
            rs.getInt("trackId"), rs.getString("trackName"), rs.getString("trackPath"),
            rs.getInt("trackNo"), rs.getDate("dateAdded")
        );

    }

    /**
     *  creates a vector of tracks from a result set
     * 
     *  @param rs the result set to use
     *  @return the track vector
     * 
     *  @throws SQLException
     * 
     */
    
    public static Vector<Track> createVectorFromResultSet( final ResultSet rs ) throws SQLException {
        
        final Vector<Track> tracks = new Vector<Track>();
        
        while ( rs.next() )
            tracks.addElement( Track.createFromResultSet(rs) );

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
               " t.track_no as trackNo, t.date_added as dateAdded ";
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
                            " on al.id = t.album_id ";
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
     *  returns a vector of tracks based on the type and id criteria
     *
     *  @param db the database connection
     *  @param type the filter type
     *  @param id the filter id
     * 
     *  @return vector of tracks found
     * 
     *  @throws SQLException
     *  @throws BadRequestException
     *  
     */
    
    public static Vector<Track> getTracks( final Database db, final String type, final int id  ) throws SQLException, BadRequestException {
        return getTracks( db, type, id, "" );
    }
    
    public static Vector<Track> getTracks( final Database db, final String type, final int id, final String orderBySql  ) throws SQLException, BadRequestException {

        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {

            final String sql = getPlaylistSql( type, id, orderBySql );
            final Vector<Track> songs = new Vector<Track>();
            
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
     *  returns a vector of track objects that are loaded from an array
     *  of custom url arguments of the form "tr123/al456/ar789"
     * 
     *  @param db the database connection
     *  @param args the custom arguments
     *  @return vector of track objects
     * 
     *  @throws SQLException
     *  @throws BadRequestException
     * 
     */

    public static Vector<Track> getTracksFromPlayArgs( final Database db, final String[] args ) throws SQLException, BadRequestException {
        return getTracksFromPlayArgs( db, args, "" );
    }
    
    public static Vector<Track> getTracksFromPlayArgs( final Database db, final String[] args, final String orderBySql ) throws SQLException, BadRequestException {

        final Vector<Track> tracks = new Vector<Track>();
        
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
    
    public static Vector<Track> getTracksFromPath( final Database db, final String path ) throws SQLException {

        ResultSet rs = null;
        PreparedStatement st = null;
        
        try {
        
            final String sql = getSelectFromSql() +
                                " where t.path like ? " +
                                " order by t.path asc ";
            
            st = db.prepare( sql );
            st.setString( 1, path+ "%" );

            rs = st.executeQuery();
            
            return createVectorFromResultSet( rs );
            
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
            p.get(Constants.WWW_USERS_REQUIRE_LOGIN).equals(p.YES)
                && p.get(Constants.STREAM_REQUIRE_LOGIN).equals(p.YES)
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
    
    public static Vector<Track> findAll( final Database db, final int limit, final int offset ) throws SQLException {
        
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
            
            String sql = getSelectFromSql();
            
            if ( limit != -1 ) {
                sql += " limit " +limit+
                       " offset " +offset;
            }
            
            st = db.prepare( sql );
            rs = st.executeQuery();
            
            return createVectorFromResultSet( rs );
            
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
    }
    
}
