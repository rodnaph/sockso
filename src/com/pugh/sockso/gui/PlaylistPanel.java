/*
 * PlaylistPanelList.java
 * 
 * Created on May 16, 2007, 11:52:07 PM
 * 
 * Allows music items to be dropped onto it, then moved around
 * to create playlists.
 *
 */

package com.pugh.sockso.gui;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.Utils;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.music.Album;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.music.Playlist;
import com.pugh.sockso.music.MusicItem;
import com.pugh.sockso.music.CollectionManager;

import java.awt.Component;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DragGestureEvent;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.JOptionPane;
import javax.swing.JList;
import javax.swing.JFrame;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

public class PlaylistPanel extends MusicList implements DropTargetListener {

    private static Logger log = Logger.getLogger( PlaylistPanel.class );
    
    private final JFrame parent;
    private final Database db;
    private final CollectionManager cm;
    
    private DefaultListModel model;
    private DropTarget dropTarget;
    private int dummyIndex;
    private Track dragItem;

    /**
     *  a custom cell renderer for displaying tracks information properly, when
     *  it's in the list normally, and when it's being dragged.
     * 
     */
    
    private final class MyCellRenderer extends DefaultListCellRenderer {
        
        @Override
        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean focus ) {
            
            JLabel c = (JLabel) super.getListCellRendererComponent( list, value, index, isSelected, focus );
            
            // just displaying a normal track?
            if ( value instanceof Track ) {
                Track track = (Track) value;
                c.setText( getTrackDisplay(track) );
            }

            // this is the dummy item, so we're dragging
            else {
                c.setBackground( list.getSelectionBackground() );
                c.setText( getTrackDisplay(dragItem) );
            }
            
            return c;

        }
        
        private String getTrackDisplay( Track track ) {
            return track.getName() + " - " +
                    track.getArtist().getName() + 
                        " (" + track.getAlbum().getName() + ")";
        }
        
    }
    
    /**
     *  constructor
     * 
     */
    
    @Inject
    public PlaylistPanel( final AppFrame parent, final Database db, final CollectionManager cm ) {
        
        super( DnDConstants.ACTION_MOVE );
        
        this.parent = parent;
        this.db = db;
        this.cm = cm;
        
        dummyIndex = -1;
        model = new DefaultListModel();        
        dropTarget = new DropTarget( this, DnDConstants.ACTION_COPY_OR_MOVE, this );

        setModel( model );
        setCellRenderer( new MyCellRenderer() );
        setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        
    }
    
    /**
     *  handles drop events onto this component
     * 
     *  @param evt the drop event
     * 
     */
    
    public void drop( DropTargetDropEvent evt ) {
        
        try {
            
            Transferable trans = evt.getTransferable();
            MusicItem item = (MusicItem) trans.getTransferData( MusicItem.MUSIC_ITEM_FLAVOR );

            // is it an internal move?
            if ( dummyIndex != -1 ) {
                model.removeElementAt( dummyIndex );
                model.insertElementAt( dragItem, dummyIndex );
                dragItem = null;
                dummyIndex = -1;
            }
            
            // otherwise external drag event
            else insertIntoPlaylist( evt.getLocation(), item );

        }
        
        catch ( IOException e ) {
            log.error( e.getMessage() );
        }

        catch ( UnsupportedFlavorException e ) {
            log.error( e.getMessage() );
        }
        
        catch ( ClassCastException e ) {
            log.error( e.getMessage() );
        }
        
    }

    /**
     *  inserts the music item into the current playlist
     * 
     *  @param item the music item to add
     * 
     */
    
    private void insertIntoPlaylist( Point location, MusicItem item ) {

        try {
            
            String sql = null;
            String type = item.getType();
            
            // get sql for track query
            if ( type.equals(MusicItem.COLLECTION) )
                sql = Track.getSelectFromSql(); // select everything in the collection
            else if ( type.equals(MusicItem.PLAYLIST) ) {
                Playlist playlist = (Playlist) item;
                sql = Playlist.getSelectTracksSql(playlist.getId(),"");
            }
            else if ( type.equals(MusicItem.ARTIST) ) {
                Artist artist = (Artist) item;
                sql = Track.getSelectFromSql() +
                        " where t.artist_id = '" + artist.getId() + "' ";
            }
            else if ( type.equals(MusicItem.ALBUM) ) {
                Album album = (Album) item;
                sql = Track.getSelectFromSql() +
                        " where t.album_id = '" + album.getId() + "' ";
            }
            else if ( type.equals(MusicItem.TRACK) ) {
                Track track = (Track) item;
                sql = Track.getSelectFromSql() +
                        " where t.id = '" + track.getId() + "' ";
            }

            if ( sql != null ) {
                
                ResultSet rs = null;
                PreparedStatement st = null;
                
                try {
                    
                    st = db.prepare( sql );
                    rs = st.executeQuery();
                    int index = locationToIndex( location );

                    while ( rs.next() ) {
                        Track track = Track.createFromResultSet(rs);
                        if ( index < 1 ) model.addElement( track );
                            else model.insertElementAt( track, index + 1 );
                    }

                }
                
                finally {
                    Utils.close( rs );
                    Utils.close( st );
                }

            }

        }

        catch ( SQLException e ) {
            log.error( e.getMessage() );
        }
        
    }
    
    public void dragEnter( DropTargetDragEvent evt ) {
         evt.acceptDrag( DnDConstants.ACTION_COPY_OR_MOVE );
    }

    public void dragExit( DropTargetEvent evt ) {}
    public void dropActionChanged( DropTargetDragEvent evt ) {}
    
    public void dragOver( DropTargetDragEvent evt ) {

        if ( dummyIndex == -1 ) return;
        
        int index = locationToIndex( evt.getLocation() );

        if ( model.getSize() >= dummyIndex )
            model.removeElementAt( dummyIndex );
        model.insertElementAt( "", index );
        
        dummyIndex = index;

    }

    /**
     *  returns the element at the given point (null if there's
     *  nothing actually there
     * 
     *  @param where the point to get item from
     *  @return the item at that point
     * 
     */
    
    private MusicItem getValue( Point where ) {

        int idx = locationToIndex( where );

        return (MusicItem) model.getElementAt( idx );

    }
    
    /**
     *  returns an array of the tracks in the playlist as Track objects
     * 
     *  @return array of tracks
     * 
     */
    
    private Track[] getTracks() {
       
        int size = model.getSize();
        Track[] tracks = new Track[ size ];
        
        for ( int i=0; i<size; i++ )
            tracks[ i ] = (Track) model.getElementAt( i );
        
        return tracks;
        
    }

    /**
     *  saves the current playlist
     * 
     */
    
    public void save() {

        // get name of playlist from user
        final String playlistName = JOptionPane.showInputDialog( parent, "Please enter a name for the playlist:" );
        if ( playlistName == null ) return;

        final Track[] tracks = getTracks();

        try {

            if ( playlistExists(playlistName) ) {
                if ( JOptionPane.showConfirmDialog( this, "That playlist already exists, replace it?",
                        "Overwrite Playlist", JOptionPane.YES_NO_OPTION ) != JOptionPane.YES_OPTION ) {
                    return;
                }
            }

            cm.savePlaylist( playlistName, tracks );

        }
        
        catch ( SQLException e ) {
            JOptionPane.showMessageDialog( parent, e.getMessage() );
            log.error( e.getMessage() );
        }
        
    }

    /**
     *  Checks if a playlist by this name exists in the database
     *
     *  @param name
     *
     *  @return
     *
     *  @throws SQLException
     *
     */
    
    protected boolean playlistExists( final String name ) throws SQLException {

        ResultSet rs = null;
        PreparedStatement st = null;

        try {
            
            final String sql = " select id " +
                         " from playlists p " +
                         " where name = ? ";

            st = db.prepare( sql );
            st.setString( 1, name );
            rs = st.executeQuery();

            return rs.next();

        }

        finally {
            Utils.close( rs );
            Utils.close( st );
        }

    }
    
    /**
     *  removes the selected items from the playlist
     * 
     */
    
    public void removeSelection() {
       
        int[] indexes = getSelectedIndices();

        for ( int i=0; i<indexes.length; i++ )
            model.removeElementAt( indexes[i] );

    }
    
    /**
     *  clears the playlist of all its items
     * 
     */
    
    public void clear() {
       model.clear(); 
    }
    
    /**
     *  this list acts as a drag source for items being moved around inside it
     * 
     *  @param evt the drag gesture event
     *
     */
    
    @Override
    public void dragGestureRecognized( DragGestureEvent evt ) {

        if ( getSelectedValue() == null ) return;

        super.dragGestureRecognized( evt );

        // remove drag item
        dummyIndex = getSelectedIndex();
        dragItem = (Track) model.getElementAt( dummyIndex );
        model.removeElementAt( dummyIndex );
        // and insert dummy
        model.insertElementAt( "", dummyIndex );

    }

}
