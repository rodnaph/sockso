
package com.pugh.sockso.commands;

import com.pugh.sockso.music.CollectionManager;
import com.pugh.sockso.resources.Locale;

public class ColDel extends BaseCommand {

    private final Locale locale;

    private final CollectionManager cm;

    public ColDel( final CollectionManager cm, final Locale locale ) {

        this.cm = cm;
        this.locale = locale;

    }

    public String getName() {
        
        return "coldel";
        
    }
    
    public String getDescription() {
        
        return "Removes a folder from the collection";
        
    }

    public String execute( final String[] args ) {

        final String path = args[ 1 ];

        return cm.removeDirectory( path )
            ? locale.getString( "con.msg.directoryDeleted" )
            : locale.getString( "con.err.directoryNotInColl" );

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
