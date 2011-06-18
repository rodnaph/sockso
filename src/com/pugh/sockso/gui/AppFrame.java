
package com.pugh.sockso.gui;

import com.pugh.sockso.Sockso;
import com.pugh.sockso.Constants;
import com.pugh.sockso.Manager;
import com.pugh.sockso.Properties;
import com.pugh.sockso.PropertiesListener;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.events.LatestVersionEvent;
import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.web.IpFinder;
import com.pugh.sockso.web.Server;
import com.pugh.sockso.music.CollectionManager;

import java.awt.Font;
import java.awt.Cursor;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.IllegalComponentStateException;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;

public class AppFrame extends JFrame implements PropertiesListener, Manager {
    
    private static final Logger log = Logger.getLogger( AppFrame.class );
    
    private final Database db;
    private final Properties p;
    private final CollectionManager cm;
    protected final Server sv;
    protected final Resources r;
    protected final IpFinder ipFinder;
    private TrayIcon tray = null;
    private JLabel urlLabel;
    private ConsoleFrame consoleFrame;
    
    /**
     *  Creates a new instance of AppFrame
     * 
     */
    
    public AppFrame( final Database db, final Properties p, final Server sv, final CollectionManager cm, final Resources r, final IpFinder ipFinder ) {

        super( r == null ? "" : r.getCurrentLocale().getString("gui.window.main") + " (" +Sockso.VERSION+ ")" );

        this.db = db;
        this.p = p;
        this.sv = sv;
        this.cm = cm;
        this.r = r;
        this.ipFinder = ipFinder;
        
        urlLabel = new JLabel();
        tray = new TrayIcon( this, r );

    }
    
    protected void initComponents() {

        // set application L&F
        log.debug( "Setting Look & Feel" );
        try {
            UIManager.setLookAndFeel( new Plastic3DLookAndFeel() );
        }
        catch ( Exception e ) {
            log.error( e.getMessage() );
        }

        log.debug( "Initialising TrayIcon" );

        tray.init();

        log.debug( "Initialising GUI components" );

    }
    
    /**
     *  lays out the GUI components in their right places
     * 
     */
    
    protected void layoutComponents() {
        
        log.debug( "Laying out GUI components" );
        
        setIconImage( r.getImage("icons/16x16/sockso.png") );
        setLayout( new BorderLayout() );
        add( getMainPane(), BorderLayout.CENTER );
        add( getBottomPane(), BorderLayout.SOUTH );

        addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing( final WindowEvent evt ) {
                if ( tray.isActive() ) toggleVisibility();
                    else exit();
            }
        });
        setSize( new Dimension(700,500) );
        setLocationRelativeTo( null );

        p.addPropertiesListener( this );

        // done with splash now
        Splash.close();

    }
    
    public void open() {

        initComponents();
        layoutComponents();

        // check to see if we should start minimized or not
        boolean setVisible = true;
        if ( tray.isActive() && p.get(Constants.APP_START_MINIMIZED).equals(Properties.YES) )
            setVisible = false;
        setVisible( setVisible );

    }
    
    /**
     *  checks with the user they want to do it, then closes the app
     * 
     */
    
    public void exit() {
        
        if ( p.get(Constants.APP_CONFIRM_EXIT).equals(Properties.YES) )
            if ( JOptionPane.showConfirmDialog( this, "Are you sure you want to exit?",
                    "Exit Sockso", JOptionPane.YES_NO_OPTION ) != JOptionPane.YES_OPTION )
                return;
        
        com.pugh.sockso.Main.exit();

    }
    
    /**
     *  toggles the visibility of the window
     * 
     */
    
    public void toggleVisibility() {

        setVisible( !isVisible() );

    }
    
    /**
     *  the panel across the bottom of the window
     *
     */
    
    private JPanel getBottomPane() {
        
        final Locale locale = r.getCurrentLocale();

        final JButton exit = new JButton( locale.getString("gui.label.exit"), new ImageIcon(r.getImage("icons/22x22/exit.png")) );
        exit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                AppFrame.this.exit();
            }
        });

        final JButton hide = new JButton( locale.getString("gui.label.hide"), new ImageIcon(r.getImage("icons/22x22/hide.png")) );
        hide.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                AppFrame.this.setVisible( false );
            }
        });

        final JButton console = new JButton( locale.getString("gui.label.showConsoleWindow"), new ImageIcon(r.getImage("icons/22x22/console.png")) );
        console.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                if ( consoleFrame == null ) {
                    consoleFrame = new ConsoleFrame( db, p, cm, r );
                    consoleFrame.setBounds( 100, 100, 600, 500 );
                }
                consoleFrame.setVisible( true );
            }
        });

        // in admin mode we won't have a server
        if ( sv != null ) {
            urlLabel.setText( locale.getString("gui.label.gettingIPAddress") );
            urlLabel.addMouseListener( new UrlLabelMouseAdapter(this,sv,r,ipFinder) );
            urlLabel.setToolTipText( "Click to open Sockso in your browser." );
            updateUrlLabel();
        }

        final JPanel buttons = new JPanel();
        buttons.setLayout( new FlowLayout(FlowLayout.RIGHT) );
        buttons.add( console );
        buttons.add( exit );
        if ( tray.isActive() )
            buttons.add( hide );

        final JPanel panel = new JPanel( new BorderLayout() );
        panel.add( buttons, BorderLayout.EAST );
        panel.add( urlLabel, BorderLayout.WEST );
        
        return panel;

    }
    
    /**
     *  updates the URL label to our current address
     *
     */
    
    protected void updateUrlLabel() {

        final String myurl = sv.getProtocol() +"://" + sv.getHost();
        final Locale locale = r.getCurrentLocale();
        
        urlLabel.setText( "<html><head><title></title></head><body>" +
                "&nbsp; " + locale.getString("gui.label.yourAddress") + ": <a href='" + myurl + "'>" + myurl + "</a>" +
                "</body></html>" );

    }
    
    /**
     *  returns the URL label
     * 
     *  @return
     * 
     */
    
    protected JLabel getUrlLabel() {
        
        return urlLabel;
        
    } 
    
    /**
     *  the main tab control
     *
     */
    
    private JTabbedPane getMainPane() {
       
        final JTabbedPane pane = new JTabbedPane();
        final Locale locale = r.getCurrentLocale();
        
        final MusicPanel musicPanel = new MusicPanel( this, db, cm, r );
        musicPanel.init();

        final UsersPanel usersPanel = new UsersPanel( this, db, p, r );
        usersPanel.init();
        
        pane.addTab( locale.getString("gui.label.music"), new ImageIcon(r.getImage("icons/22x22/music.png")),
            musicPanel, locale.getString("gui.tooltip.music") );
        
        pane.addTab( locale.getString("gui.label.collection"), new ImageIcon(r.getImage("icons/22x22/collection.png")),
            new CollectionPanel(this,db,cm,r,p), locale.getString("gui.tooltip.collection") );
        
        pane.addTab( locale.getString("gui.label.users"), new ImageIcon(r.getImage("icons/22x22/users.png")),
            usersPanel, locale.getString("gui.tooltip.users") );
        
        pane.addTab( locale.getString("gui.label.general"), new ImageIcon(r.getImage("icons/22x22/general.png")),
            new GeneralPanel(this,db,p,r,sv,cm), locale.getString("gui.tooltip.general") );

        pane.addTab( locale.getString("gui.label.encoders"), new ImageIcon(r.getImage("icons/22x22/encoders.png")),
            new EncodersPanel(this,p,r), locale.getString("gui.tooltip.encoders") );

        return pane;

    }
    
    /**
     *  closes the window and disposes of it
     *
     */
    
    public void close() {
        
        log.info( "Closing GUI" );
        setVisible( false );
        dispose();

    }
    
    /**
     *  properties have been saved
     * 
     *  @param p the new properties
     * 
     */
    
    public void propertiesSaved( Properties p ) {
        updateUrlLabel();
    }

    /**
     *  checks for a newer version
     * 
     */
    
    public void latestVersionReceived( final LatestVersionEvent evt ) {

        final String latestVersion = evt.getVersion();
        
        if ( latestVersion != null && !latestVersion.equals(Sockso.VERSION) ) {
            
            Splash.closeNow();

            final Locale locale = r.getCurrentLocale();
            final String message = locale.getString(
                "misc.msg.updateAvailable",
                new String[] { latestVersion }
            );
            
            JOptionPane.showMessageDialog( this, message, "Sockso", JOptionPane.INFORMATION_MESSAGE );
            
        }

    }
    
}

/**
 *  mouse adapter for the URL label
 * 
 */

class UrlLabelMouseAdapter extends MouseAdapter {
    
    private static final Logger log = Logger.getLogger( UrlLabelMouseAdapter.class );
    
    private final JFrame parent;
    private final Server sv;
    private final Resources r;
    private final JPopupMenu menu;
    private final IpFinder ipFinder;
    
    /**
     *  creates the mouse adapter
     *
     *  @param parent the frame the label is on
     *  
     */
    
    public UrlLabelMouseAdapter( final JFrame parent, final Server sv, final Resources r, final IpFinder ipFinder ) {

        this.parent = parent;
        this.sv = sv;
        this.r = r;
        this.ipFinder = ipFinder;
        
        this.menu = createPopupMenu();

    }

    /**
     *  mouse moved over the label
     * 
     *  @param evt the mouse event
     * 
     */
    
    @Override
    public void mouseEntered( final MouseEvent evt ) {
        parent.setCursor( new Cursor(Cursor.HAND_CURSOR) );
    }
    
    /**
     *  mouse moved off the label
     * 
     *  @param evt the mouse event
     * 
     */

    @Override
    public void mouseExited( final MouseEvent evt ) {
        parent.setCursor( new Cursor(Cursor.DEFAULT_CURSOR) );
    }
    
    /**
     *  mouse clicked, show popup
     * 
     *  @param evt the mouse event
     * 
     */
    
    @Override
    public void mouseClicked( final MouseEvent evt ) {

        // this error was thrown once, but i'm not sure why.  so we'll
        // just catch it and report it, the user can try clicking
        // again and it'll probably work.
        try {
            menu.show( (JLabel) evt.getSource(), evt.getX(), evt.getY() );
        }   
        catch ( final IllegalComponentStateException e ) {
            log.error( e );
        }

    }

    /**
     *  creates and returns a popup menu for this component
     *
     *  @return a popup menu
     *
     */

    private JPopupMenu createPopupMenu() {
        
        final JPopupMenu popup = new JPopupMenu();
        final Locale locale = r.getCurrentLocale();
        
        final JMenuItem inet = new JMenuItem( locale.getString("gui.label.internetAddress") );
        inet.setFont( inet.getFont().deriveFont(Font.BOLD) );
        inet.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                openUrl( sv.getProtocol() +"://" + sv.getHost() );
            }
        });

        final JMenuItem local = new JMenuItem( locale.getString("gui.label.myComputer") );
        local.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                openUrl( sv.getProtocol() +"://127.0.0.1:" + sv.getPort() );
            }
        });

        final JMenuItem refresh = new JMenuItem( locale.getString("gui.label.refresh"), new ImageIcon(r.getImage("icons/16x16/refresh.png")) );
        refresh.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                ipFinder.refresh();
                JOptionPane.showMessageDialog( parent, "IP Updated!" );
            }
        });

        popup.add( inet );
        popup.add( local );
        popup.addSeparator();
        popup.add( refresh );

        return popup;

    }
    
    /**
     *  open the URL in the users default browser (hopefully)
     * 
     *  @param url the url to open
     * 
     */
    
    private void openUrl( String url ) {

        String errMsg = "Error attempting to launch web browser: ";
        boolean error = true;

        log.debug( "Opening URL " + url );

        try {
            final BrowserLauncher launcher = new BrowserLauncher();
            launcher.openURLinBrowser( url );
            error = false;
        }
        
        catch ( UnsupportedOperatingSystemException e ) {
            log.error( e );
            errMsg += e.getLocalizedMessage();
        }
        
        catch ( BrowserLaunchingInitializingException e ) {
            log.error( e );
            errMsg += e.getLocalizedMessage();
        }

        if ( error )
            JOptionPane.showMessageDialog( null, errMsg );

    }    

}
