
package com.pugh.sockso.gui.controls;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Collection;
import com.pugh.sockso.music.CollectionManager;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.resources.Resources;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;

/**
 * Button for handling rescanning the collection.  When clicked it prompts the
 * user to either scan the entire collection, or just a particular folder.
 * 
 */
public class RescanButton extends JButton implements ActionListener {
    
    private final static Logger log = Logger.getLogger( RescanButton.class );
    
    private final CollectionManager cm;
    
    private final Resources r;
    
    private final JFrame parentFrame;
    
    private final Database db;
    
    private JPopupMenu menu;
    
    /**
     *  Create a new rescan button
     * 
     *  @param r
     *  @param cm 
     * 
     */
    
    public RescanButton( final Resources r, final CollectionManager cm,
                         final JFrame parentFrame, final Database db ) {
        
        super(
            r.getCurrentLocale().getString("gui.label.rescanCollection"),
            new ImageIcon( r.getImage("icons/16x16/rescan.png") )
        );

        this.r = r;
        this.cm = cm;
        this.parentFrame = parentFrame;
        this.db = db;
        
    }
    
    /**
     *  Initialise the button
     * 
     */
    
    public void init() {

        addActionListener( this );
        
        initMenu();

    }
    
    /**
     *  Initialise the popup menu used to select what to scan
     * 
     */
    
    protected void initMenu() {

        final Locale locale = r.getCurrentLocale();
        
        menu = new JPopupMenu();
        
        final JMenuItem entireCollection = new JMenuItem( locale.getString("gui.label.scanEntireCollection") );
        entireCollection.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                scanEntireCollection();
            }
        });

        final JMenuItem selectFolder = new JMenuItem( locale.getString("gui.label.scanSelectFolder") );
        selectFolder.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                scanSelectFolder();
            }
        });

        menu.add( entireCollection );
        menu.add( selectFolder );

    }
    
    /**
     *  Button has been clicked, show menu for selecting the scan type
     * 
     *  @param evt 
     * 
     */
    
    public void actionPerformed( final ActionEvent evt ) {
        
        menu.show(
            (JButton) evt.getSource(),
            0,
            this.getHeight()
        );
        
    }
    
    /**
     *  Start a scan on the entire collection
     *
     */
    
    protected void scanEntireCollection() {

        new Thread() {
            
            @Override
            public void run() { cm.checkCollection(); }
            
        }.start();

    }
    
    /**
     *  Allow the user to select a folder to scan
     * 
     */
    
    protected void scanSelectFolder() {
        
        final File folderToScan = getFolderToScan();
        
        if ( folderToScan != null ) {

            try {
            
                final Collection collection = Collection.findByPath( db, folderToScan.getAbsolutePath() );
                
                if ( collection != null ) {
                    new Thread() {
                        @Override
                        public void run() {
                            cm.scanDirectory(
                                collection.getId(),
                                folderToScan
                            );
                        }
                    }.start();
                }
                
                else {
                    JOptionPane.showMessageDialog(
                        parentFrame,
                        r.getCurrentLocale().getString("gui.error.directoryNotInCollection"),
                        "Error scanning folder",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            
            }
            
            catch ( final SQLException e ) {
                log.debug( e.getMessage() );
            }
            
        }
        
    }
    
    /**
     *  Prompts the user to select a folder to scan and returns the one they
     *  select, or null if then cancel the dialog
     * 
     *  @return 
     * 
     */
    
    protected File getFolderToScan() {
        
        final JFileChooser chooser = new JFileChooser();
        
        chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        
        return chooser.showOpenDialog(parentFrame) == JFileChooser.APPROVE_OPTION
            ? chooser.getSelectedFile()
            : null;

    }

}
