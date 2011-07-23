/**
 * ServerThread.java
 *
 * Created on May 8, 2007, 12:31 PM
 *
 */

package com.pugh.sockso.web;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.web.action.Errorer;
import com.pugh.sockso.web.action.BaseAction;
import com.pugh.sockso.web.log.DbRequestLogger;
import com.pugh.sockso.web.log.RequestLogger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.Socket;
import java.net.SocketException;

import org.apache.log4j.Logger;

public class ServerThread extends Thread {

    private static final Logger log = Logger.getLogger( ServerThread.class );

    private final Socket client;
    private final Server sv;
    private final Database db;
    private final Properties p;
    private final Resources r;
    private final Dispatcher dispatcher;

    /**
     *  Creates a new instance of ServerThread
     *
     *  @param server the server this thread is attached to
     *  @param client the client socket
     *  @param db the database connection
     *  @param p app properties
     *  @param r app resources
     *
     */
    
    public ServerThread( final Server server, final Socket client, final Database db,
                         final Properties p, final Resources r, final Dispatcher dispatcher ) {

        this.sv = server;
        this.client = client;
        this.db = db;
        this.p = p;
        this.r = r;
        this.dispatcher = dispatcher;
                
    }
    
    /**
     *  the service, handles the request
     *
     */
    
    @Override
    public void run() {

        Response res = null;
        Request req = null;
        User user = null;
        Locale locale = r.getCurrentLocale();
        
        try {

            req = new HttpRequest( sv );
            req.process( new BufferedInputStream(client.getInputStream()) );

            locale = r.getLocale( req.getPreferredLangCode() );

            final Session session = new Session( db, req, null );
            user = session.getCurrentUser();

            res = new HttpResponse(
                new BufferedOutputStream(client.getOutputStream()),
                db, p, locale, user,
                req.getHeader("Accept-Encoding").contains("gzip")
                    // @TODO fix safari gzip bug
                    && !req.getHeader("User-Agent").contains("Safari")
            );

            process( user, req, locale, res );

        }
        
        // client has probably disconnected, ignore
        catch ( final SocketException e ) {}

        // ignore unknown IE6 behaviour
        catch ( final EmptyRequestException e ) {}
        
        // "handled" exception from Sockso
        catch ( final BadRequestException e ) {
            showException( e, req, res, false );
        }

        // something bad like IO, SQL, etc...
        catch ( final Exception e ) {
            // create our own (exception for the template) but clone the stack trace...
            final BadRequestException exception = new BadRequestException( e.getMessage(), 500 );
            exception.setStackTrace( e.getStackTrace() );
            showException( exception, req, res, true );
        }

        finally {
            // we're done with the request so we can close the
            // connection to the client now.
            try { client.close(); }
            catch ( Exception e ) {}
        }
        
        sv.requestComplete( this );

    }

    /**
     *  Process a request and generate a response
     *
     *  @param user
     *  @param req
     *  @param locale
     *  @param res
     *
     *  @throws Exception
     *
     */

    protected void process( final User user, final Request req, final Locale locale, final Response res ) throws Exception {

        if ( p.get(Constants.WWW_LOG_REQUESTS_ENABLED).equals(p.YES) ) {
            final RequestLogger logger = new DbRequestLogger( db );
            logger.log( user, client.getInetAddress().getHostAddress(),
                req.getResource(), req.getHeader("User-Agent"),
                req.getHeader("Referer"), req.getHeader("Cookie") );
        }
        
        final BaseAction action = dispatcher.getAction( req );
        action.init( req, res, user, locale );

        if ( action == null ) {
            throw new BadRequestException(locale.getString("www.error.unknownRequest"), 400);
        }

        if ( loginRequired(user,action) ) {
            res.redirect( p.getUrl("/user/login"));
        }

        else {
            action.handleRequest();
        }

    }

    /**
     *  Indicates if the user needs to log in before running this action
     *
     *  @param user
     *  @param action
     *
     *  @return
     *
     */

    protected boolean loginRequired( final User user, final BaseAction action ) {

        return p.get( Constants.WWW_USERS_REQUIRE_LOGIN ).equals( p.YES )
            && user == null
            && action.requiresLogin();

    }

    /**
     *  Shows an error, and sends the status code in the response
     *
     *  @param e
     *  @param req
     *  @param res
     *  @param showStackTrace
     * 
     */
    
    private void showException( final BadRequestException e, final Request req, final Response res, final boolean showStackTrace ) {

        log.error( e.getMessage() );
        
        if ( showStackTrace ) {
            e.printStackTrace();
        }

        final Errorer err = new Errorer( e, showStackTrace );
        err.setRequest( req );
        err.setResponse( res );

        try { err.handleRequest(); }
            catch ( final Exception e2 ) { /* an error showing the error message, ummm.. */ }

    }
    
    /**
     *  tries to shut down the thread cleanly
     * 
     */
    
    public void shutdown() {
        
        log.info( "Shutting Down" );
    
        try { finalize(); }
            catch ( Throwable t ) {}
        
    }


}
