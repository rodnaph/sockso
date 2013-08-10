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
 * Provides database access operations for {@link Artist}
 *
 * @author Nathan Perrier
 */

public class ArtistDataProvider extends AbstractDataProvider<Artist> {

    private static final Logger log = Logger.getLogger(ArtistDataProvider.class);

    private static final int NO_LIMIT = -1;

    @Inject
    public ArtistDataProvider( Database db ) {
        super(db);
    }

    public Artist findByName( final String name ) throws SQLException {

        PreparedStatement st = null;
        ResultSet rs = null;

        try {

            String sql = " select id "
                       + " from artists "
                       + " where name = ? ";

            st = db.prepare(sql);
            st.setString(1, name);
            rs = st.executeQuery();

            return createFromResultSet(rs);

        }
        finally {
            Utils.close(rs);
            Utils.close(st);
        }

    }

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

    public Artist find( final int id ) throws SQLException {

        PreparedStatement st = null;
        ResultSet rs = null;

        try {

            final String sql = " select ar.id, ar.name, ar.date_added "
                             + " from artists ar "
                             + " where id = ? ";

            st = db.prepare(sql);
            st.setInt(1, id);
            rs = st.executeQuery();

            if ( rs.next() ) {
                return new Artist.Builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .dateAdded(rs.getDate("date_added"))
                        .build();
            }

        }
        finally {
            Utils.close(st);
            Utils.close(rs);
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

    public List<Artist> findAll( final int limit, final int offset, final Date fromDate  ) throws SQLException {

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

            if ( limit != NO_LIMIT ) {
                sql += " limit " +limit+ " " +
                       " offset " +offset;
            }

            st = db.prepare( sql );
            rs = st.executeQuery();

            final List<Artist> artists = new ArrayList<Artist>();

            while ( rs.next() ) {
                final Artist artist = createFromResultSet(rs);
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

    public List<Artist> findAll( final int limit, final int offset ) throws SQLException {

        return findAll(limit, offset, null);
    }

    public void update( final Artist artist ) throws SQLException {

        PreparedStatement st = null;
        ResultSet rs = null;

        try {

            st = this.db.prepare(
                      " update albums set "
                    + " artist_id = ?, "
                    + " name = ?, "
                    + " browse_name = ? "
                    + " where id = ?" );
            st.setInt(1, artist.getId());
            st.setString(2, artist.getName());
            st.setString(3, artist.getBrowseName());
            st.setInt(4, artist.getId());
            st.executeUpdate();
        }
        finally {
            Utils.close(rs);
            Utils.close(st);
        }
    }

    public Artist save( final Artist artist ) throws SQLException {

        PreparedStatement st = null;
        ResultSet rs = null;

        try {

            st = db.prepare(" insert into artists ( name, date_added, browse_name ) "
                          + " values ( ?, current_timestamp, ? ) ");
            st.setString(1, artist.getName());
            st.setString(2, artist.getBrowseName());
            st.executeUpdate();

            rs = st.getGeneratedKeys();

            if ( rs != null && rs.next() ) {
                artist.setId(rs.getInt(1));
                log.debug("Added Artist: " + artist);
            }
            else {
                throw new SQLException("Failed to insert artist: " + artist);
            }

            return artist;

        }
        finally {
            Utils.close(rs);
            Utils.close(st);
        }
    }

    protected int exists( final Artist artist ) throws SQLException {

        ResultSet rs = null;
        PreparedStatement st = null;

        try {

            final String sql = " select id "
                             + " from artists "
                             + " where name = ? ";

            st = db.prepare(sql);
            st.setString(1, artist.getName());
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

    private Artist createFromResultSet( ResultSet rs ) throws SQLException {

        if ( rs != null ) {
            
            return new Artist.Builder()
                    .id(rs.getInt("id"))
                    .name(rs.getString("name"))
                    .dateAdded(rs.getTimestamp("date_added"))
                    .build();
        }

        return null;
    }

}
