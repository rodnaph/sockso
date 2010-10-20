
package com.pugh.sockso.gui;

import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.music.MusicItem;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.apache.log4j.Logger;

/**
 *  A custom renderer for the music tree
 *
 */

public class MusicTreeCellRenderer extends DefaultTreeCellRenderer {

    private static Logger log = Logger.getLogger( MusicTreeCellRenderer.class );
    
    private final Resources r;

    /**
     *  Constructor
     *
     *  @param r
     *
     */

    public MusicTreeCellRenderer( Resources r ) {

        this.r = r;

    }

    /**
     *  Renders and returns the component to display in the tree
     *
     *  @param tree
     *  @param value
     *  @param sel
     *  @param expanded
     *  @param leaf
     *  @param row
     *  @param hasFocus
     *
     *  @return
     *
     */

    @Override
    public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {

        super.getTreeCellRendererComponent( tree, value, sel, expanded, leaf, row, hasFocus );

        if ( value.getClass().equals(MusicTreeNode.class) ) {
            
            // @TODO move icon fetching to MusicItem class

            final MusicTreeNode node = (MusicTreeNode) value;
            final MusicItem item = (MusicItem) node.getUserObject();
            final String type = item.getType();

            if ( type.equals(MusicItem.COLLECTION) ) {
                setIcon( new ImageIcon(r.getImage("icons/16x16/collection.png")) );
            }

            else if ( type.equals(MusicItem.ARTIST) ) {
                setIcon( new ImageIcon(r.getImage("icons/16x16/artist.png")) );
            }

            else if ( type.equals(MusicItem.ALBUM) ) {
                setIcon( new ImageIcon(r.getImage("icons/16x16/album.png")) );
            }

            else if ( type.equals(MusicItem.TRACK) ) {
                setIcon( new ImageIcon(r.getImage("icons/16x16/tracks.png")) );
            }

        }

        return this;

    }

}
