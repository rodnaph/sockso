
package com.pugh.sockso.web.action.admin;

import com.pugh.sockso.commands.CommandExecuter;
import com.pugh.sockso.music.CollectionManager;
import com.pugh.sockso.templates.web.admin.TConsole;
import com.pugh.sockso.web.action.AdminAction;

import java.io.IOException;

import java.sql.SQLException;

import com.google.inject.Inject;

/**
 *  Shows the web admin console
 *
 */

public class Console extends AdminAction {

    private final CollectionManager cm;
    
    @Inject
    public Console( final CollectionManager cm ) {
        
        this.cm = cm;
        
    }

    /**
     *  Handles a request, either processing a command, or displaying the console
     *
     *  @throws IOException
     *  @throws SQLException
     *
     */
    
    public void handleAdminRequest() throws Exception {

        final String command = getRequest().getUrlParam( 2 );

        if ( command.equals("send") ) {
            processCommand();
        }

        else {
            showConsole();
        }
        
    }

    /**
     *  Dispatches a command that has been sent via the request and sends
     *  the output to the response
     *
     */

    protected void processCommand() throws Exception {

        final String command = getRequest().getArgument( "command" );
        final CommandExecuter cmd = new CommandExecuter( getDatabase(), getProperties(), cm, getLocale() );
        final String output = cmd.execute( command );
        
        getResponse().showText( output );

    }

    /**
     *  Shows the admin console
     * 
     *  @throws IOException
     *
     */

    protected void showConsole() throws IOException {
        
        final TConsole tpl = new TConsole();

        tpl.setProperties( getProperties() );
        tpl.setLocale( getLocale() );
        tpl.setUser( getUser() );
        
        getResponse().showHtml( tpl.makeRenderer() );

    }

}
