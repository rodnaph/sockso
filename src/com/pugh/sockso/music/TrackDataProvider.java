
package com.pugh.sockso.music;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.Utils;
import com.pugh.sockso.web.BadRequestException;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.inject.Inject;


/**
 * Provides database access operations for {@link Track}
 *
 * @author Nathan Perrier
 */

public class TrackDataProvider extends AbstractDataProvider<Track> {

    @Inject
    public TrackDataProvider( Database db ) {
        super(db);
    }

    @Override
    protected int exists( Track t ) throws SQLException {
        
    }

    public List<Track> findAll( int limit, int offset ) throws SQLException {

    }

    public List<Track> findAll( int limit, int offset, Date fromDate ) throws SQLException {

    }

    public Track find( int id ) throws SQLException {

    }

    public void update( Track t ) throws SQLException {

    }

    public Track save( Track t ) throws SQLException {

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
     *  creates a new track from a result set row
     *
     *  @param rs the result set
     *  @return Track
     *
     *  @throws SQLException
     *
     */

    public static Track createFromResultSet( final ResultSet rs ) throws SQLException {

        final Artist artist = new Artist.Builder()
                .id(rs.getInt("artistId"))
                .name(rs.getString("artistName"))
                .dateAdded(rs.getDate("artistDateAdded"))
                .build();
        final Album album = new Album.Builder()
                .artist(artist)
                .id(rs.getInt("albumId"))
                .name(rs.getString("albumName"))
                .year(rs.getString("albumYear"))
                .dateAdded(rs.getDate("albumDateAdded"))
                .build();
        final Genre genre = new Genre( rs.getInt("genreId"), rs.getString("genreName") );

        final Track.Builder builder = new Track.Builder();
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
        return " select ar.id as artistId, ar.name as artistName, ar.date_added as artistDateAdded, " +
               " al.id as albumId, al.name as albumName, al.year as albumYear, al.date_added as albumDateAdded, " +
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

}
