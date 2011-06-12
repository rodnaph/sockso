
package com.pugh.sockso.gui.action;

import com.pugh.sockso.db.DBExporter;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.resources.Locale;

import java.awt.event.ActionEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class RequestLogExport extends RequestLogAction {

    private final JFrame parent;
    
    private final Database db;
    
    private final Locale locale;
    
    private final JComboBox formats;
    
    public RequestLogExport( final JFrame parent, final Database db, final Locale locale,
                             final JComboBox formats ) {

        this.parent = parent;
        this.db = db;
        this.locale = locale;
        this.formats = formats;
        
    }
    
    /**
     *  Prompt user for file to export to, and then export
     * 
     *  @param evt 
     * 
     */
    
    public void actionPerformed( ActionEvent evt ) {

        final JFileChooser chooser = new JFileChooser();      
        final int result = chooser.showSaveDialog( parent );
        
        if ( result == JFileChooser.APPROVE_OPTION ) {
            
            try {
                exportRequestLog( chooser.getSelectedFile() );
                JOptionPane.showMessageDialog( parent, locale.getString("gui.message.exportComplete") );
            }
                    
            catch ( Exception e ) {
                JOptionPane.showMessageDialog( parent, e.getMessage() );
            }
            
        }
        
    }

    /**
     *  Export the request log to the specified file
     * 
     *  @param toFile
     * 
     *  @throws IOException 
     * 
     */
    
    protected void exportRequestLog( final File toFile ) throws IOException {
        
        final DBExporter exporter = new DBExporter( db );
        final DBExporter.Format format = (DBExporter.Format) formats.getSelectedItem();
        final String sql = " select * " +
                           " from request_log " +
                           " order by date_of_request desc ";

        final FileWriter writer = new FileWriter( toFile );
        final String data = exporter.export( sql, format );

        writer.write( data );
        writer.close();

        fireRequestLogChanged();

    }
    
}
