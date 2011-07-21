
package com.pugh.sockso.gui.action;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.gui.PlaylistFileFilter;
import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.music.CollectionManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import java.io.File;



import org.apache.log4j.Logger;

/**
 *  an action to import a playlist
 * 
 */

public class ImportPlaylist implements ActionListener {

    private static final Logger log = Logger.getLogger( ImportPlaylist.class );
    
    private final JFrame parent;
    private final Database db;
    private final CollectionManager cm;
    private final Resources r;
    
    public ImportPlaylist( final JFrame parent, final Database db, final CollectionManager cm, final Resources r ) {

        this.parent = parent;
        this.db = db;
        this.cm = cm;
        this.r = r;
        
    }

    public void actionPerformed( ActionEvent evt ) {

        String error = "";

        final JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter( new PlaylistFileFilter(r.getCurrentLocale()) );

        if ( fc.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION ) {

            try {

                final File file = fc.getSelectedFile();
                final int playlistId = cm.importPlaylist( file );
                final Locale locale = r.getCurrentLocale();

                if ( playlistId == -1 ) {
                    error = locale.getString("gui.message.playlistImportFailed");
                }
                else {
                    JOptionPane.showMessageDialog(
                        parent, locale.getString("gui.message.playlistImported"),
                        "Sockso", JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }

            }

            catch ( final Exception e ) {
                e.printStackTrace();
                log.error( e );
                error = e.getMessage();
            }

            JOptionPane.showMessageDialog(
                parent, error, "Sockso",
                JOptionPane.ERROR_MESSAGE
            );

        }

    }
    
}
