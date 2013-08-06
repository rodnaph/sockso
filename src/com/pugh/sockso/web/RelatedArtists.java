
package com.pugh.sockso.web;

import com.pugh.sockso.Utils;
import com.pugh.sockso.cache.CacheException;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.web.action.AudioScrobbler;

import java.io.IOException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

import com.google.inject.Inject;
import java.util.List;

public class RelatedArtists {

    private final Database db;
    
    private final AudioScrobbler scrobbler;
    
    @Inject
    public RelatedArtists( final Database db, final AudioScrobbler scrobbler ) {
        
        this.db = db;
        this.scrobbler = scrobbler;
        
    }
    
    /**
     *  Returns artists related to the specified artist id
     * 
     *  @param artistId
     * 
     *  @return
     * 
     *  @throws SQLException
     *  @throws IOException
     *  @throws BadRequestException 
     * 
     */
    
    public List<Artist> getRelatedArtistsFor( final int artistId ) throws SQLException, IOException, BadRequestException, CacheException {
        
        ResultSet rs = null;
        PreparedStatement st = null;
        
        try {
            
            final ArrayList<Artist> artists = new ArrayList<Artist>();
            String artistSql = " '' ";
            
            for ( final String relatedArtist : scrobbler.getSimilarArtists(artistId) ) {
                artistSql += " , '" +db.escape(relatedArtist)+ "' ";
            }

            final String sql = " select id, name, date_added " +
                               " from artists " +
                               " where name in ( " +artistSql+ " ) ";

            st = db.prepare( sql );
            rs = st.executeQuery();
            
            while ( rs.next() ) {
                artists.add(new Artist.Builder()
                    .id(rs.getInt( "id" ))
                    .name(rs.getString( "name" ))
                    .dateAdded(rs.getDate( "date_added" ))
                    .build()
                );
            }

            return artists;
            
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
        
    }
    
}
