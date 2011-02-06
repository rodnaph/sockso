
package com.pugh.sockso.commands;

import com.pugh.sockso.music.CollectionManager;

public class ColScan extends BaseCommand {

    private final CollectionManager cm;
    
    public ColScan( final CollectionManager cm ) {
        
        this.cm = cm;
        
    }

    public String getName() {
        
        return "colscan";
        
    }
    
    public String getDescription() {
        
        return "Start a collection scan";
        
    }

    /**
     *  handles command to scan the collection now
     *
     */

    public String execute( final String[] args ) {

        cm.checkCollection();

        return "Scanning collection...";

    }


}
