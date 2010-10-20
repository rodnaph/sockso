/*
 * Collection.java
 * 
 * Created on May 17, 2007, 9:49:41 PM
 * 
 * Represents the entire collection
 * 
 */

package com.pugh.sockso.music;

public class Collection extends MusicItem {

    private final int id;
    private final String path;
    
    public Collection() {
        this( -1, "" );
    }
    
    public Collection( final int id, final String path ) {
        super( MusicItem.COLLECTION, -1, null );
        this.id = id;
        this.path = path;
    }

    @Override
    public int getId() { return id; }
    @Override
    public String toString() { return "Collection"; }

    public String getPath() { return path; }
    
}
