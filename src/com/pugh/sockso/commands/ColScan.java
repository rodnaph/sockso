
package com.pugh.sockso.commands;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Collection;
import com.pugh.sockso.music.CollectionManager;

import java.io.File;

import java.sql.SQLException;

public class ColScan extends BaseCommand {

    private final CollectionManager cm;

    private final Database db;
    
    public ColScan( final CollectionManager cm, final Database db ) {
        
        this.cm = cm;
        this.db = db;
        
    }

    public String getName() {
        
        return "colscan";
        
    }
    
    public String getDescription() {
        
        return "Start a collection scan";
        
    }

    public String[] getArguments() {

        return new String[] { "DIR (optional)" };

    }

    /**
     *  handles command to scan the collection now
     *
     */

    public String execute( final String[] args ) throws SQLException {

        if ( args.length == 2 ) {
            return scanDirectory( args[1] );
        }
        else {
            cm.checkCollection();
            return "Scanning collection...";
        }

    }

    /**
     *  Resolves a collection from the path and scans the folder
     *
     *  @param path
     *
     *  @throws SQLException
     *
     */

    protected String scanDirectory( final String path ) throws SQLException {

        final Collection collection = Collection.findByPath( db, path );

        if ( collection != null ) {
            cm.scanDirectory( collection.getId(), new File(path) );
            return "Scanning folder...";
        }

        else {
            return "Invalid directory";
        }

    }

}
