
package com.pugh.sockso.commands;

import com.pugh.sockso.Main;

public class Exit extends BaseCommand {

    public String getName() {

        return "exit";

    }

    public String getDescription() {

        return "Exit Sockso";

    }
    
    /**
     *  handles the CMD_EXIT command
     *
     */

    public String execute( final String[] args ) {

        Main.exit();

        return "Exiting...";

    }

}
