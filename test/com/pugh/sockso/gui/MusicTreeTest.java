
package com.pugh.sockso.gui;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;

import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;

public class MusicTreeTest extends SocksoTestCase {

    private MusicTree t;
    private DefaultTreeModel model;
    private DefaultMutableTreeNode root;

    @Override
    public void setUp() throws Exception {
        TestDatabase db = new TestDatabase();
        db.fixture( "musicTree" );
        t = new MusicTree( db );
        t.init();
        model = (DefaultTreeModel) t.getModel();
        root = (DefaultMutableTreeNode) model.getRoot();
    }

    public void testConstructorDoesNoWork() {
        new MusicTree( null );
    }

    public void testTreeCreatedWhenInitialised() {
        assertEquals( 2, root.getChildCount() );
    }

    public void testArtistNodesHaveDummyNodesWhenCreated() {
        assertEquals( 1, root.getChildAt(0).getChildCount() );
    }

    public void testAlbumsHaveDummyNodeWhenCreated() {
        TreeNode artist = root.getChildAt( 0 );
        t.expandNode( artist );
        assertEquals( 1, artist.getChildAt(0).getChildCount() );
        assertEquals( 1, artist.getChildAt(1).getChildCount() );
    }

    public void testAlbumsLoadedWhenArtistExpanded() {
        TreeNode artist = root.getChildAt( 0 );
        assertEquals( 1, artist.getChildCount() );
        t.expandNode( artist );
        assertEquals( 2, artist.getChildCount() );
    }

    public void testTracksLoadedWhenAlbumExpanded() {
        TreeNode artist = root.getChildAt( 0 );
        t.expandNode( artist );
        TreeNode album = artist.getChildAt( 0 );
        assertEquals( 1, album.getChildCount() );
        t.expandNode( album );
        assertEquals( 1, album.getChildCount() );
    }

    public void testArtistNodesNotExpandableIfTheyHaveNoAlbums() {
    }

    public void testAlbumNodesNotExpandableIfTheyHaveNoTracks() {
    }

    public void testRefreshingTreeAgainCreatesTreeWithJustArtists() {
    }

}
