
package com.pugh.sockso.gui;

import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.music.CollectionManager;
import com.pugh.sockso.gui.action.ImportPlaylist;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.DefaultListModel;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class MusicPanel extends JPanel {
    
    private final static Logger log = Logger.getLogger( MusicPanel.class );
    
    DefaultListModel playlistModel;
    
    private final JFrame parent;
    private final Resources r;
    private final CollectionManager cm;
    private final Locale locale;
    private final Injector injector;
    
    /**
     *  Creates a new instance of MusicPanel
     *
     *  @param parent the parent window
     *  @param db database connection
     *  @param cm collection manager
     * 
     */
    
    @Inject
    public MusicPanel( final AppFrame parent, final CollectionManager cm, final Resources r,
                       final Locale locale, final Injector injector ) {
    
        this.parent = parent;
        this.r = r;
        this.cm = cm;
        this.locale = locale;
        this.injector = injector;

    }
    
    protected void init() {

        final SitePlaylists sitePlaylists = injector.getInstance( SitePlaylists.class );
        sitePlaylists.refresh();

        final UserPlaylists userPlaylists = injector.getInstance( UserPlaylists.class );
        userPlaylists.refresh();

        final ActionListener importPlaylistAction = injector.getInstance( ImportPlaylist.class );

        final MusicTree musicTree = injector.getInstance( MusicTree.class );
        final JSplitPane playlistsPanel = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            getSitePlaylistsPanel( sitePlaylists, importPlaylistAction ),
            getUserPlaylistsPanel( userPlaylists )
        );
        final JTabbedPane tabbedPane = getTabbedPane( musicTree, playlistsPanel );

        musicTree.setCellRenderer( new MusicTreeCellRenderer(r) );
        musicTree.init();
        cm.addCollectionManagerListener( musicTree );
        
        setLayout( new BorderLayout() );
        add(
            new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                tabbedPane,
                injector.getInstance( PlaylistManager.class )
            ),
            BorderLayout.CENTER
        );

    }

    /**
     *  returns the main panel with the music tree and playlists on
     * 
     *  @param musicTree
     *  @param playlistsPanel
     * 
     *  @return
     * 
     */
    
    protected JTabbedPane getTabbedPane( final MusicTree musicTree, final JSplitPane playlistsPanel ) {

        final JTabbedPane pane = new JTabbedPane();
        
        pane.addTab(
            locale.getString( "gui.label.tracks" ),
            new ImageIcon( r.getImage("icons/16x16/tracks.png") ),
            new JScrollPane( musicTree ),
            "Tracks in the collection"
        );

        pane.addTab(
            locale.getString( "gui.label.playlists" ),
            new ImageIcon( r.getImage("icons/16x16/playlists.png") ),
            new JScrollPane( playlistsPanel ),
            "Your playlists"
        );
        
        pane.setMinimumSize( new Dimension(170,100) );

        return pane;
        
    }
    
    /**
     *  returns the panel to display site playlists, and some actions like
     *  importing them from disk.
     * 
     *  @return JPanel
     * 
     */
    
    protected JPanel getSitePlaylistsPanel( final SitePlaylists sitePlaylists, final ActionListener importPlaylistAction ) {

        final JPanel buttons = new JPanel( new FlowLayout(FlowLayout.LEFT) );
        final JButton importPlaylist = new JButton(
            locale.getString("gui.label.import"),
            new ImageIcon( r.getImage("icons/16x16/import.png") )
        );
        importPlaylist.addActionListener( importPlaylistAction );
        
        buttons.add( importPlaylist );
        
        final JPanel panel = new JPanel( new BorderLayout() );
        panel.add( new JScrollPane(sitePlaylists), BorderLayout.CENTER );
        panel.add( buttons, BorderLayout.SOUTH );
        
        return panel;

    }

    /**
     *  returns the panel used to display user created playlists
     * 
     *  @return JPanel
     * 
     */
    
    protected JPanel getUserPlaylistsPanel( final UserPlaylists userPlaylists ){

        final JLabel userPlaylistsLabel = new JLabel(
            locale.getString("gui.label.userPlaylists"),
            new ImageIcon( r.getImage("icons/16x16/playlists.png") ),
            SwingConstants.LEFT
        );
        userPlaylistsLabel.setBorder(BorderFactory.createEmptyBorder( 4, 7, 4, 7));
        
        final JPanel panel = new JPanel( new BorderLayout() );
        panel.add( userPlaylistsLabel, BorderLayout.NORTH );
        panel.add( new JScrollPane(userPlaylists), BorderLayout.CENTER );

        return panel;
        
    }
    
}
