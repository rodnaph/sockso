/*
 * This panel has options for setting the encoding applications sockso can
 * use to change how music is streamed to the client
 * 
 */

package com.pugh.sockso.gui;


import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.builder.DefaultFormBuilder;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class EncodersPanel extends JPanel {

    @Inject
    public EncodersPanel( final Injector injector ) {
        
        final FormLayout layout = new FormLayout(
            " right:max(40dlu;pref), 3dlu, 150dlu, 7dlu "
        );
        
        final DefaultFormBuilder b = new DefaultFormBuilder( layout );
        b.setDefaultDialogBorder();

        for ( String format : new String[] { "mp3", "ogg", "wma", "flac", "m4a" } ) {
            
            final EncoderPanel panel = injector.getInstance( EncoderPanel.class );
            panel.init( format );
            
            b.appendSeparator( format );
            b.nextLine();
            b.append( panel );
            b.nextLine();
            
        }

        setLayout( new BorderLayout() );
        add( new JScrollPane(b.getPanel()), BorderLayout.CENTER );
        
    }
    
}
