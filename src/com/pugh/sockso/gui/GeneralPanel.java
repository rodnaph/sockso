
package com.pugh.sockso.gui;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Utils;
import com.pugh.sockso.Properties;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.db.DBExporter;
import com.pugh.sockso.gui.action.RequestLogClear;
import com.pugh.sockso.gui.action.RequestLogExport;
import com.pugh.sockso.gui.action.RequestLogChangeListener;
import com.pugh.sockso.gui.controls.UploadDirectoryOptionField;
import com.pugh.sockso.gui.controls.NumberOptionField;
import com.pugh.sockso.gui.controls.TextOptionField;
import com.pugh.sockso.gui.controls.BooleanOptionField;
import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.web.Server;
import com.pugh.sockso.web.action.Nater;
import com.pugh.sockso.music.CollectionManager;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.net.URL;
import java.net.HttpURLConnection;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.builder.DefaultFormBuilder;

import org.apache.log4j.Logger;

public class GeneralPanel extends JPanel implements RequestLogChangeListener {

    private static Logger log = Logger.getLogger( Logger.class );
    
    private final JFrame parent;
    private final Database db;
    private final Properties p;
    private final Resources r;
    private final Server sv;
    private final Locale locale;
    private final CollectionManager cm;
    
    private JComboBox exportRequestLogFormats;
    private JButton exportRequestLogButton, clearRequestLog;
    
    public GeneralPanel( JFrame parent, Database db, Properties p, Resources r, Server sv, CollectionManager cm ) {

        this.parent = parent;
        this.db = db;
        this.p = p;
        this.r = r;
        this.sv = sv;
        this.cm = cm;
        
        this.locale = r.getCurrentLocale();
        
        createComponents();
        layoutComponents();
        
    }
    
    /**
     *  creates the components that will be part of this panels interface
     * 
     */
    
    private void createComponents() {
        
        exportRequestLogFormats = new JComboBox( DBExporter.Format.values() );
        
        RequestLogExport exportAction = new RequestLogExport( parent, db, locale, exportRequestLogFormats );
        exportAction.addListener( this );
        
        exportRequestLogButton = new JButton(
            locale.getString("gui.label.export"),
            new ImageIcon( r.getImage("icons/16x16/export.png") )
        );
        exportRequestLogButton.addActionListener( exportAction );

        clearRequestLog = new JButton( getClearRequestLogButtonText(), new ImageIcon(r.getImage("icons/16x16/remove.png")) );
        clearRequestLog.addActionListener(
            new RequestLogClear( parent, db, locale )
        );

    }

    /**
     *  Handler for when the request log has changed
     * 
     */
    public void requestLogChanged() {

        clearRequestLog.setText( getClearRequestLogButtonText() );

    }
    
    private String getClearRequestLogButtonText() {
        
        return locale.getString(
            "gui.label.clearRequestLog", new String[] { getRequestLogSize() }
        );
        
    }
    
    /**
     *  tries to fetch the number of records in the request log
     * 
     *  @return
     * 
     */
    
    private String getRequestLogSize() {
        
        ResultSet rs = null;
        PreparedStatement st = null;
        
        try {
            
            final String sql = " select count(*) as total " +
                                " from request_log ";
            
            st = db.prepare( sql );
            rs = st.executeQuery();
            
            if ( rs.next() )
                return rs.getString( "total" );
            
        }
        
        catch ( SQLException e ) {
            log.error( e );
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
        return "unknown";
        
    }
    
    /**
     *  lays out the components on this panel
     * 
     */
    
    private void layoutComponents() {
        
        FormLayout layout = new FormLayout(
            " right:max(40dlu;pref), 3dlu, 150dlu, 7dlu "
        );
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setDefaultDialogBorder();
  
        builder.appendSeparator( locale.getString("gui.label.community") );
        builder.append( locale.getString("gui.label.communityEnabled"), new BooleanOptionField(p,Constants.COMMUNITY_ENABLED) );
        builder.nextLine();
        
        builder.appendSeparator( locale.getString("gui.label.webServer") );
        builder.append( locale.getString("gui.label.port"), new NumberOptionField(p,"server.port") );
        builder.nextLine();
        builder.append( locale.getString("gui.label.basepath"), new TextOptionField(p,"server.basepath") );
        builder.nextLine();
        builder.append( locale.getString("gui.label.wwwTitle"), new TextOptionField(p,"www.title") );
        builder.nextLine();
        builder.append( locale.getString("gui.label.wwwTagline"), new TextOptionField(p,"www.tagline") );
        builder.nextLine();
        builder.append( locale.getString("gui.label.disableDownloads"), new BooleanOptionField(p,"www.disableDownloads") );
        builder.nextLine();
        builder.append( locale.getString("gui.label.enableFolderBrowsing"), new BooleanOptionField(p,"browse.folders.enabled") );

        final String scheduler = p.get( Constants.SCHED, "" );

        if ( !scheduler.equals("cron") && !scheduler.equals("manual") ) {
            builder.appendSeparator( locale.getString("gui.label.collection") );
            builder.append( locale.getString("gui.label.scanOnStartup"), new BooleanOptionField(p,"collman.scan.onStart") );
            builder.nextLine();
            builder.append( locale.getString("gui.label.scanInterval"), new NumberOptionField(p,Constants.SCHED_SIMPLE_INTERVAL) );
            builder.nextLine();
        }

        builder.appendSeparator( locale.getString("gui.label.uploads") );
        builder.append( locale.getString("gui.label.enableUploads"), new BooleanOptionField(p,"uploads.enabled") );
        builder.nextLine();
        builder.append( locale.getString("gui.label.anonymousUploads"), new BooleanOptionField(p,"uploads.allowAnonymous") );
        builder.nextLine();
        builder.append( locale.getString("gui.label.uploadsDirectory"), new UploadDirectoryOptionField(parent,p,"uploads.collectionId",locale,db,cm) );
        builder.nextLine();

        builder.appendSeparator( locale.getString("gui.label.general") );
        builder.append( locale.getString("gui.label.startMinimized"), new BooleanOptionField(p,"app.startMinimized") );
        builder.nextLine();
        builder.append( locale.getString("gui.label.confirmExit"), new BooleanOptionField(p,"app.confirmExit") );
        builder.nextLine();

        builder.appendSeparator( locale.getString("gui.label.logging") );
        builder.append( locale.getString("gui.label.enableRequestLog"), new BooleanOptionField(p,"log.requests.enable") );
        builder.nextLine();
        builder.append( locale.getString("gui.label.exportRequestLogAs"), exportRequestLogFormats  );
        builder.nextLine();
        builder.append( "", exportRequestLogButton  );
        builder.nextLine();
        builder.append( "", clearRequestLog );
        builder.nextLine();
        
        JButton testNat = new JButton( locale.getString("gui.label.testNat") );
        testNat.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                doNatTest();
            } 
        });
        
        JButton forwardPort = new JButton( locale.getString("gui.label.portForward"), new ImageIcon(r.getImage("icons/22x22/port_forward.png")) );
        forwardPort.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                showForwardPortDialog();
            }
        });
        
        builder.appendSeparator( locale.getString("gui.label.natSetup") );
        builder.append( "", testNat );
        builder.nextLine();
        builder.append( "", forwardPort );

        setLayout( new BorderLayout() );
        add( new JScrollPane(builder.getPanel()), BorderLayout.CENTER );
        
    }
    
    private void showForwardPortDialog() {
        
        new ForwardPortDialog( parent, sv, r );
        
    }
    
    /**
     *  tries to test the users NAT setup, will show a message
     *  to say if it went well or not
     * 
     */
    
    public void doNatTest() {
        
        String result = locale.getString( "gui.message.natTestFailed" ); // assume failure unless we get a good response
        int port = Integer.parseInt( p.get(Constants.SERVER_PORT) );
        BufferedReader in = null;
        
        try {

            String urlString = "http://sockso.pu-gh.com/nat/test/" + port;
            URL url = new URL( urlString );
            
            log.debug( "NAT Test URL: " + urlString );
            
            HttpURLConnection cnn = (HttpURLConnection) url.openConnection();

            cnn.setRequestMethod( "GET" );

            in = new BufferedReader (new InputStreamReader(cnn.getInputStream()) );
            String s = in.readLine();

            log.debug( "Nat Test Response: " + s );
            
            if ( s.equals( Nater.NAT_TEST_STRING ) )
                result = locale.getString("gui.message.natTestOk");

        }

        catch ( IOException e ) {
            log.error( e );
        }

        finally { Utils.close(in); }
        
        JOptionPane.showMessageDialog( parent, result );

    }
    
}
