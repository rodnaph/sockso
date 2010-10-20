/*
 *  MusicList.java
 * 
 *  Created on May 23, 2007, 9:38:45 PM
 * 
 *  A draggable list that contains MusicItems
 * 
 */

package com.pugh.sockso.gui;

import com.pugh.sockso.music.MusicItem;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DragGestureListener;

import javax.swing.JList;

import org.apache.log4j.Logger;

public class MusicList extends JList implements DragSourceListener, DragGestureListener{

    private static final Logger log = Logger.getLogger( MusicList.class );
    
    private DragSource dragSource;

    public MusicList() {
        this( DnDConstants.ACTION_COPY );
    }
    
    /**
     *  you can specify a drag type.
     * 
     *  @param dndType the DnD type
     * 
     */
    
    public MusicList( int dndType ) {

        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer( this, dndType, this );

    }
    
    public void dragDropEnd( DragSourceDropEvent evt ) {}
    public void dragExit( DragSourceEvent evt ) {}
    public void dropActionChanged( DragSourceDragEvent evt ) {}
    public void dragOver( DragSourceDragEvent evt ) {}
    public void dragEnter( DragSourceDragEvent evt ) {}

    /**
     *  a drag gesture has been recognised, passes the selected music
     *  item to the transfar handler
     * 
     *  @param evt the drag event
     * 
     */
    
    public void dragGestureRecognized( DragGestureEvent evt ) {

        if ( getSelectedValue() == null ) return;
        
        try {
            
            dragSource.startDrag(
                evt, DragSource.DefaultMoveDrop,
                (MusicItem) getSelectedValue(), this
            );

        }
        catch ( ClassCastException e ) {
            log.error( "Error starting drag: " + e.getMessage() );
        }

    }

}
