
package com.pugh.sockso.gui.action;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.resources.Locale;

import java.awt.event.ActionEvent;

import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

public class RequestLogClear extends RequestLogAction {

    private static final Logger log = Logger.getLogger( RequestLogClear.class );
    
    private final JFrame parent;
    
    private final Database db;
    
    private final Locale locale;
    
    public RequestLogClear( final JFrame parent, final Database db, final Locale locale ) {
        
        this.parent = parent;
        this.db = db;
        this.locale = locale;
        
    }
    
    public void actionPerformed( ActionEvent evt ) {

        if ( confirmClearLog() ) {
            
            try {
                clearRequestLog();
                JOptionPane.showMessageDialog(
                    parent,
                    locale.getString("gui.message.requestLogCleared"),
                    "Sockso",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
                    
            catch ( SQLException e ) {
                JOptionPane.showMessageDialog(
                    parent,
                    e.getMessage(),
                    "Sockso",
                    JOptionPane.ERROR_MESSAGE
                );
                log.error( e );
            }
            
        }

    }

    protected void clearRequestLog() throws SQLException {

        final String sql = " delete from request_log ";
        
        db.update( sql );

        fireRequestLogChanged();
        
    }
    
    protected boolean confirmClearLog() {
        
        final int result = JOptionPane.showConfirmDialog(
            parent,
            locale.getString("gui.message.confirmClearRequestLog"),
            "Sockso",
            JOptionPane.YES_NO_OPTION
        );

        return ( result == JOptionPane.OK_OPTION );
        
    }
    
}
