/*
 * MusicItem.java
 * 
 * Created on May 17, 2007, 11:21:05 AM
 * 
 * Represents an item in the collection
 * 
 */

package com.pugh.sockso.music;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;

public class MusicItem implements Transferable {

    public static final DataFlavor MUSIC_ITEM_FLAVOR = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType, "MusicItem" );

    private final DataFlavor[] flavors = { MUSIC_ITEM_FLAVOR };

    public static final String ARTIST = "artist";
    public static final String ALBUM = "album";
    public static final String GENRE = "genre";
    public static final String TRACK = "track";
    public static final String COLLECTION = "collection";
    public static final String PLAYLIST = "playlist";

    private String type;
    private String name;
    private int id;
    
    public MusicItem( final String type, final int id, final String name ) {
        this.type = type;
        this.id = id;
        this.name = name;
    }

    public String getType() {

        return type;
    }

    public int getId() {

        return id;
    }

    public String getName() {

        return name;
    }

    public String getShortType() {

        return type.substring(0, 2).toLowerCase();
    }

    public int getChildCount() {

        return 0;
    }

    public void setId( final int id ) {

        this.id = id;
    }

    @Override
    public String toString() {

        return name;
    }

    @Override
    public Object getTransferData( final DataFlavor flavor ) {

        return this;
    }

    @Override
    public boolean isDataFlavorSupported( final DataFlavor flavor ) {

        return ( flavor.equals(flavors[0]) );
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        
        return flavors.clone();
    }
    
}
