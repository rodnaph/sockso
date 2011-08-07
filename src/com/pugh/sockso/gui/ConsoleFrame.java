
package com.pugh.sockso.gui;

import com.pugh.sockso.commands.CommandExecuter;
import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.resources.Locale;

import java.awt.Font;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

/**
 * A window for interacting with the Sockso console from the GUI
 * 
 */
public class ConsoleFrame extends JFrame {

    private static final Logger log = Logger.getLogger( ConsoleFrame.class );
    
    private static final int MAX_OUTPUT_LENGTH = 5000;

    private final CommandExecuter cmd;
    private final Resources r;
    private final Locale locale;

    private JTextField inputField;
    protected JTextArea outputArea;
    private JButton commandButton, hideButton;
    
    @Inject
    public ConsoleFrame( final CommandExecuter cmd, final Resources r, final Locale locale ) {
        
        super( locale.getString("gui.window.console") );

        this.cmd = cmd;
        this.r = r;
        this.locale = locale;
        
        createComponents();
        layoutComponents();
        
    }
    
    /**
     *   creates the components for the GUI and set up actions, etc...
     * 
     */
    
    private void createComponents() {
        
        inputField = new JTextField();
        inputField.addActionListener( getConsoleCommandAction() );
        outputArea = new JTextArea();
        outputArea.setEditable( false );
        outputArea.setFont(new Font( "Courier", Font.PLAIN, 12 ));
        
        commandButton = new JButton( locale.getString("gui.label.consoleCommandGo"), new ImageIcon(r.getImage("icons/22x22/execute.png")) );
        commandButton.addActionListener( getConsoleCommandAction() );
        
        hideButton = new JButton( locale.getString("gui.label.hide"), new ImageIcon(r.getImage("icons/22x22/hide.png")) );
        hideButton.addActionListener( getHideButtonAction() );
        
    }
    
    /**
     *  lays out the GUI's components
     * 
     */
    
    private void layoutComponents() {
        
        JPanel buttonPanel = new JPanel( new FlowLayout() );
        buttonPanel.add( commandButton );
        buttonPanel.add( hideButton );
        
        JPanel commandPanel = new JPanel();
        commandPanel.setLayout( new BorderLayout() );
        commandPanel.add( inputField, BorderLayout.CENTER );
        commandPanel.add( buttonPanel, BorderLayout.EAST );
        
        Container c = getContentPane();
        c.setLayout( new BorderLayout() );
        c.add( new JScrollPane(outputArea), BorderLayout.CENTER );
        c.add( commandPanel, BorderLayout.SOUTH );

    }
    
    /**
     *  returns the action to use when executing a command
     * 
     *  @return
     * 
     */
    
    private ActionListener getConsoleCommandAction() {
        return new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                
                try {

                    // constrain the amount of text displayed in the outputArea,
                    // don't want it getting too massive.
                    final String newText = outputArea.getText() + cmd.execute( inputField.getText() ) + "\n\n";
                    final int len = newText.length();
                    final int start = len > MAX_OUTPUT_LENGTH ? len - MAX_OUTPUT_LENGTH : 0;

                    outputArea.setText( newText.substring(start,len) + "\n" );

                    inputField.setText( "" );
                    
                }
                
                catch ( Exception e ) {
                    log.error( e );
                }

            }
        };
    }
    
    /**
     *  returns the action handler for the hide button
     * 
     *  @return
     * 
     */
    
    private ActionListener getHideButtonAction() {
        return new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                setVisible( false );
            }
        };
    }
    
}

/**
 *  the input stream of data for the console to read from, HOWEVER, this is
 *  unused as we just access the consoles dispatchCommand() method directly
 * 
 */

class ConsoleInputStream extends InputStream {
    
    @Override
    public int read() {
        return -1;
    }
    
}

/**
 *  class to read data that comes back from the console.  it will then write
 *  this data into the main text area for the user to see
 * 
 */

class ConsoleOutputStream extends OutputStream {

    private final ConsoleFrame frame;
    
    public ConsoleOutputStream( ConsoleFrame frame ) {
        this.frame = frame;
    }
    
    public void write( int i ) {        
        
        StringBuffer sb = new StringBuffer( frame.outputArea.getText() );
        
        sb.append( (char) i );
        
        frame.outputArea.setText( sb.toString() );
        
    }

}
