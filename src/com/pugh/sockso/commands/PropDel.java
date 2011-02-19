
package com.pugh.sockso.commands;

import com.pugh.sockso.Properties;
import com.pugh.sockso.resources.Locale;

public class PropDel extends BaseCommand {

    private final Properties p;
    
    private final Locale locale;
    
    public PropDel( final Properties p, final Locale locale ) {
        
        this.p = p;
        this.locale = locale;
        
    }

    public String getName() {
        
        return "propdel";
        
    }
    
    public String getDescription() {
        
        return "Deletes a property";
        
    }

    /**
     *  command to delete a property
     *
     *  @param args
     *
     */

    public String execute( final String[] args ) {

        final String propName = args[ 1 ];

        if ( p.exists(propName) ) {

            p.delete( propName );
            p.save();

            return locale.getString("con.msg.propertyDeleted");

        }

        else {
            return locale.getString("con.err.propertyDoesntExist");
        }

    }

    @Override
    public int getNumArgs() {

        return 1;

    }

    @Override
    public String[] getArguments() {

        return new String[] { "NAME" };

    }

}
