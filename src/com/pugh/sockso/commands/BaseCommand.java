
package com.pugh.sockso.commands;

abstract public class BaseCommand implements Command {

    public int getNumArgs() {
        
        return -1;
        
    }
    
    public int getMinArgs() {
        
        return -1;
        
    }
    
    public int getMaxArgs() {
        
        return -1;
        
    }

    public String[] getArguments() {

        return new String[] {};
        
    }

}
