
package com.pugh.sockso.commands;

/**
 *  Interface for commands to implement.  Their names need to be unique or one
 *  command will override another
 *
 */
public interface Command {

    /**
     *  Returns the name of the command (eg. proplist)
     *
     *  @return
     *
     */

    public String getName();

    /**
     *  Returns the description of the command
     *
     *  @return
     *
     */

    public String getDescription();

    /**
     *  Execute the command and return the result
     *
     *  @param args
     *
     *  @return
     *
     */

    public String execute( final String[] args );

}
