/**
 * CollectionManager.java
 *
 * Created on May 10, 2007, 9:50 PM
 *
 */

package com.pugh.sockso.music;

import com.pugh.sockso.web.User;

import java.io.File;

public interface CollectionManager {

    public static final int DEFAULT_SCAN_INTERVAL = 5; // minutes
    
    /**
     *  checks the collection for updates.  it actually does 2 scans, one
     *  to check for new files, and the second to check the files in the
     *  collection are still there.
     *
     */
    
    public void checkCollection();
    
    /**
     *  allows components to register for collection activity messages
     * 
     *  @param listener the listener to register
     *
     */
    
    public void addCollectionManagerListener( final CollectionManagerListener listener );

    /**
     *  adds a directory to the collection
     * 
     *  @param dir the directory to add
     *
     */
    
    public int addDirectory( final File dir );
    
    /**
     *  removes a directory from the collection
     *
     *  @param path the path of the directory to remove
     * 
     */
    
    public boolean removeDirectory( final String path );

    /**
     *  saves a playlist to the collection
     * 
     *  @param name the name of the playlist
     *  @param tracks the tracks for the playlist
     *  @return id of playlist
     * 
     */
    
    public int savePlaylist( final String name, final Track[] tracks );

    /**
     *  saves a playlist for a user to the collection
     * 
     *  @param name the name of the playlist
     *  @param tracks the tracks for the playlist
     *  @param user the user to save for
     *  @return id of playlist
     * 
     */
    
    public int savePlaylist( final String name, final Track[] tracks, final User user );

    /**
     *  tries to remove a playlist from the collection, returns a boolean
     *  indicating if it was successful
     * 
     *  @param id id of playlist to remove
     *  @return boolean indicating success
     * 
     */
    
    public boolean removePlaylist( final int id );
 
    /**
     *  initiates a recursive scan of a directory
     * 
     *  @param collectionId the collection associated with this directory
     *  @param dir absolute path of directory to scan
     * 
     */
    
    public void scanDirectory( final int collectionId, final File dir );

    /**
     *  fires an event from the collection manager
     * 
     *  @param type the type of message from CollectionManagerListener
     *  @param message a description
     * 
     */
    
    public void fireCollectionManagerEvent( final int type, final String message );

    public void rescanTags();
    
}
