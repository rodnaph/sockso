/*
 * BooleanOptionField.java
 * 
 * Created on Aug 7, 2007, 7:51:00 PM
 * 
 * A checkbox property field
 * 
 */

package com.pugh.sockso.gui.controls;

import com.pugh.sockso.Properties;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

public class BooleanOptionField extends JCheckBox implements ActionListener {

    private Properties p;
    private String name;

    public BooleanOptionField( Properties p, String name ) {
        this.p = p;
        this.name = name;
        setSelected( p.get(name).equals(Properties.YES) );
        addActionListener( this );
    }

    public void actionPerformed( ActionEvent evt ) {
        p.set( name, isSelected() );
        p.save();
    }

}
