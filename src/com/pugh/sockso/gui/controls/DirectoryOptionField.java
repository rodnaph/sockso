/*
 * DirectoryOptionField.java
 * 
 * Created on Nov 23, 2007, 7:25:27 PM
 * 
 * Implements an option field for selecting a directory
 *
 */

package com.pugh.sockso.gui.controls;

import com.pugh.sockso.Properties;
import com.pugh.sockso.resources.Locale;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JFileChooser;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.io.File;

public class DirectoryOptionField extends JPanel implements ActionListener {
   
    private Properties p;
    private String name;
    
    private JFrame parent;
    private JButton browse;
    private JTextField field;
    
    public DirectoryOptionField( JFrame parent, Properties p, String name, Locale locale ) {
        
        this.parent = parent;
        this.p = p;
        this.name = name;
        
        initComponents( locale );
        initLayout();
        
    }
    
    private void initComponents( Locale locale ) {
        
        browse = new JButton( locale.getString("gui.label.browse") );
        browse.addActionListener( this );
        
        field = new JTextField();
        field.setEditable( false );

        setPath( p.get(name) );

    }
    
    private void initLayout() {
        
        setLayout( new BorderLayout() );
        add( field, BorderLayout.CENTER );
        add( browse, BorderLayout.EAST );
        
    }
    
    /**
     *  sets the path in the text field
     * 
     *  @param path the path to set
     * 
     */
    
    protected void setPath( String path ) {
        
        field.setText( path );
        
    }
    
    /**
     *  the browse button has been clicked
     * 
     *  @param evt the button click event
     * 
     */
    
    public void actionPerformed( ActionEvent evt ) {
        
        File folder = null;
        
        if ( (folder = chooseFolder()) != null ) {
            
            String path = folder.getAbsolutePath();
            
            p.set( name, path );
            p.save();

            setPath( path );
            
        }

    }
    
    /**
     *  shows a dialog to choose a folder, if nothing is selected or cancel is
     *  clicked then null is returned, otherwise you'll get the folder
     * 
     *  @return folder selected, or null
     * 
     */
    
    protected File chooseFolder() {

        JFileChooser chooser = new JFileChooser();
        
        chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        
        return chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION
            ? chooser.getSelectedFile()
            : null;

    }
    
}
