
package com.pugh.sockso.music;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.web.User;

import java.util.Date;

import org.apache.log4j.Logger;

public class Track extends MusicItem {

    private static final Logger log = Logger.getLogger( Track.class );
    
    private final Artist artist;
    private final Album album;
    private final Genre genre;
    private final String path;
    private final int number;
    private final Date dateAdded;
    private int playCount = 0;

    /**
     *  constructor
     * 
     *  @param Builder builder
     * 
     */

    public Track( Builder builder ) {
        super(MusicItem.TRACK, builder.id, builder.name);

        this.artist = builder.artist;
        this.album = builder.album;
        this.genre = builder.genre;
        this.path = builder.path;
        this.number = builder.number;
        this.dateAdded = ( builder.dateAdded != null ) ? new Date( builder.dateAdded.getTime() ) : null;
    }


    public static class Builder {

        private int id;
        private String name;
        private Artist artist;
        private Album album;
        private Genre genre;
        private String path;
        private int number;
        private Date dateAdded;
        // private int playCount = 0;

        public Builder artist( Artist artist ) {
            this.artist = artist;
            return this;
        }

        public Builder album( Album album ) {
            this.album = album;
            return this;
        }

        public Builder genre( Genre genre ) {
            this.genre = genre;
            return this;
        }

        public Builder path( String path ) {
            this.path = path;
            return this;
        }

        public Builder number( int number ) {
            this.number = number;
            return this;
        }

        public Builder dateAdded( Date dateAdded ) {
            this.dateAdded = dateAdded;
            return this;
        }

        public Builder id( int id ) {
            this.id = id;
            return this;
        }

        public Builder name( String name ) {
            this.name = name;
            return this;
        }

        public Track build() {
            return new Track(this);
        }

    }

    public Artist getArtist() {

        return artist;
    }

    public Album getAlbum() {

        return album;
    }

    public Genre getGenre() {

        return genre;
    }

    public String getPath() {

        return path;
    }

    public int getNumber() {

        return number;
    }

    public int getPlayCount() {

        return playCount;
    }

    public Date getDateAdded() {

        return dateAdded == null ? null : new Date(dateAdded.getTime());
    }
    
    public void setPlayCount( final int playCount ) {
        
        this.playCount = playCount;
    }
    
    /**
     *  Returns the URL to use to stream this track, with things like the users
     *  session on if that is required, etc...
     *
     *  @param p
     *  @param user
     *
     *  @return
     *
     */

    public String getStreamUrl( final Properties p, final User user ) {

        final String description = removeSpecialChars( getArtist().getName() ) +
                                    "-" +
                                    removeSpecialChars( getName() );
        
        final String sessionArgs =
            p.get(Constants.WWW_USERS_REQUIRE_LOGIN).equals(Properties.YES)
                && p.get(Constants.STREAM_REQUIRE_LOGIN).equals(Properties.YES)
                && user != null
            ? "?sessionId=" +user.getSessionId()+ "&sessionCode=" +user.getSessionCode()
            : "";

        return p.getUrl( "/stream/" + getId() + "/" + description + sessionArgs );
        
    }

    /**
     *  Removes any non alpha-numeric characters from a string
     *
     *  @param string
     *
     *  @return
     *
     */

    private String removeSpecialChars( final String string ) {

        return string.replaceAll( "[^A-Za-z0-9]", "" );
        
    }
    
    /**
     *  A track is equal to another track if they have the same ID
     * 
     *  @param object
     * 
     *  @return 
     * 
     */

    @Override
    public boolean equals( final Object object ) {

        if ( !object.getClass().equals(Track.class) ) {
            return false;
        }

        final Track track = (Track) object;

        return getId() == track.getId();

    }

}
