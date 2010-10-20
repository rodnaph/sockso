
package com.pugh.sockso.music;

import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Vector;

/**
 *  a class for searching for music
 * 
 */

public class MusicSearch {

    private final Database db;
    
    /**
     *  constructor
     * 
     *  @param db
     * 
     */
    
    public MusicSearch( final Database db ) {
        
        this.db = db;
        
    }

    /**
     *  searches the database for music matching the specified query
     * 
     *  @param query
     * 
     *  @return
     * 
     *  @throws java.sql.SQLException
     * 
     */
    
    public Vector<MusicItem> search( final String query ) throws SQLException {

        ResultSet rs = null;
        PreparedStatement st = null;

        try {
        
            final String sql = " select '" + MusicItem.TRACK + "' as type, t.id as id, t.name as name " +
                        " from tracks t " +
                        " where t.name like '%" + db.escape(query) + "%' " +
                        " union " +
                        " select '" + MusicItem.ALBUM + "', al.id, al.name " +
                        " from albums al " +
                        " where al.name like '%" + db.escape(query) + "%' " +
                        " union " +
                        " select '" + MusicItem.ARTIST + "', ar.id, ar.name " +
                        " from artists ar " +
                        " where ar.name like '%" + db.escape(query) + "%' " +
                        " union " +
                        " select '" + MusicItem.ALBUM + "', p.id, p.name " +
                        " from playlists p " +
                        " where p.name like '%" + db.escape(query) + "%' " +
                        " order by name asc " +
                        " limit 15 ";

            st = db.prepare( sql );
            rs = st.executeQuery();
            
            final Vector<MusicItem> items = new Vector<MusicItem>();
            
            while ( rs.next() )
                items.addElement( new MusicItem(rs.getString("type"),rs.getInt("id"),rs.getString("name")) );

            return items;
            
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }

    }
    
}
