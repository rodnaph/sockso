
package com.pugh.sockso.gui;

import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.Constants;

import java.awt.BorderLayout;
import java.io.File;
import javax.swing.JWindow;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

public class Splash extends JWindow {

    private static Splash instance;

    private Splash( Resources r ) {

        setAlwaysOnTop( true );
        setSize( 400, 300 );
        setLocationRelativeTo( null );

        setLayout( new BorderLayout() );
        add( new JLabel(new ImageIcon(r.getImage(Constants.IMAGE_DIR + File.separator + "splash.png"))), BorderLayout.CENTER );

        setVisible( true );

    }

    /**
     *  creates and shows the splash screen
     *
     */

    public static void start( Resources r ) {

        instance = new Splash( r );

    }

    /**
     *  closes the splash screen after a short delay
     *
     */

    public static void close() {
        new Thread() {
            @Override
            public void run() {
                try { Thread.sleep( 2000 ); }
                catch ( InterruptedException e ) {}
                closeNow();
            }
        }.start();
    }

    /**
     *  Closes the splash window
     *
     */

    public static void closeNow() {

        instance.dispose();

    }

}
