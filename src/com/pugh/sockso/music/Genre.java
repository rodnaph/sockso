package com.pugh.sockso.music;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 *
 * @author Nathan Perrier
 */
public class Genre extends MusicItem {

    private static final Logger log = Logger.getLogger(Genre.class);
    
    /**
     *
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

        return new Genre(rs.getInt("id"), rs.getString("genre"));
    }
    
}
