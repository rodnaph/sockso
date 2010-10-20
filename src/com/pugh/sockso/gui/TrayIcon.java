/*
 * TrayIcon.java
 * 
 * Created on Jun 10, 2007, 8:32:40 PM
 *
 * Tries to create a tray icon, and handle the difference between
 * systems.
 * 
 */

package com.pugh.sockso.gui;

import com.pugh.sockso.resources.Resources;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.jdesktop.jdic.tray.SystemTray;

import snoozesoft.systray4j.SysTrayMenu;
import snoozesoft.systray4j.SysTrayMenuIcon;
import snoozesoft.systray4j.SysTrayMenuListener;
import snoozesoft.systray4j.SysTrayMenuEvent;

import org.apache.log4j.Logger;

public class TrayIcon implements ActionListener, SysTrayMenuListener  {
    
    private static Logger log = Logger.getLogger( TrayIcon.class );
    
    private final AppFrame af;
    private final Resources r;

    private org.jdesktop.jdic.tray.TrayIcon allTray;
    private SysTrayMenuIcon winTray = null;
    private boolean isActive = false;

    /**
     *  constructor, creates a new tray icon based on the platform we're on.
     *  it tries to swallow up any errors that could possibly occur, so you need
     *  to check afterward if it worked via the isActive() method
     *
     *  @param af the main window to control
     * 
     */

    public TrayIcon( final AppFrame af, final Resources r ) {

        this.af = af;
        this.r = r;
        
    }
    
    public void init() {
        
        try {

            // windows
            if( System.getProperty("os.name").toLowerCase().indexOf("windows") != -1 ) {
                log.info( "Creating Windows Tray Icon" );
                winTray = new SysTrayMenuIcon( "icons/tray" );
                winTray.addSysTrayMenuListener( this );
                new SysTrayMenu( winTray, "Sockso" );
            }

            // everything else
            else {
                log.info( "Creating JDIC Tray Icon" );
		final ImageIcon icon = new ImageIcon( r.getImage("icons/tray.png") );
                allTray = new org.jdesktop.jdic.tray.TrayIcon( icon, "Sockso", null );
                allTray.addActionListener( this );
                SystemTray.getDefaultSystemTray().addTrayIcon( allTray );
            }
            
            isActive = true;
            
        }

        // if we log errors from NoClassDefFoundError there's an infinite loop...  puzzle.
        catch ( NoClassDefFoundError e ) {}
        catch ( UnsatisfiedLinkError e ) { e.printStackTrace(); log.error(e.getMessage()); }
        catch ( Exception e ) { e.printStackTrace(); log.error(e.getMessage()); }

    }

    /**
     *  indicates if the tray icon was created ok and is running
     * 
     *  @return true of icon running, false otherwise
     * 
     */
    
    public boolean isActive() {
        
        return isActive;
        
    }
    
    /**
     *  the tray icon (linux) has been clicked
     *
     *  @param evt the action event
     *
     */
    
    public void actionPerformed( ActionEvent evt ) {
        trayIconClicked();
    }
    
    /**
     *  the tray icon (all) has been clicked
     *
     */
    
    private void trayIconClicked() {
        af.toggleVisibility();
    }

    /**
     *  the tray icon (windows) has been clicked
     *
     *  @param evt the tray click event
     * 
     */

    public void iconLeftClicked( SysTrayMenuEvent evt ) {
        trayIconClicked();
    }

    public void iconLeftDoubleClicked( SysTrayMenuEvent e ) {}
    public void menuItemSelected( SysTrayMenuEvent e ) {}

}
