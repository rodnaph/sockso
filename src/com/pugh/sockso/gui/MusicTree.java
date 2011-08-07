
package com.pugh.sockso.gui;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.Utils;
import com.pugh.sockso.music.MusicItem;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.music.Album;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.music.Collection;
import com.pugh.sockso.music.CollectionManagerListener;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.awt.dnd.DragSource;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceDragEvent;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

public class MusicTree extends JTree implements DragSourceListener, DragGestureListener, CollectionManagerListener, TreeExpansionListener {
    
    private static Logger log = Logger.getLogger( MusicTree.class );
    
    private DragSource dragSource;

    private final Database db;

    /**
     *  constructor
     * 
     *  @param db database connection
     * 
     */

    @Inject
    public MusicTree( Database db  ) {

        super( new MusicTreeNode(new Collection()) );

        this.db = db;
        
    }

    /**
     *  Initialise the music tree
     *
     */
    
    public void init() {
        
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer( this, DnDConstants.ACTION_COPY, this );

        setShowsRootHandles( true );

        refresh();

        addTreeExpansionListener( this );

    }

    /**
     *  Handler for when tree nodes are collapsed
     *
     *  @param evt
     *
     */
    
    public void treeCollapsed( final TreeExpansionEvent evt ) {}

    /**
     *  Handler for when tree nodes are expanded, we may need to load their children
     *
     *  @param evt
     *
     */

    public void treeExpanded( final TreeExpansionEvent evt ) {

        final TreePath path = evt.getPath();
        
        try {

            final MusicTreeNode node = (MusicTreeNode) path.getLastPathComponent();
            final MusicItem item = (MusicItem) node.getUserObject();

            if ( item.getType().equals(MusicItem.ARTIST) ) {
                fillArtistNode( node, item );
            }

            else if ( item.getType().equals(MusicItem.ALBUM) ) {
                fillAlbumNode( node, item );
            }

        }

        catch ( final SQLException e ) {
            log.debug( e );
        }

    }

    /**
     *  Fills the collection node with all the artists
     *
     *  @param root
     *
     *  @throws SQLException
     *
     */

    private void fillCollectionNode( final DefaultMutableTreeNode root ) throws SQLException {

        ResultSet rs = null;
        PreparedStatement st = null;

        try {

            final String sql = " select ar.id, ar.name " +
                               " from artists ar " +
                               " order by ar.name asc ";
            st = db.prepare( sql );
            rs = st.executeQuery();

            while ( rs.next() ) {
                final Artist artist = new Artist( rs.getInt("id"), rs.getString("name") );
                final MusicTreeNode node = new MusicTreeNode( artist );
                node.add( new DefaultMutableTreeNode() );
                root.add( node );
            }

            reloadNode( root );

        }

        finally {
            Utils.close( rs );
            Utils.close( st );
        }

    }

    /**
     *  Fills an artist node with albums
     *
     *  @param node
     *  @param item
     *
     */

    private void fillArtistNode( final MusicTreeNode node, final MusicItem item ) throws SQLException {

        ResultSet rs = null;
        PreparedStatement st = null;

        try {

            final Artist artist = new Artist( item.getId(), item.getName() );
            final String sql = " select al.id, al.name, al.year " +
                               " from albums al " +
                               " where al.artist_id = ? " +
                               " order by al.name asc ";

            st = db.prepare( sql );
            st.setInt( 1, item.getId() );
            rs = st.executeQuery();

            node.removeAllChildren();

            while ( rs.next() ) {
                final Album album = new Album( artist, rs.getInt("id"), rs.getString("name"), rs.getString("year") );
                final MusicTreeNode child = new MusicTreeNode( album );
                child.add( new DefaultMutableTreeNode() );
                node.add( child );
            }

            reloadNode( node );

        }

        finally {
            rs.close();
            st.close();
        }

    }

    /**
     *  Fills an album node with albums
     *
     *  @param node
     *  @param item
     *
     */

    private void fillAlbumNode( final MusicTreeNode node, final MusicItem item ) throws SQLException {

        ResultSet rs = null;
        PreparedStatement st = null;

        try {

            final String sql = Track.getSelectFromSql() +
                               " where t.album_id = ? " +
                               " order by t.track_no asc, t.name asc ";

            st = db.prepare( sql );
            st.setInt( 1, item.getId() );
            rs = st.executeQuery();

            node.removeAllChildren();

            while ( rs.next() ) {
                final Track track = Track.createFromResultSet( rs );
                final MusicTreeNode child = new MusicTreeNode( track );
                node.add( child );
            }

            reloadNode( node );

        }

        finally {
            rs.close();
            st.close();
        }

    }

    /**
     *  Refreshes the tree with the artists from the collection
     *
     */
    
    public void refresh() {
        
        try {

            final DefaultTreeModel model = (DefaultTreeModel) this.getModel();
            final DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();

            root.removeAllChildren();

            fillCollectionNode( root );
            reloadNode( root );
            expandNode( root );

        }

        catch ( final SQLException e ) {
            log.error( e );
        }

    }

    /**
     *  Expands a tree node and fires the expanded event
     *
     *  @param node
     *
     */
    
    protected void expandNode( final TreeNode node ) {

        final TreePath path = new TreePath( ((DefaultMutableTreeNode)node).getPath() );

        expandPath( path );
        fireTreeExpanded( path );

    }

    /**
     *  Informs the tree model that a node has changed
     *
     *  @param node
     *
     */
    
    private void reloadNode( final TreeNode node ) {

        final DefaultTreeModel model = (DefaultTreeModel) this.getModel();

        model.nodeStructureChanged( node );

    }

    /**
     *  the collection has changed
     * 
     *  @param type the type of change
     *  @param message a description of the change
     * 
     */
    
    public void collectionManagerChangePerformed( int type, String message ) {

        if ( type == CollectionManagerListener.UPDATE_COMPLETE ) {
            refresh();
        }

    }
    
    /**
     *  a drag gesture has been made, need to initiate the drag
     * 
     *  @param evt the drag event
     * 
     */
    
    public void dragGestureRecognized( DragGestureEvent evt ) {

        try {

            final MusicTreeNode node = (MusicTreeNode) getSelectionPath().getLastPathComponent();

            dragSource.startDrag(
                evt, DragSource.DefaultMoveDrop,
                (MusicItem) node.getUserObject(), this
            );

        }

        catch ( final ClassCastException e ) {
            log.error( "Error starting drag: " + e.getMessage() );
        }

    }

    /*
     *  un-used drag and drop methods
     * 
     */
    
    public void dragDropEnd( DragSourceDropEvent evt ) {}
    public void dragExit( DragSourceEvent evt ) {}
    public void dropActionChanged( DragSourceDragEvent evt ) {}
    public void dragOver( DragSourceDragEvent evt ) {}
    public void dragEnter( DragSourceDragEvent evt ) {}

}
