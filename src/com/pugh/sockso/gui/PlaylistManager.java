
package com.pugh.sockso.gui;

import com.pugh.sockso.resources.Resources;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class PlaylistManager extends JPanel {

    private PlaylistPanel playlist;
    private Resources r;
    
    @Inject
    public PlaylistManager( final Injector injector, final Resources r  ) {

        this.r = r;
        
        playlist = injector.getInstance( PlaylistPanel.class );
        
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
