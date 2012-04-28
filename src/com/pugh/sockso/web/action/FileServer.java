
package com.pugh.sockso.web.action;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.pugh.sockso.Utils;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.Response;

import com.google.inject.Inject;

public class FileServer extends BaseAction {

    private static final Logger log = Logger.getLogger( FileServer.class );

    private final Resources r;

    @Inject
    public FileServer( final Resources r ) {
        this.r = r;
    }
    
    /**
     *  handles the "file" command, which is a request for a resource from
     *  the applications htdocs folder
     * 
     *  @throws IOException
     *  @throws BadRequestException
     *  @throws SQLException
     * 
     */
    
    public void handleRequest() throws IOException, BadRequestException, SQLException {

        final Request req = getRequest();
        final Response res = getResponse();
        final String type = req.getUrlParam( 1 );

        res.setCookiesEnabled( false );

        serveFile();

    }

    /**
     *  this action doesn't require a login as we still need to serve
     *  images and css and stuff when the user isn't logged in
     * 
     *  @return false
     * 
     */
    
    @Override
    public boolean requiresLogin() {

        return false;

    }

    /**
     *  no login required at all so no need to start a session
     * 
     *  @return
     * 
     */
    
    @Override
    public boolean requiresSession() {

        return false;

    }
    
    /**
     *  serves a request file to the client
     * 
     *  @throws IOException
     *  @throws BadRequestException
     * 
     */
    
    public void serveFile() throws IOException, BadRequestException {

        serveResource( getPathFromRequest() );

    }

    /**
     *  Returns the file path from the request
     * 
     *  @return 
     * 
     */

    protected String getPathFromRequest() {

        final Request req = getRequest();
        final StringBuffer path = new StringBuffer( "htdocs" );
        
        for ( int i=1; i<req.getParamCount(); i++ ) {
            path.append( "/" );
            path.append( req.getUrlParam(i) );
        }
        
        return path.toString()
                   .replace( "..", "" );

    }

    /**
     *  serves the resource to the client
     * 
     *  @param path path to the resource
     * 
     *  @throws IOException
     *  @throws BadRequestException
     * 
     */

    private void serveResource( final String path ) throws IOException, BadRequestException {

        DataInputStream in = null;
        final Locale locale = getLocale();

        // only gzip for certain file types
        final String ext = Utils.getExt( path ).toLowerCase();
        for ( final String gzipExt : new String[] { "css", "js" } )
            if ( gzipExt.equals(ext) )
                getResponse().enableGzip();

        try {
            
            in = new DataInputStream( r.getResourceAsStream(path) );
            
            sendHeaders( path );
            getResponse().sendData( in );
            
        }

        catch ( final FileNotFoundException e ) {
            throw new BadRequestException( locale.getString("www.error.fileNotFound"), 404 );
        }
        
        finally {
            Utils.close( in );
        }

    }
    
    /**
     *  send the headers for serving a resource, just need to give the name of
     *  the file we're serving to work out content types and stuff
     * 
     *  @param filename
     * 
     */
    
    protected void sendHeaders( final String filename ) {

        FileHeaders fh = new FileHeaders(
            getResponse(),
            getProperties()
        );

        fh.sendHeaders( filename );

    }

}
