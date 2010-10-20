
package com.pugh.sockso.web.action;

import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

/**
 *  a superclass for cover search classes.  just provides some utility methods
 *  that sub-classes may find useful.
 * 
 */

public abstract class AbstractCoverSearch implements CoverSearch {
            
    private static final Logger log = Logger.getLogger( AbstractCoverSearch.class );
    
    protected final Database db;
    
    public AbstractCoverSearch( final Database db ) {
        this.db = db;
    }

    /**
     *  returns the normal type name from the abbreviated name used
     *  in custom play url parameters. if the type is not known then
     *  null is returned.
     * 
     *  @param type the cust type (eg. "ar", "al", etc...)
     * 
     *  @return the normal type name
     * 
     */
    
    protected String getCustomTypeFromAbrev( final String type ) {
       
        if ( type.equals("pl") ) return "playlist";
        if ( type.equals("ar") ) return "artist";
        if ( type.equals("al") ) return "album";
        if ( type.equals("tr") ) return "track";
        
        return null;
        
    }

    /**
     *  given a music argument (eg ar123) searches for it's real keywords in the db.
     *  if nothing is found then null is returned.
     * 
     *  @param itemName the music itemName
     * 
     *  @return it's real keywords
     * 
     */
    
    protected String getMusicItemName( final String arg ) {

        final String type = getCustomTypeFromAbrev( arg.substring(0,2) );
        final int id = Integer.parseInt( arg.substring(2) );
        
        ResultSet rs = null;
        PreparedStatement st = null;
        
        try {
            
            final String sql = " select name as name " +
                         " from " + type + "s " +
                         " where id = ? ";
            st = db.prepare( sql );
            st.setInt( 1, id );
            rs = st.executeQuery();
            
            if ( rs.next() )
                return rs.getString( "name" );

        }
        
        catch ( final SQLException e ) {
            log.debug( e );
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
    
        return null;
        
    }

    /**
     *  returns the search keywords to use for a given play itemName (eg. ar123).  if
     *  it's an artist then it'll be the artist name.  if it's an album then it'll
     *  be the artist and album name.  if nothing is found then null is returned.
     * 
     *  @param itemName the play itemName (eg. ar456)
     * 
     *  @return
     * 
     */
    
    protected String getSearchKeywords( final String arg ) {
        
        String keywords = getMusicItemName( arg );
        
        // if we don't have any keywords we can't go on
        if ( keywords == null ) return null;

        // if it's an album then we also want to get the artist name
        if ( arg.substring(0,2).equals("al") ) {
            keywords = getArtistName(
                Integer.parseInt( arg.substring(2) )
             )+ " " +keywords;
            
        }
        
        keywords = removeUselessWords( keywords );
                
        return keywords;
        
    }
    
    /**
     *  tries to remove any useless words that will just confuse the search
     *  eg. "CD1" in album name or something...
     * 
     *  @param keywords
     * 
     *  @return
     * 
     */
    
    protected String removeUselessWords( String keywords ) {

        keywords = Utils.replaceAll( "cd\\s*\\d+", "", keywords );
        keywords = Utils.replaceAll( "disc\\s*\\d+", "", keywords );

        return keywords.trim();
        
    }
    
    /**
     *  tries to fetch the name of the artist for this albumId, of nothing
     *  is found then " is returned
     * 
     *  @param albumId
     * 
     *  @return
     * 
     */
    
    protected String getArtistName( final int albumId ) {
        
        ResultSet rs = null;
        PreparedStatement st = null;

        try {

            final String sql = " select ar.name as name " +
                               " from albums al " +
                                   " inner join artists ar " +
                                   " on ar.id = al.artist_id " +
                               " where al.id = ? ";
            st = db.prepare( sql );
            st.setInt( 1, albumId );
            rs = st.executeQuery();

            if ( rs.next() )
                return rs.getString( "name" );

        }

        catch ( final SQLException e ) {
            log.debug( e );
        }

        finally {
            Utils.close( rs );
            Utils.close( st );
        }

        return "";
        
    }
    
}
