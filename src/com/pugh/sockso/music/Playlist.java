/*
 * Playlist.java
 * 
 * Created on May 17, 2007, 9:50:09 PM
 * 
 * Represents a playlist
 * 
 */

package com.pugh.sockso.music;

import com.pugh.sockso.web.User;

public class Playlist extends MusicItem {
    
    private final int trackCount;
    private final User user;
    
    public Playlist( final int id, final String name ) {
        this( id, name, -1 );
    }
    
    public Playlist( final int id, final String name, final int trackCount ) {
        this( id, name, trackCount, null );
    }

    public Playlist( final int id, final String name, final int trackCount, User user ) {
        super( MusicItem.PLAYLIST, id, name );
        this.trackCount = trackCount;
        this.user = user;
    }

    public User getUser() {
        return user;
    }
    
    public int getTrackCount() {
        return trackCount;
    }
    
    /**
     *  returns the sql to select the tracks from a playlist
     * 
     *  @param playlistId the id of the playlist
     *  @return the sql
     * 
     */

    public static String getSelectTracksSql( final int playlistId, final String orderBySql ) {
                
        return Track.getSelectSql() + 
                    " from playlists p " +
                    
                        " left outer join playlist_tracks pt " +
                        " on pt.playlist_id = p.id " +
                        
                        " inner join tracks t " +
                        " on t.id = pt.track_id " +
                        
                        " inner join artists ar " +
                        " on ar.id = t.artist_id " +
                        
                        " inner join albums al " +
                        " on al.id = t.album_id " +
                        
                   " where p.id = '" + playlistId + "' " +
                   orderBySql;
        
    }
    
}
