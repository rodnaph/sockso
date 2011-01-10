
package com.pugh.sockso.web.action.admin;

import com.pugh.sockso.music.CollectionManager;
import com.pugh.sockso.templates.web.admin.TConsole;
import com.pugh.sockso.web.action.AdminAction;
import com.pugh.sockso.web.StringOutputStream;

import java.io.IOException;
import java.io.PrintStream;

import java.sql.SQLException;

/**
 *  Shows the web admin console
 *
 */

public class Console extends AdminAction {

    private final CollectionManager cm;
    
    public Console( final CollectionManager cm ) {
        
        this.cm = cm;
        
    }

    public void handleAdminRequest() throws IOException, SQLException {

        showConsole();
        
    }

    /**
     *  Dispatches a command that has been sent via the request and sends
     *  the output to the response
     *
     */

    protected void processCommand() throws SQLException, IOException {

        final String command = getRequest().getArgument( "command" );
        final StringOutputStream stream = new StringOutputStream();
        final PrintStream out = new PrintStream( stream );

        com.pugh.sockso.Console console = new com.pugh.sockso.Console(
            getDatabase(),
            getProperties(),
            cm,
            out,
            null,
            getLocale()
        );

        console.dispatchCommand( command );

        final String output = stream.toString();
        
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
