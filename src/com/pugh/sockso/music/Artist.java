/*
 * Artist.java
 * 
 * Created on May 17, 2007, 10:59:59 AM
 * 
 * Represents an artist in the collection
 * 
 */

package com.pugh.sockso.music;

import java.util.Date;

import org.apache.log4j.Logger;

public class Artist extends MusicItem {
    
    private final int albumCount, trackCount, playCount;
    private final Date dateAdded;

    private static Logger log = Logger.getLogger( CollectionManager.class );

    public Artist( final int id, final String name ) {
        this( id, name, null, -1, -1 );
    }
    
    public Artist( final int id, final String name, final int playCount ) {
        this( id, name, null, -1, -1, playCount );
    }
    
    public Artist( final int id, final String name, final Date dateAdded, final int albumCount, final int trackCount ) {
        this( id, name, dateAdded, albumCount, trackCount, -1 );
    }
    
    public Artist( final int id, final String name, final Date dateAdded, final int albumCount, final int trackCount, final int playCount ) {
        super( MusicItem.ARTIST, id, name );
        this.dateAdded = ( dateAdded != null ) ? new Date(dateAdded.getTime()) : null;
        this.albumCount = albumCount;
        this.trackCount = trackCount;
        this.playCount = playCount;
    }

    public Date getDateAdded() { return new Date(dateAdded.getTime()); }
    public int getTrackCount() { return trackCount; }
    public int getAlbumCount() { return albumCount; }
    public int getPlayCount() { return playCount; }

}
