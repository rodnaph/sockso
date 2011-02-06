
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
     *  Returns the names of the command arguments
     *
     *  @return
     *
     */
    
    public String[] getArguments();

    /**
     *  Execute the command and return the result
     *
     *  @param args
     *
     *  @return
     *
     *  @throws Exception
     *
     */

    public String execute( final String[] args ) throws Exception;

    /**
     *  Returns the exact number of args this command requires, or -1
     *
     *  @return
     *
     */

    public int getNumArgs();

    /**
     *  Returns the minimum number of args this command requires, or -1
     *
     *  @return
     *
     */

    public int getMinArgs();

    /**
     *  Returns the maximum number of args this command requires, or -1
     *
     *  @return
     *
     */

    public int getMaxArgs();

}
