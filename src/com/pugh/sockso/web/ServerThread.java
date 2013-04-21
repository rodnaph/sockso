
package com.pugh.sockso.web;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.resources.LocaleFactory;
import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.web.action.Errorer;
import com.pugh.sockso.web.action.WebAction;
import com.pugh.sockso.web.log.DbRequestLogger;
import com.pugh.sockso.web.log.RequestLogger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.Socket;
import java.net.SocketException;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

public class ServerThread extends Thread {

    private static final Logger log = Logger.getLogger( ServerThread.class );

    private final Server sv;
    private final Database db;
    private final Properties p;
    private final Resources r;
    private final Dispatcher dispatcher;
    private final LocaleFactory localeFactory;

    private Socket client;
    
    /**
     *  Creates a new instance of ServerThread
     *
     *  @param server the server this thread is attached to
     *  @param db the database connection
     *  @param p app properties
     *  @param r app resources
     *
     */
    
    @Inject
    public ServerThread( final Server server, final Database db, final Properties p,
                         final Resources r, final Dispatcher dispatcher, final LocaleFactory localeFactory ) {

        this.sv = server;
        this.db = db;
        this.p = p;
        this.r = r;
        this.dispatcher = dispatcher;
        this.localeFactory = localeFactory;
                
    }
    
    /**
     *  Sets the client socket to use to communicate with the caller
     * 
     *  @param client 
     * 
     */
    
    public void setClientSocket( final Socket client ) {
        
        this.client = client;
        
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
        
        try {

            req = new HttpRequest( sv );
            req.process( new BufferedInputStream(client.getInputStream()) );

            Locale locale = localeFactory.getLocale( req.getPreferredLangCode() );

            final WebAction action = dispatcher.getAction( req );
            action.setRequest( req );
            action.setLocale( locale );

            if ( action.requiresSession() ) {
                final Session session = new Session( db, req, null );
                user = session.getCurrentUser();
            }

            action.setUser( user );

            res = new HttpResponse(
                new BufferedOutputStream(client.getOutputStream()),
                db, p, locale, user,
                req.getHeader("Accept-Encoding").contains("gzip")
                    // @TODO fix safari gzip bug
                    && !req.getHeader("User-Agent").contains("Safari")
            );

            action.setResponse( res );

            process( action, user, req, locale, res );

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
     *  @param action
     *  @param user
     *  @param req
     *  @param locale
     *  @param res
     *
     *  @throws Exception
     *
     */

    protected void process( final WebAction action, final User user, final Request req, final Locale locale, final Response res ) throws Exception {

        if ( p.get(Constants.WWW_LOG_REQUESTS_ENABLED).equals(Properties.YES) ) {
            final RequestLogger logger = new DbRequestLogger( db );
            logger.log( user, client.getInetAddress().getHostAddress(),
                req.getResource(), req.getHeader("User-Agent"),
                req.getHeader("Referer"), req.getHeader("Cookie") );
        }
        
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

    protected boolean loginRequired( final User user, final WebAction action ) {

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
