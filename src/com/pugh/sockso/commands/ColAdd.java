
package com.pugh.sockso.commands;

import com.pugh.sockso.music.CollectionManager;
import com.pugh.sockso.resources.Locale;

import java.io.File;

public class ColAdd extends BaseCommand {

    private final Locale locale;
    
    private final CollectionManager cm;

    public ColAdd( final CollectionManager cm, final Locale locale ) {

        this.cm = cm;
        this.locale = locale;
        
    }

    public String getName() {
        
        return "coladd";
        
    }
    
    public String getDescription() {
        
        return "Adds a folder to the collection";
        
    }
    
    /**
     *  handles the CMD_COLADD command, adds a directory
     *  to the collection
     *
     *  @param args command arguments
     *
     */

    public String execute( final String[] args ) {

        final String path = args[ 1 ];
        final File file = new File( path );

        if ( file.exists() ) {
            cm.addDirectory( file );
            return locale.getString("con.msg.directoryAdded");
        }
        else {
            return locale.getString("con.err.pathNotExist",new String[] {path});
        }

    }

    @Override
    public int getNumArgs() {

        return 1;

    }

    @Override
    public String[] getArguments() {

        return new String[] { "PATH" };
        
    }

}
