
package com.pugh.sockso.gui;

import com.pugh.sockso.music.MusicItem;
import com.pugh.sockso.resources.FileResources;
import com.pugh.sockso.tests.SocksoTestCase;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultMutableTreeNode;

public class MusicTreeCellRendererTest extends SocksoTestCase {

    private MusicTreeCellRenderer rend;

    @Override
    public void setUp() {
        rend = new MusicTreeCellRenderer( new FileResources() );
    }

    public void testConstructor() {
        assertNotNull( new MusicTreeCellRenderer(null) );
    }

    public void testRenderingMusicItems() {
        String[] types = new String[] {
            MusicItem.COLLECTION,
            MusicItem.ARTIST,
            MusicItem.ALBUM,
            MusicItem.TRACK,
        };
        for ( String type : types ) {
            MusicItem item = new MusicItem( type, 1, "foo" );
            rend.getTreeCellRendererComponent( new JTree(), new MusicTreeNode(item), true,true,false,0,true );
        }
    }

    public void testRenderingNonMusicItemNodeHandledOk() {
        TreeNode node = new DefaultMutableTreeNode();
        rend.getTreeCellRendererComponent( new JTree(), node, true,true,false,0,true );
    }

}
