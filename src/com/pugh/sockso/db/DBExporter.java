/*
 *  Exports data in various formats from a database
 * 
 */

package com.pugh.sockso.db;

import com.pugh.sockso.Utils;
import com.pugh.sockso.templates.TXmlResultSet;
import com.pugh.sockso.templates.TCsvResultSet;
import com.pugh.sockso.templates.json.TResultSet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import org.jamon.Renderer;

public class DBExporter {

    private static final Logger log = Logger.getLogger( DBExporter.class  );
    
    private final Database db;

    public static enum Format {
        XML,
        JSON,
        CSV
    };
    
    /**
     *  creates an exporter object ready to export data from
     *  the specified database
     * 
     *  @param db
     * 
     */
    
    public DBExporter( final Database db ) {
        this.db = db;
    }

    /**
     *  runs a query on the database then returns the results formatted
     *  in the specified format
     * 
     *  @param sql
     *  @param format
     * 
     *  @return
     * 
     */
    
    public String export( final String sql, final Format format ) {
        
        String data = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
            
            st = db.prepare( sql );
            rs = st.executeQuery();
            
            Renderer renderer = null;
            
            // xml
            if ( format.equals(Format.XML) ) {
                final TXmlResultSet tpl = new TXmlResultSet();
                tpl.setResultSet( rs );
                renderer = tpl.makeRenderer();
            }

            // csv
            else if ( format.equals(Format.CSV) ) {
                final TCsvResultSet tpl = new TCsvResultSet();
                tpl.setResultSet( rs );
                renderer = tpl.makeRenderer();
            }
            
            // json
            else if ( format.equals(Format.JSON) ) {
                final TResultSet tpl = new TResultSet();
                tpl.setResultSet( rs );
                renderer = tpl.makeRenderer();
            }

            if ( renderer != null )
                return renderer.asString();

        }
        
        catch ( final Exception e ) {
            log.debug( e );
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
        return data;
        
    }
    
}
