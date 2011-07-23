
package com.pugh.sockso.web.action.browse;

import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Album;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.templates.web.browse.TAlbum;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.action.BaseAction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.io.IOException;
import java.util.Vector;

/**
 *  shows an album and it's tracks
 *
 */

public class Albumer extends BaseAction {
    
    /**
     *  browses an album
     * 
     *  @param req the request object
     *  @param res the response object
     * 
     *  @throws IOException
     *  @throws SQLException
     *  @throws BadRequestException
     * 
     */
    
    public void handleRequest() throws IOException, SQLException, BadRequestException {
        
        final Request req = getRequest();
        final int id = Integer.parseInt( req.getUrlParam(2)  );
        final Album album = getAlbum ( id );
        final Vector<Track> tracks = getAlbumTracks( id );
        
        showAlbum( album, tracks );
        
    }
    
    /**
     *  shows the album page listing it's tracks
     * 
     *  @param album
     *  @param tracks
     * 
     *  @throws java.io.IOException
     * 
     */
    
    protected void showAlbum( final Album album, final Vector<Track> tracks ) throws IOException, SQLException {
        
        final TAlbum tpl = new TAlbum();

        tpl.setAlbum( album );
        tpl.setTracks( tracks );

        getResponse().showHtml( tpl );

    }

    /**
     *  fetches the tracks from an album
     * 
     *  @param albumId
     * 
     *  @return
     * 
     *  @throws java.sql.SQLException
     * 
     */
    
    protected Vector<Track> getAlbumTracks( final int albumId ) throws SQLException {

        ResultSet rs = null;
        PreparedStatement st = null;
        
        try {
            
            final Database db = getDatabase();
            final String sql = Track.getSelectFromSql() +
                  " where t.album_id = ? " +
                  " order by t.track_no asc ";
            st = db.prepare( sql );
            st.setInt( 1, albumId );
            rs = st.executeQuery();

            return Track.createVectorFromResultSet( rs );
            
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
            
    }
    
    /**
     *  fetches an album by id, if it's not found then a BadRequestException
     *  is thrown
     * 
     *  @param id
     * 
     *  @return
     * 
     *  @throws java.sql.SQLException
     *  @throws com.pugh.sockso.web.BadRequestException
     * 
     */
    
    protected Album getAlbum( final int id ) throws SQLException, BadRequestException {

        ResultSet rs = null;
        PreparedStatement st = null;
        
        try {
            
            final Database db = getDatabase();
            final String sql = " select ar.id as artistId, ar.name as artistName, " +
                        " al.id as albumId, al.name as albumName, al.year as albumYear, " +
                        " al.date_added, ( " +
                            " select count(*) " +
                            " from play_log l " +
                                " inner join tracks t " +
                                " on t.id = l.track_id " +
                            " where t.album_id = al.id " +
                        " ) as playCount " +
                    " from albums al " +
                        " inner join artists ar " +
                        " on ar.id = al.artist_id " +
                    " where al.id = ? " +
                    " limit 1 ";
            st = db.prepare( sql );
            st.setInt( 1, id );
            rs = st.executeQuery();
            if ( !rs.next() )
                throw new BadRequestException( "album not found", 404 );

            return new Album(
                new Artist( rs.getInt("artistId"), rs.getString("artistName") ),
                rs.getInt("albumId"), rs.getString("albumName"), rs.getString("albumYear"),
                rs.getDate("date_added"), -1, rs.getInt("playCount")
            );

        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }

    }
    
}
