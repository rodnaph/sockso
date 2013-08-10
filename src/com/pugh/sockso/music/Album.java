
package com.pugh.sockso.music;

import java.util.Date;

public class Album extends MusicItem {

    private Artist artist;
    private int trackCount;
    private int playCount;
    private Date dateAdded;
    private String year;

    /**
     *  constructor
     *
     *  @param Builder builder
     *
     */

    public Album( final Builder builder ) {
        super( MusicItem.ALBUM, builder.id, builder.name);

        this.artist = builder.artist;
        this.dateAdded = ( builder.dateAdded != null ) ? new Date( builder.dateAdded.getTime() ) : null;
        this.year = ( builder.year != null ) ? builder.year : "";
        this.trackCount = builder.trackCount;
        this.playCount = builder.playCount;
    }

    public static class Builder {

        private int id;
        private String name;
        private Artist artist;
        private Date dateAdded;
        private String year = "";
        private int playCount = -1;
        private int trackCount = -1;

        public Builder id( int id ) {
            this.id = id;
            return this;
        }

        public Builder artist( Artist artist ) {
            this.artist = artist;
            return this;
        }

        public Builder name( String name ) {
            this.name = name;
            return this;
        }

        public Builder dateAdded( Date dateAdded ) {
            this.dateAdded = dateAdded;
            return this;
        }

        public Builder year( String year ) {
            this.year = year;
            return this;
        }

        public Builder playCount( int playCount) {
            this.playCount = playCount;
            return this;
        }

        public Builder trackCount( int trackCount) {
            this.trackCount = trackCount;
            return this;
        }

        public Album build() {
            return new Album(this);
        }
    }

    /**
     * Returns the year for this album
     *
     * @return
     */
    public String getYear() {

        return ( year.length() > 4 )
            ? year.substring( 0, 4 )
            : year;
    }

    public Artist getArtist() {

        return artist;
    }

    public int getTrackCount() {

        return trackCount;
    }

    public Date getDateAdded() {

        return dateAdded == null ? null : new Date(dateAdded.getTime());
    }

    public int getPlayCount() {
        
        return playCount;
    }

    public void setYear( final String year ) {
        
        this.year = year;
    }
}
