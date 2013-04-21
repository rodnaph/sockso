
package com.pugh.sockso.web.action.browse;

import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Album;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.templates.web.browse.TArtist;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.action.BaseAction;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Artister extends BaseAction {
    
    /**
     *  browses a particular artist
     * 
     *  @param req the request object
     *  @param res the response object
     * 
     *  @throws IOException
     *  @throws SQLException
     *  @throws BadRequestException
     * 
     */
    
    @Override
    public void handleRequest() throws IOException, SQLException, BadRequestException {
        
        final Request req = getRequest();
        final int id = Integer.parseInt( req.getUrlParam(2)  );
        final Artist artist = getArtist( id );
        final List<Album> albums = getArtistAlbums( id );
        
        showArtist( artist, albums );
        
    }
    
    /**
     *  shows the artist page listing their albums
     * 
     *  @param artist
     *  @param albums
     * 
     *  @throws java.io.IOException
     * 
     */
    
    protected void showArtist( final Artist artist, final List<Album> albums  ) throws IOException, SQLException {
        
        final TArtist tpl = new TArtist();

        tpl.setArtist( artist );
        tpl.setAlbums( albums );

        getResponse().showHtml( tpl );

    }

    /**
     *  fetches the albums for an artist
     * 
     *  @param artistId
     * 
     *  @return
     * 
     *  @throws java.sql.SQLException
     * 
     */
    
    protected List<Album> getArtistAlbums( final int artistId ) throws SQLException {

        ResultSet rs = null;
        PreparedStatement st = null;
        
        try {
            
            final Database db = getDatabase();
            final String sql = " select ar.id as artistId, ar.name as artistName, " +
                        " al.id as albumId, al.name as albumName, al.year as albumYear, count(t.id) as trackCount " +
                        " from albums al " +
                            " inner join artists ar " +
                            " on ar.id = al.artist_id " +
                            " left outer join tracks t " +
                            " on t.album_id = al.id " +
                        " where (t.artist_id = ? or al.artist_id = ?) " +
                        " group by artistId, artistName, albumId, albumYear, albumName " +
                        " order by al.year desc, al.name asc ";
            st = db.prepare( sql );
            st.setInt( 1, artistId );
            st.setInt( 2, artistId );
            rs = st.executeQuery();

            final List<Album> albums = new ArrayList<Album>();
            
            while ( rs.next() )
                albums.add(
                    new Album(
                        new Artist( rs.getInt("artistId"), rs.getString("artistName") ),
                        rs.getInt("albumId"), rs.getString("albumName"), rs.getString("albumYear"), rs.getInt("trackCount")                  
                    )
                
                );

            return albums;
            
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
            
    }
    
    /**
     *  fetches an artist, if not found then a BadRequestException is thrown
     * 
     *  @param artistId
     * 
     *  @return
     * 
     *  @throws java.sql.SQLException
     *  @throws com.pugh.sockso.web.BadRequestException
     * 
     */
    
    protected Artist getArtist( final int artistId ) throws SQLException, BadRequestException {
        
        ResultSet rs = null;
        PreparedStatement st = null;
        
        try {

            final Database db = getDatabase();
            final String sql = " select ar.id as id, ar.name as name, ar.date_added as date_added, " +
                        " count(t.id) as trackCount, " +
                        " ( " +
                            " select count(*) as playCount " +
                            " from play_log l " +
                                " inner join tracks t " +
                                " on t.id = l.track_id " +
                            " where t.artist_id = ar.id " +
                        " ) as playCount " +
                  " from artists ar " +
                       " left outer join tracks t " +
                       " on t.artist_id = ar.id " +
                  " where ar.id = ? " +
                  " group by ar.id, ar.name, ar.date_added " +
                  " limit 1 ";
            st = db.prepare( sql );
            st.setInt( 1, artistId );
            rs = st.executeQuery();
            if ( !rs.next() )
                throw new BadRequestException( "artist not found", 404 );
            
            return new Artist(
                rs.getInt("id"), rs.getString("name"),
                rs.getDate("date_added"), -1, rs.getInt("trackCount"),
                rs.getInt("playCount")
            );
        
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }

    }
    
}
