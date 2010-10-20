/*
 * NumberFieldOption.java
 * 
 * Created on Aug 19, 2007, 3:46:59 PM
 * 
 * Only allows the input of numbers
 * 
 */

package com.pugh.sockso.gui.controls;

import com.pugh.sockso.Properties;

import java.text.DecimalFormat;

import javax.swing.text.NumberFormatter;

public class NumberOptionField extends TextOptionField {

    public NumberOptionField( Properties p, String name ) {
        super( p, name, getNumberFormatter() );
    }
    
    private static NumberFormatter getNumberFormatter() {
        
        DecimalFormat format = new DecimalFormat( "#####" );
        NumberFormatter f = new NumberFormatter( format );
        
        f.setAllowsInvalid( false );
        
        return f;
        
    }

}
