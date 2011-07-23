
package com.pugh.sockso.web.action.browse;

import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.templates.web.browse.TByLetter;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.action.BaseAction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.io.IOException;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 *  shows artists by letter
 * 
 */

public class ByLetterer extends BaseAction {
    
    private static final Logger log = Logger.getLogger( ByLetterer.class );

    /**
     *  browses artists by their first letter
     * 
     *  @param req the request object
     *  @param res the response object
     * 
     *  @throws IOException
     *  @throws SQLException
     * 
     */
    
    public void handleRequest() throws IOException, SQLException {
        
        final Request req = getRequest();
        
        String letter = req.getUrlParam(2);
        if ( !letter.equals("") )
            letter = letter.substring(0,1).toLowerCase();

        showByLetter( letter, getArtistsByLetter(letter) );
        
    }
    
    /**
     *  shows the page to browse artists by letter
     * 
     *  @param letter
     *  @param artists
     * 
     *  @throws java.io.IOException
     * 
     */
    
    protected void showByLetter( final String letter, final Vector<Artist> artists ) throws IOException, SQLException {

        final TByLetter tpl = new TByLetter();

        tpl.setArtists( artists );
        tpl.setLetter( letter );

        getResponse().showHtml( tpl );
        
    }

    /**
     *  returns the artists that start with a particular letter
     * 
     *  @param letter
     * 
     *  @return
     * 
     *  @throws java.sql.SQLException
     * 
     */
    
    protected Vector<Artist> getArtistsByLetter( final String letter ) throws SQLException {
        
        ResultSet rs = null;
        PreparedStatement st = null;
                
        try {
            
            final Database db = getDatabase();
            final String sql = " select ar.id as id, ar.name as name, count(al.id) as albumCount " +
                               " from artists ar " +
                                   " left outer join albums al " +
                                   " on al.artist_id = ar.id " +
                               " where " +
                               ( letter.equals("")
                                   // doesn't start with a-z
                                   // @TODO ascii() not sqlite compatible
                                   ? " ascii(lower(ar.name)) < 96 " +
                                         " or ascii(lower(ar.name)) > 123 "
                                   // starts with a particular letter
                                   : " ar.browse_name like ? ") +
                               " group by ar.id, ar.name " +
                               " order by ar.name asc ";

            st = db.prepare( sql );

            if ( !letter.equals("") ) {
                st.setString( 1, letter + "%" );
            }

            rs = st.executeQuery();

            final Vector<Artist> artists = new Vector<Artist>();
            while ( rs.next() )
                artists.addElement( new Artist(
                    rs.getInt("id"), rs.getString("name"),
                    null, rs.getInt("albumCount"), -1
                ));

            return artists;
            
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
    }
    
}
