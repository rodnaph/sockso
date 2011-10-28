/*
 * Playlists.java
 * 
 * Created on May 18, 2007, 12:13:17 PM
 * 
 * displays the playlists in the collection for the specified mode.  the items
 * can be dragged off onto the playlist creation panel.
 * 
 */

package com.pugh.sockso.gui;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.Utils;
import com.pugh.sockso.music.Playlist;
import com.pugh.sockso.music.CollectionManager;
import com.pugh.sockso.music.CollectionManagerListener;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JPopupMenu;
import javax.swing.DefaultListModel;
import javax.swing.JMenuItem;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

public class Playlists extends MusicList implements CollectionManagerListener {

    private static final Logger log = Logger.getLogger( Playlists.class );
    
    public static final int USER_PLAYLISTS = 1;
    public static final int SITE_PLAYLISTS = 2;
    
    private final Database db;
    private final CollectionManager cm;
    private final Resources r;
    private final DefaultListModel model;
    private final int mode;
    
    public Playlists( final Database db, final CollectionManager cm, final Resources r, final int mode ) {
        
        this.db = db;
        this.cm = cm;
        this.r = r;
        this.mode = mode;
        
        model = new DefaultListModel();
        addMouseListener( new MouseAdapter() {
           @Override
           public void mouseClicked( final MouseEvent evt ) {
               // screen for right-clicks
               if ( evt.getButton() == MouseEvent.BUTTON3 )
                   handleRightClick( evt );
           } 
        });
        setModel( model );

        cm.addCollectionManagerListener( this );

    }

    /**
     *  handles a right-click on the control
     *
     *  @param evt the MouseEvent object
     *  
     */

    private void handleRightClick( final MouseEvent evt ) {
        
        final Point p = evt.getPoint();

        if ( getSelectedIndices() == null ) return;

        final JMenuItem delete = new JMenuItem( "Delete", new ImageIcon(r.getImage("icons/16x16/delete.png")) );
        delete.addActionListener( new ActionListener() {
            public void actionPerformed( final ActionEvent evt ) {
                deleteSelectedItems();
            }
        });
        
        // show the popup menu
        final JPopupMenu menu = new JPopupMenu();
        menu.add( delete );
        menu.show( this, (int)p.getX(), (int)p.getY() );
        
    }
    
    /**
     *  does exactly what it says on the tin.  tries to remove the
     *  selected playlists from the collection, and if this works then
     *  they're removed from the list aswell.
     * 
     */
    
    private void deleteSelectedItems() {
        
        int index = getMinSelectionIndex();
        int finalIndex = getMaxSelectionIndex();

        for ( int i = index; i <= finalIndex; i++ ) {

            if ( i != -1 ) {

                final Playlist playlist = (Playlist) model.getElementAt( index );

                cm.removePlaylist( playlist.getId() );

            }

        }

    }
    
    /**
     *  completely refreshes the list with all the playlists
     *  from the database
     * 
     */
    
    protected void refresh() {
        
        model.clear();
        
        ResultSet rs = null;
        PreparedStatement st = null;
        
        try {

            final String sql = " select p.id as id, p.name as name " +
                            " from playlists p " +
                            " where user_id is " +(mode == USER_PLAYLISTS ? " not " : "" )+ " null " +
                            " order by p.name asc ";
            
            st = db.prepare( sql );
            rs = st.executeQuery();

            while ( rs.next() )
                model.addElement( new Playlist(rs.getInt("id"),rs.getString("name")) );

        }
        
        catch ( final SQLException e ) {
            log.error( e.getMessage() );
        }

        finally {
            Utils.close( rs );
            Utils.close( st );
        }

    }
    
    /**
     *  handles changes in the collection that will effect playlists
     * 
     *  @param type the change type
     *  @param message description
     * 
     */
    
    public void collectionManagerChangePerformed( final int type, final String message ) {
       if ( type == CollectionManagerListener.PLAYLISTS_CHANGED )
           refresh();
    }
    
}
