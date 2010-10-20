/*
 * TextOptionField.java
 * 
 * Created on Aug 7, 2007, 7:49:58 PM
 * 
 * A string property field.
 * 
 */

package com.pugh.sockso.gui.controls;

import com.pugh.sockso.Properties;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import javax.swing.text.DefaultFormatter;

import javax.swing.JFormattedTextField;

public class TextOptionField extends JFormattedTextField implements KeyListener {

    private Thread thread;
    private Properties p;
    protected String name;
    
    public TextOptionField( Properties p, String name ) {
        this( p, name, null );
    }
    
    public TextOptionField( Properties p, String name, DefaultFormatter format ) {
        super( format );
        this.p = p;
        this.name = name;
        setText( p.get(name) );
        addKeyListener( this );
    }

    /**
     *  this method handles key events, then sets a timeout to
     *  save the state of the field.  if another event is received
     *  before the timeout has finished then a new timeout is set
     * 
     *  @param evt the key event
     * 
     */
    
    public void keyReleased( KeyEvent evt ) {
        if ( thread != null ) {
            thread.interrupt();
            thread = null;
        }
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep( 3000 );
                    p.set( name, getText() );
                    p.save();
                }
                catch ( InterruptedException e ) {}
            }
        };
        thread.start();
    }

    // used interface methods
    public void keyPressed( KeyEvent evt ) {}
    public void keyTyped( KeyEvent evt ) {}

}
