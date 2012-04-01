
package com.pugh.sockso.music;

import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;

import java.io.File;
<<<<<<< HEAD
=======

>>>>>>> 651f57e63ba9fcae401bc730b1a47d3da2a35a7a
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *  Represents a collection of music, rooted at a certain folder.
 *
 */

public class Collection extends MusicItem {

    private final int id;
    private final String path;
    
    /**
     * Create a collection with id -1
     * 
     */
    public Collection() {
        
        this( -1, "" );
        
    }
    
    /**
     *  Create a collection with the specified ID and path
     * 
     *  @param id
     *  @param path 
     * 
     */
    
    public Collection( final int id, final String path ) {

        super( MusicItem.COLLECTION, -1, null );

        this.id = id;
        this.path = path;

    }

    /**
     *  Returns the collection id
     * 
     *  @return 
     * 
     */
    
    @Override
    public int getId() { return id; }
    
    /**
     *  Returns the string "Collection"
     * 
     *  @return 
     * 
     */
    
    @Override
    public String toString() { return "Collection"; }

    /**
     *  Returns the path to the root of this collection
     * 
     *  @return 
     * 
     */
    
    public String getPath() { return path; }

    /**
     *  Finds a collection based on a directory path, which could be inside the
     *  collection.
     *
     *  @param db
     *  @param path
     *
     *  @return
     *
     */
    
    public static Collection findByPath( final Database db, final String path ) throws SQLException {
        
        return findByPathWithSeparator( db, path, File.separator );
    }

    /**
     *  Finds a collection based on a directory path, which could be inside the
     *  collection.  The separator parameter is appended to the path if the path 
     *  contains no trailing slash.
     *
     *  @param db
     *  @param path
     *  @param separator
     *
     *  @return
     *
     */
    
    protected static Collection findByPathWithSeparator( final Database db, final String path, final String separator ) throws SQLException {
        
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {

            final String matchPath = path.endsWith( separator ) ? path : path + separator;
            final String sql = " select c.id, c.path " +
                               " from collection c " +
                               " where c.path = substring( ?, 1, length(c.path) )";

            st = db.prepare( sql );
            st.setString( 1, matchPath );
            rs = st.executeQuery();

            if ( rs.next() ) {
                return new Collection(
                    rs.getInt( "id" ),
                    rs.getString( "path" )
                );
            }

            return null;

        }
        
        finally {
            Utils.close( st );
            Utils.close( rs );
        }
    }
}
