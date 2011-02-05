
package com.pugh.sockso.commands;

public class UserList implements Command {

    public String getName() {
        
        return "userlist";
        
    }
    
    public String getDescription() {
        
        return "Lists the users";
        
    }
    
    public String execute( final String[] args ) {
        
        return null;
        
    }

}
