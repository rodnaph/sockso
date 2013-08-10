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

import com.google.inject.Inject;

/**
 * Provides database access operations for {@link Album}
 *
 * @author Nathan Perrier
 */

public class AlbumDataProvider extends AbstractDataProvider<Album> {

    private static final Logger log = Logger.getLogger(AlbumDataProvider.class);

    private static final int NO_LIMIT = -1;

    private static final String SELECT_ALL_SQL = ""
                + " select al.id, al.name, al.year, al.date_added, "
                + " ar.id as artist_id, ar.name as artist_name, ar.date_added as artist_date_added "
                + " from albums al "
                + " inner join artists ar "
                + " on ar.id = al.artist_id ";

    @Inject
    public AlbumDataProvider( Database db ) {
        super(db);
    }

    /**
     * Find albums for the specified artist
     *
     * @param db
     * @param artistId
     *
     * @throws SQLException
     *
     * @return
     *
     */
    public List<Album> findByArtistId( final int artistId ) throws SQLException {

        PreparedStatement st = null;
        ResultSet rs = null;

        try {

            final String sql = getSelectAllFromSql()
                           + " where al.artist_id = ? ";

            st = this.db.prepare(sql);
            st.setInt(1, artistId);
            rs = st.executeQuery();

            return createListFromResultSet(rs);

        }
        finally {
            Utils.close(st);
            Utils.close(rs);
        }

    }

    public Album findByNameAndArtistId( final String name, final int artistId ) throws SQLException {

        PreparedStatement st = null;
        ResultSet rs = null;

        try {

            final String sql = getSelectAllFromSql()
                           + " where name = ? "
                           + " and artist_id = ? ";

            st = db.prepare(sql);
            st.setString(1, name);
            st.setInt(2, artistId);
            rs = st.executeQuery();

            return createFromResultSet(rs);

        }
        finally {
            Utils.close(st);
            Utils.close(rs);
        }

    }

    /**
     * Returns the SELECT X FROM Y to select albums
     *
     * @return
     *
     */
    protected static String getSelectAllFromSql() {

        return SELECT_ALL_SQL;
    }

    /**
     * Creates an album from the current position of the result set
     *
     * @param rs
     *
     * @return
     *
     * @throws SQLException
     *
     */
    protected static Album createFromResultSet( final ResultSet rs ) throws SQLException {

        final Artist artist = new Artist.Builder()
                .id(rs.getInt("artist_id"))
                .name(rs.getString("artist_name"))
                .dateAdded(rs.getDate("artist_date_added"))
                .build();

        return new Album.Builder()
                .artist(artist)
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .year(rs.getString("year"))
                .dateAdded(rs.getDate("date_added"))
                .build();
    }

    /**
     * Creates a list of albums from the result set
     *
     * @param rs
     *
     * @return
     *
     * @throws SQLException
     *
     */
    protected static List<Album> createListFromResultSet( final ResultSet rs ) throws SQLException {

        final List<Album> albums = new ArrayList<Album>();

        if ( rs != null ) {
            while ( rs.next() ) {
                albums.add(createFromResultSet(rs));
            }
        }
        
        return albums;

    }

    /**
     * Finds an album by id, returns null if not found
     *
     * @param db
     * @param id
     *
     * @throws SQLException
     *
     * @return
     *
     */
    public Album find( final int id ) throws SQLException {

        PreparedStatement st = null;
        ResultSet rs = null;

        try {

            final String sql = getSelectAllFromSql()
                             + " where al.id = ? ";

            st = this.db.prepare(sql);
            st.setInt(1, id);
            rs = st.executeQuery();

            if ( rs.next() ) {
                return createFromResultSet(rs);
            }

        } finally {
            Utils.close(st);
            Utils.close(rs);
        }

        return null;

    }

    /**
     * Finds all albums, returns listed alphabetically, with the specified
     * offset and limit
     * since the given datetime
     *
     * @param db
     * @param limit
     * @param offset
     * @param fromDate
     *
     * @return
     *
     * @throws SQLException
     *
     */
    public List<Album> findAll( final int limit, final int offset, final Date fromDate ) throws SQLException {

        PreparedStatement st = null;
        ResultSet rs = null;

        try {

            String sql = getSelectAllFromSql();

            if ( fromDate != null ) {
                Timestamp timestamp = new Timestamp(fromDate.getTime());
                sql += " where al.date_added >= '" + timestamp + "' ";
            }

            sql += " order by al.name asc ";

            if ( limit != NO_LIMIT ) {
                sql += " limit " + limit
                     + " offset " + offset;
            }

            st = this.db.prepare(sql);
            rs = st.executeQuery();

            return createListFromResultSet(rs);

        } finally {
            Utils.close(rs);
            Utils.close(st);
        }

    }

    /**
     * Finds all albums, returns listed alphabetically
     *
     * @param db
     * @param limit
     * @param offset
     *
     * @return
     *
     * @throws SQLException
     *
     */
    public List<Album> findAll( final int limit, final int offset ) throws SQLException {

        return findAll(limit, offset, null);
    }


    public void update( final Album album ) throws SQLException {

        PreparedStatement st = null;
        ResultSet rs = null;

        try {

            st = this.db.prepare(
                    " update albums set "
                    + " artist_id = ?, "
                    + " name = ?, "
                    + " year = ? "
                    + " where id = ?" );
            st.setInt(1, album.getArtist().getId());
            st.setString(2, album.getName());
            st.setString(3, album.getYear());
            st.setInt(4, album.getId());
            st.executeUpdate();

        }
        finally {
            Utils.close(rs);
            Utils.close(st);
        }
    }

    
    public Album save( final Album album ) throws SQLException {

        PreparedStatement st = null;
        ResultSet rs = null;

        try {

            st = this.db.prepare( " insert into albums ( artist_id, name, year, date_added ) "
                                + " values ( ?, ?, ?, current_timestamp ) ");
            st.setInt(1, album.getArtist().getId());
            st.setString(2, album.getName());
            st.setString(3, album.getYear());
            st.executeUpdate();

            rs = st.getGeneratedKeys();

            if ( rs != null && rs.next() ) {
                album.setId(rs.getInt(1));
                log.debug("Added Album: " + album);
            }
            else {
                throw new SQLException("Failed to insert album: " + album);
            }

            return album;

        }
        finally {
            Utils.close(rs);
            Utils.close(st);
        }
    }

    protected int exists( final Album album ) throws SQLException {

        ResultSet rs = null;
        PreparedStatement st = null;

        try {

            final String sql = " select id "
                             + " from albums "
                             + " where artist_id = ? "
                             + " and name = ? ";

            st = db.prepare(sql);
            st.setInt(1, album.getArtist().getId());
            st.setString(2, album.getName());
            rs = st.executeQuery();

            if ( rs.next() ) {
                return rs.getInt("id");
            }

            return NO_ID;

        }
        finally {
            Utils.close(rs);
            Utils.close(st);
        }

    }

}
