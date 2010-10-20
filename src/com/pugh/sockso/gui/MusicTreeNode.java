/*
 * MusicTreeNode.java
 * 
 * Created on May 15, 2007, 10:59:44 PM
 * 
 * A node in the music tree
 *
 */

package com.pugh.sockso.gui;

import com.pugh.sockso.music.MusicItem;

import javax.swing.tree.DefaultMutableTreeNode;

public class MusicTreeNode extends DefaultMutableTreeNode {    
    
    public MusicTreeNode( MusicItem userObject ) {
        super( userObject );
    }
    
}
