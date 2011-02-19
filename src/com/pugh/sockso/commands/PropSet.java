
package com.pugh.sockso.commands;

import com.pugh.sockso.Properties;
import com.pugh.sockso.Utils;
import com.pugh.sockso.resources.Locale;

public class PropSet extends BaseCommand {

    private final Properties p;

    private final Locale locale;
    
    public PropSet( final Properties p, final Locale locale ) {
        
        this.p = p;
        this.locale = locale;
        
    }

    public String getName() {
        
        return "propset";
        
    }
    
    public String getDescription() {
        
        return "Sets a property";
        
    }

    /**
     *  handles the CMD_PROPSET command to set a
     *  particular application property
     *
     *  @param args command arguments
     *
     */

    public String execute( final String[] args ) {

        final String name = args[ 1 ];
        final String value = Utils.joinArray( args, " ", 2, args.length - 1 );

        p.set( name, value );
        p.save();

        return locale.getString( "con.msg.propertySaved" );

    }

    @Override
    public int getNumArgs() {

        return 2;

    }

    @Override
    public String[] getArguments() {

        return new String[] { "NAME", "VALUE" };
        
    }

}
