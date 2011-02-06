
package com.pugh.sockso.commands;

import com.pugh.sockso.Sockso;

public class Version extends BaseCommand {

    public String getName() {
        
        return "version";
        
    }
    
    public String getDescription() {
        
        return "Show version information";
        
    }
    
    /**
     *  prints version information
     *
     */

    public String execute( final String[] args ) {

        return "Sockso " +Sockso.VERSION;

    }

}
