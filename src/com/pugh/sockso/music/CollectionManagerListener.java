/*
 * CollectionManagerListener.java
 *
 * Created on May 12, 2007, 5:40 PM
 *
 * An interface to listen for collection manager events
 * 
 */

package com.pugh.sockso.music;

public interface CollectionManagerListener {

    public static final int ERROR = 0;

    public static final int ARTIST_ADDED = 1;
    public static final int ALBUM_ADDED = 2;
    public static final int TRACK_ADDED = 3;
    public static final int GENRE_ADDED = 4;
    public static final int PLAYLISTS_CHANGED = 5;

    public static final int UPDATE_STARTED = 6;
    public static final int UPDATE_COMPLETE = 7;

    public static final int DIRECTORY_SCAN_START = 8;
    
    public void collectionManagerChangePerformed( int type, String message );

}
