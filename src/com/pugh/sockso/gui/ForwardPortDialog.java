/*
 * ForwardPortDialog.java
 * 
 * Created on Jun 25, 2007, 9:54:35 PM
 * 
 * A dialog for allowing the user to forward ports from UPNP enabled
 * internet gateway devices
 * 
 */

package com.pugh.sockso.gui;

import com.pugh.sockso.UPNP;
import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.web.Server;

import java.io.IOException;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JFrame;

import net.sbbi.upnp.impls.InternetGatewayDevice;

import org.apache.log4j.Logger;

public class ForwardPortDialog extends JDialog {

    private static Logger log = Logger.getLogger( ForwardPortDialog.class );
    
    private Server sv;
    private Resources r;
    private JButton nextButton = null;
    private JList deviceList = null;
    private DefaultListModel deviceListModel = null;
    private JLabel statusLabel = null;
    
    /**
     *  creates and shows the forward port wizard dialog modally
     * 
     *  @param parent the parent frame
     * 
     */
    
    public ForwardPortDialog( JFrame parent, Server sv, Resources r ) {
        
        super( parent, " Sockso - Internet Setup", true );

        this.sv = sv;
        this.r = r;

        // set up the dialog
        setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
        setBounds( 30,30, 400,300 );
        setLocationRelativeTo( null );
        setResizable( false );

        // setup layout
        setLayout( new BorderLayout() );
        add( getMainPane(), BorderLayout.CENTER );
        add( getButtonPane(), BorderLayout.SOUTH );
        //pack();

        // probe for devices (don't hold up GUI)
        new Thread() {
            public void run() { loadDevices(); }
        }.start();

        // show!
        setVisible( true );
        
    }

    /**
     *  does a UPNP query to fetch the routers on the network and
     *  loads them into the main list component.
     * 
     */
    
    private void loadDevices() {

        try {
            
            statusLabel.setText( "Probing for routers..." );
            
            InternetGatewayDevice[] devices = UPNP.getRouterDevices();
            
            if ( devices != null ) {
                for ( InternetGatewayDevice device : devices )
                    deviceListModel.addElement( device );
                statusLabel.setText( "Select a device" );
            }
            
            else statusLabel.setText( "Sorry, no devices found" );

        }
        
        catch ( IOException e ) {
            log.error( e.getMessage() );
        }

    }
    
    /**
     *  returns the main pane with the device list
     * 
     *  @return the panel
     * 
     */
    
    private JPanel getMainPane() {
        
        deviceListModel = new DefaultListModel();
        deviceList = new JList( deviceListModel );
        deviceList.setMinimumSize( new Dimension(400,300) );
        
        statusLabel = new JLabel( "Initializing..." );
        statusLabel.setBorder( new EmptyBorder(4,4,4,4) );
        
        JPanel p = new JPanel();
        p.setLayout( new BorderLayout() );
        p.add( new JScrollPane(deviceList), BorderLayout.CENTER );
        p.add( statusLabel, BorderLayout.SOUTH );
        
        return p;
        
    }
    
    /**
     *  the next button was clicked, see if a device has been selected, and if
     *  it has try to do some port forwarding.
     * 
     */
    
    private void nextClicked() {
        
        Object item = deviceList.getSelectedValue();
        
        if ( item != null ) {

            boolean success = false;
            
            try {
                InternetGatewayDevice device = (InternetGatewayDevice) item;
                success = UPNP.forwardPort( device, sv.getPort() );
            }
            catch ( Exception e ) {}
            
            JOptionPane.showMessageDialog( this,
                success ? "Port forwarded successfully!" : "Ack... it didn't seem to work..." );
            
            close();

        }
        
        else JOptionPane.showMessageDialog( this, "You need to select a device first" );

    }
    
    /**
     *  the cancel button was clicked, close the window
     * 
     */
    
    private void cancelClicked() {
        close();
    }
    
    /**
     *  close the window
     * 
     */
    
    private void close() {
        setVisible( false );
        dispose();
    }
    
    /**
     *  creates and returns the button panel
     * 
     *  @return the panel
     * 
     */
    
    private JPanel getButtonPane() {
        
        JButton cancel = new JButton( "Cancel", new ImageIcon(r.getImage("icons/22x22/cancel.png")) );
        cancel.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                ForwardPortDialog.this.cancelClicked();
            }
        });
        
        nextButton = new JButton( "Next", new ImageIcon(r.getImage("icons/22x22/ok.png")) );
        nextButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                ForwardPortDialog.this.nextClicked();
            }
        });

        JPanel p = new JPanel();
        p.setLayout( new FlowLayout(FlowLayout.RIGHT) );
        p.add( cancel );
        p.add( nextButton );
        return p;
        
    }
    
}
