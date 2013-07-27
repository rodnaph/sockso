package com.pugh.sockso.music;

import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Nathan Perrier
 */
public class Genre extends MusicItem {

    private static final Logger log = Logger.getLogger(Genre.class);
    
    /**
     * @param id
     * @param name
     */
    public Genre( final int id, final String name ) {
        super(MusicItem.GENRE, id, name);
    }
    
    /**
     * creates a new genre from a result set row
     *
     * @param rs the result set
     * @return Genre
     * @throws SQLException
     */
    public static Genre createFromResultSet( final ResultSet rs ) throws SQLException {

        return new Genre(rs.getInt("id"), rs.getString("name"));
    }

    /**
     *  Find all genres, listed alphabetically, with the specified offset and limit
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

    public static List<Genre> findAll( final Database db, final int limit, final int offset ) throws SQLException {

        PreparedStatement st = null;
        ResultSet rs = null;

        try {

            String sql = " select g.id, g.name " +
                               " from genres g " +
                               " order by g.name asc ";

            if ( limit != -1 ) {
                sql += " limit " +limit+ " " +
                       " offset " +offset;
            }

            st = db.prepare( sql );
            rs = st.executeQuery();

            final List<Genre> genres = new ArrayList<Genre>();

            while ( rs.next() ) {
                Genre genre = createFromResultSet(rs);
                genres.add( genre );
            }
            
            return genres;

        }

        finally {
            Utils.close( rs );
            Utils.close( st );
        }

    }
}
