/*
 * PlaylistPanel.java
 * 
 * Created on May 16, 2007, 11:12:31 PM
 * 
 * Allows playlists to be created and saved.
 *
 */

package com.pugh.sockso.gui;

import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.CollectionManager;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JFrame;

public class PlaylistManager extends JPanel {

    private PlaylistPanel playlist;
    private Resources r;
    
    public PlaylistManager(  JFrame parent, Database db, CollectionManager cm, Resources r  ) {

        this.r = r;
        
        playlist = new PlaylistPanel( parent, db, cm );
        
        setLayout( new BorderLayout() );
        add( getButtonPane(), BorderLayout.NORTH );
        add( new JScrollPane(playlist), BorderLayout.CENTER );
        
    }

    /**
     *  the panel with buttons for interacting with the playlist (saving/deleting)
     * 
     */
    
    private JPanel getButtonPane() {
        
        JButton clear = new JButton( "Clear", new ImageIcon(r.getImage("icons/16x16/clear.png")) );
        clear.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) { playlist.clear(); }
        });
        
        JButton save = new JButton( "Save Playlist", new ImageIcon(r.getImage("icons/16x16/save.png")) );
        save.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) { playlist.save(); }
        });

        JButton remove = new JButton( "Remove", new ImageIcon(r.getImage("icons/16x16/remove.png")) );
        remove.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) { playlist.removeSelection(); }
        });

        JPanel p = new JPanel();
        p.setLayout( new FlowLayout(FlowLayout.LEFT) );
        p.add( clear );
        p.add( remove );
        p.add( save );
        
        return p;
        
    }
    
}
