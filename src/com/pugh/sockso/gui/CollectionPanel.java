/**
 * CollectionPanel.java
 *
 * Created on May 12, 2007, 4:52 PM
 *
 */

package com.pugh.sockso.gui;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.music.*;
import com.pugh.sockso.*;
import com.pugh.sockso.gui.controls.RescanButton;

import java.io.File;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class CollectionPanel extends JPanel implements CollectionManagerListener {

    private DefaultListModel listModel;
    private JList list;
    private JLabel feedback;

    private final JFrame parent;
    private final Database db;
    private final CollectionManager cm;
    private final Resources r;
    private final Properties p;

    /**
     *  Creates a new instance of CollectionPanel
     *
     */
    
    public CollectionPanel( final JFrame parent, final Database db, final CollectionManager cm, final Resources r, final Properties p ) {

        this.parent = parent;
        this.db = db;
        this.cm = cm;
        this.r = r;
        this.p = p;
        
        feedback = new JLabel();
        
        listModel = new DefaultListModel();
        ResultSet rs = null;
        PreparedStatement st = null;
        
        String collectionId = db.escape( p.get(Constants.WWW_UPLOADS_COLLECTION_ID) );
        if ( collectionId.equals("") )
            collectionId = "-1";
        
        try {
            final String sql = " select c.path " +
                               " from collection c " +
                               " where c.id != ? " +
                               " order by c.path asc ";
            st = db.prepare( sql );
            st.setString( 1, collectionId );
            rs = st.executeQuery();
            while ( rs.next() )
                listModel.addElement( rs.getString("path") );
        }
        catch ( final SQLException e ) {
            feedback.setText( "Error loading collection information: " + e.getMessage() );
        }
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        list = new JList( listModel );

        final JPanel topPane = new JPanel();
        topPane.setLayout( new BorderLayout() );
        topPane.add( getButtonPane(), BorderLayout.NORTH );
        topPane.add( getInfoPane(), BorderLayout.CENTER );
        
        setLayout( new BorderLayout() );
        add( topPane, BorderLayout.NORTH );
        add( new JScrollPane(list), BorderLayout.CENTER );
        add( feedback, BorderLayout.SOUTH );

        // register listener
        cm.addCollectionManagerListener( this );
        
    }
    
    private JPanel getInfoPane() {
        
        final JLabel infoLabel = new JLabel( "<html><head></head><body>" +
                "Here you can add the folders on your computer with your mp3's in.  Just click " +
                "the 'Add' button above to get started.  You can also remove folders you've added " +
                "by selecting them in the list and then clicking the 'Remove' button." +
            "</body></html>" );

        final JPanel p = new JPanel( new BorderLayout() );
        p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5) );
        p.add( infoLabel, BorderLayout.CENTER );

        return p;
        
    }
    
    /**
     *  removes the selected folder from the collection
     *
     */
    
    private void removeCollectionFolder() {
       
        if ( list.getSelectedIndex() != -1 ) {
            final String path = (String) list.getSelectedValue();
            // make sure directory is removed
            if ( cm.removeDirectory(path) )
                listModel.remove( list.getSelectedIndex() );
        }

    }
    
    /**
     *  presents the user with the dialogs to add a new
     *  folder to the collection
     *
     */
    
    private void addCollectionFolder() {

        File newFolder = null;
        
        // first get the folder to add
        final JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        if ( chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION )
            newFolder = chooser.getSelectedFile();
        else return;

        ResultSet rs = null;
        PreparedStatement st = null;
        
        // check it isn't a subfolder of existing folder
        try {

            final String path = Utils.getPathWithSlash( newFolder );
            final String sql = " select 1 " +
                               " from collection c " +
                               " where substr(?,0,length(c.path)) = c.path ";
            
            st = db.prepare( sql );
            st.setString( 1, path );
            rs = st.executeQuery();

            if ( rs.next() ) {
                JOptionPane.showMessageDialog( parent, "That folder is already in your collection!" );
                return;
            }

        }
        catch ( final SQLException e ) {
            feedback.setText( e.getMessage() );
            return;
        }
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
        // add it to the collection, do this in a thread not
        // to hold up the gui.  this "newFolderAgain" variable is
        // hideous, but it works.
        final File newFolderAgain = newFolder;
        new Thread(new Runnable() {
            public void run() {
                listModel.addElement( newFolderAgain.getAbsolutePath() );
                cm.addDirectory( newFolderAgain );
            }
        }).start();
       
    }
    
    /**
     *  creates the panel with the add/remove buttons
     * 
     *  @return JPanel
     * 
     */
    
    private JPanel getButtonPane() {

        final JPanel panel = new JPanel( new BorderLayout() );
       
        panel.add( getFolderButtonPane(), BorderLayout.WEST );
        panel.add( getMiscButtonPane(), BorderLayout.EAST );
        
        return panel;
        
    }
    
    /**
     *  returns the button panel with misc buttons like re-scanning the collection
     * 
     *  @return
     * 
     */
    
    private JPanel getMiscButtonPane() {
       
        final RescanButton rescan = new RescanButton( r, cm, parent, db );
        final FlowLayout layout = new FlowLayout( FlowLayout.RIGHT );
        final JPanel panel = new JPanel( layout );
        
        rescan.init();
        panel.add( rescan );
        
        return panel;
        
    }
    
    /**
     *  returns the panel with buttons to add/remove folders
     * 
     *  @return
     * 
     */
    
    private JPanel getFolderButtonPane() {
        
        final Locale locale = r.getCurrentLocale();
        
        final JButton addFolder = new JButton( locale.getString("gui.label.addFolder"), new ImageIcon(r.getImage("icons/16x16/add.png")) );
        addFolder.addActionListener( new ActionListener() {
            public void actionPerformed( final ActionEvent evt ) {
                addCollectionFolder();
            }
        });
        
        final JButton removeFolder = new JButton( locale.getString("gui.label.removeFolder"), new ImageIcon(r.getImage("icons/16x16/delete.png")) );
        removeFolder.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                removeCollectionFolder();
            }
        });
        
        final JPanel panel = new JPanel();
        panel.setLayout( new FlowLayout(FlowLayout.LEFT) );
        panel.add( addFolder );
        panel.add( removeFolder );
        
        return panel;

    }
    
    /**
     *  handles change events from the CollectionManager
     * 
     *  @param type the type of change
     *  @param message a description of the change
     * 
     */
    
    public void collectionManagerChangePerformed( final int type, final String message ) {
        switch ( type ) {
            case CollectionManagerListener.DIRECTORY_SCAN_START:
                feedback.setText( "Scanning: " + message );
                break;
            case CollectionManagerListener.ARTIST_ADDED:
                feedback.setText( "Artist added: " + message );
                break;
            case CollectionManagerListener.ALBUM_ADDED:
                feedback.setText( "Album added: " + message );
                break;
            case CollectionManagerListener.TRACK_ADDED:
                feedback.setText( "Track added: " + message );
                break;
            case CollectionManagerListener.UPDATE_COMPLETE:
                feedback.setText( "Collection Updated!" );
                break;
        }
    }
    
}
