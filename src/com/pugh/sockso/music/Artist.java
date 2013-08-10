
package com.pugh.sockso.music;

import java.util.Date;

import org.apache.log4j.Logger;

public class Artist extends MusicItem {
    
    private static final Logger log = Logger.getLogger( Artist.class );

    private final String browseName;
    private final int albumCount;
    private final int trackCount;
    private final int playCount;
    private final Date dateAdded;

    public Artist( final Builder builder ) {
        super( MusicItem.ARTIST, builder.id, builder.name );

        this.browseName = builder.browseName;
        this.albumCount = builder.albumCount;
        this.trackCount = builder.trackCount;
        this.playCount  = builder.playCount;
        this.dateAdded  = ( builder.dateAdded != null ) ? new Date( builder.dateAdded.getTime() ) : null;
    }

    public static class Builder {

        private int id;
        private String name;
        private String browseName;
        private int albumCount = -1;
        private int trackCount = -1;
        private int playCount = -1;
        private Date dateAdded;

        public Builder id( int id ) {
            this.id = id;
            return this;
        }

        public Builder name( String name ) {
            this.name = name;
            return this;
        }

        public Builder browseName( String browseName ) {
            this.browseName = browseName;
            return this;
        }

        public Builder albumCount( int albumCount ) {
            this.albumCount = albumCount;
            return this;
        }

        public Builder trackCount( int trackCount ) {
            this.trackCount = trackCount;
            return this;
        }

        public Builder playCount( int playCount ) {
            this.playCount = playCount;
            return this;
        }

        public Builder dateAdded( Date dateAdded ) {
            this.dateAdded = dateAdded;
            return this;
        }

        public Artist build() {
            return new Artist(this);
        }

    }

    /**
     *  Getters for artist info
     * 
     */

    public String getBrowseName() {

        return browseName;
    }

    public Date getDateAdded() {

        return dateAdded == null ? null : new Date(dateAdded.getTime());
    }

    public int getTrackCount() {

        return trackCount;
    }

    public int getAlbumCount() {

        return albumCount;
    }

    public int getPlayCount() {

        return playCount;
    }

}
