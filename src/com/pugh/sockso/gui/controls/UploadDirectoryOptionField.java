/*
 * UploadDirectoryOptionField.java
 * 
 * Created on Nov 25, 2007, 2:54:00 PM
 * 
 * This is a special version of the directory chooser which allows setting
 * the uploads directory
 *
 */

package com.pugh.sockso.gui.controls;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Utils;
import com.pugh.sockso.Properties;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.music.CollectionManager;

import javax.swing.JFrame;

import java.awt.event.ActionEvent;

import java.io.File;

import org.apache.log4j.Logger;

public class UploadDirectoryOptionField extends DirectoryOptionField {

    private static final Logger log = Logger.getLogger( UploadDirectoryOptionField.class );
    
    private Database db;
    private Properties p;
    private CollectionManager cm;
    
    public UploadDirectoryOptionField( JFrame parent, Properties p, String name, Locale locale, Database db, CollectionManager cm ) {
        
        super( parent, p, name, locale );
        
        this.p = p;
        this.db = db;
        this.cm = cm;
        
        setPath( Utils.getUploadsPath(db,p) );
        
    }
    
    @Override
    public void actionPerformed( ActionEvent evt ) {

        File folder = null;
        
        if ( (folder = chooseFolder()) != null ) {
            
            final String newPath = folder.getAbsolutePath();
            final String oldPath = Utils.getUploadsPath( db, p );

            // do we have something new?
            if ( !oldPath.equals(newPath) ) {
                
                // if there's an existing folder set then we need to remove it
                // from the collection
                if ( !oldPath.equals("") )
                    cm.removeDirectory( oldPath );

                // set collection id in properties
                final File uploadsDir = new File( newPath );
                final int newCollectionId = cm.addDirectory( uploadsDir );

                p.set( "uploads.collectionId", newCollectionId );
                p.save();

                setPath( newPath );

            }
            
        }

    }
    
}
