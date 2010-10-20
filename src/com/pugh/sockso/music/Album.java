/*
 * Album.java
 * 
 * Created on May 17, 2007, 11:00:06 AM
 * 
 * Represents an album in the collection
 * 
 */

package com.pugh.sockso.music;

import java.util.Date;

public class Album extends MusicItem {

    private final Artist artist;
    private final int trackCount;
    private final int playCount;
    private final Date dateAdded;
    
    public Album( final int artistId, final String artistName, final int id, final String name ) {
        this( new Artist(artistId,artistName), id, name );
    }
    
    public Album( final Artist artist, final int id, final String name ) {
        this( artist, id, name, -1 );
    }

    public Album( final Artist artist, final int id, final String name, final int trackCount ) {
        this( artist, id, name, null, trackCount, -1 );
    }
    
    public Album( final Artist artist, final int id, final String name, final Date dateAdded, final int trackCount, int playCount ) {
        super( MusicItem.ALBUM, id, name );
        this.artist = artist;
        this.trackCount = trackCount;
        this.playCount = playCount;
        this.dateAdded = ( dateAdded != null ) ? new Date(dateAdded.getTime()) : null;
    }
    
    public Artist getArtist() { return artist; }
    public int getTrackCount() { return trackCount; }
    public Date getDateAdded() { return new Date(dateAdded.getTime()); }
    public int getPlayCount() { return playCount; }

}
