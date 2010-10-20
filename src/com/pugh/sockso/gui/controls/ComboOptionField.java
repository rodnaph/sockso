/*
 * Implements a combo box for a property
 * 
 */

package com.pugh.sockso.gui.controls;

import com.pugh.sockso.Properties;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JComboBox;

import org.apache.log4j.Logger;

public class ComboOptionField extends JComboBox implements ActionListener {

    private static final Logger log = Logger.getLogger( ComboOptionField.class );
    
    private final Properties p;
    private final String name;
    
    public ComboOptionField( Properties p, String name, Object items[] ) {

        super( items );
        
        this.p = p;
        this.name = name;
        
        // init the combo with the saved value
        String value = p.get( name );
        int length = getItemCount();
        
        for ( int i=0; i<length; i++ ) {
            Object item = getItemAt( i );
            if ( item != null && getItemSaveName(item).equals(value) )
                setSelectedItem( item );
        }

        addActionListener( this );
        
    }
    
    /**
     *  returns the name to use when saving an item (defaults to the items
     *  toString representation)
     * 
     *  @param item
     * 
     *  @return String
     * 
     */
    
    protected String getItemSaveName( Object item ) {
        
        return item.toString();
        
    }
    
    /**
     *  the combo selection has changed, save the new value
     * 
     *  @param evt
     * 
     */
    
    @Override
    public void actionPerformed( ActionEvent evt ) {

        Object item = getSelectedItem();
        
        if ( item != null ) {
            p.set( name, getItemSaveName(item) );
            p.save();
        }
        
    }
    
}
