/*
 * Userer.java
 * 
 * Created on Aug 4, 2007, 10:31:57 AM
 * 
 * Handles dealing with registering users, and logging them
 * in and out of the system.
 * 
 */

package com.pugh.sockso.web.action;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Validater;
import com.pugh.sockso.web.*;
import com.pugh.sockso.Properties;
import com.pugh.sockso.Utils;
import com.pugh.sockso.auth.Authenticator;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.templates.web.users.TUserLogin;
import com.pugh.sockso.templates.web.users.TUserRegister;
import com.pugh.sockso.templates.web.users.TUserRegistered;
import com.pugh.sockso.templates.web.users.TUserEdit;
import com.pugh.sockso.templates.web.users.TUserUpdated;
import com.pugh.sockso.templates.web.users.TScrobbleLog;
import com.pugh.sockso.resources.Locale;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Userer extends BaseAction {
    
    private static final Logger log = Logger.getLogger( Userer.class );

    private final List<Authenticator> authenticators;

    /**
     *  Constructor
     *
     */
    
    public Userer() {
        
        this.authenticators = new ArrayList<Authenticator>();
                
    }

    /**
     *  handles the "user" command to manage user accounts
     * 
     *  @param req the request object
     *  @param res the response object
     * 
     */
    
    public void handleRequest() throws BadRequestException, IOException, SQLException {
       
        final Request req = getRequest();
        final String type = req.getUrlParam( 1 );
        
        if ( type.equals("register") )
            register();
        else if ( type.equals("login") )
            login();
        else if ( type.equals("logout") )
            logout();
        else if ( type.equals("edit") )
            edit();
        else if ( type.equals("update") )
            update();
        else if ( type.equals("scrobbleLog") )
            scrobbleLog();
        else
            throw new BadRequestException( "unknown command '" + type + "'", 400 );

    }

    /**
     * Adds an authenticator that can be used to log in users
     *
     * @param authenticator
     */
    public void addAuthenticator( Authenticator authenticator ) {

        authenticators.add( authenticator );

    }

    /**
     *  creates a scrobble log file for the tracks the user has listened to (but that
     *  haven't already been scrobbled)
     * 
     */
    
    protected void scrobbleLog() throws SQLException, IOException {

        final User user = getUser();
        final List<Track> tracks = getNonScrobbledTracks( user );

        markUsersTracksScrobbled( user );
        showScrobbleLog( tracks );

    }
    
    /**
     *  shows the template for the users scrobble log of unscrobbled tracks
     * 
     *  @param tracks
     * 
     *  @throws java.io.IOException
     * 
     */
    
    protected void showScrobbleLog( final List<Track> tracks ) throws IOException {
        
        final TScrobbleLog tpl = new TScrobbleLog();
        final Response res = getResponse();

        tpl.setTracks( tracks );
        
        res.addHeader( "Content-Disposition", "inline; filename=\".scrobbler.log\"" );
        res.showText( tpl.makeRenderer() );
        
    }
    
    /**
     *  marks all a users played tracks as having been scrobbled
     * 
     */
    
    protected void markUsersTracksScrobbled( final User user ) throws SQLException {
        
        PreparedStatement st = null;
        
        try {
        
            final Database db = getDatabase();
            final String sql = " update play_log " +
                               " set scrobbled = 1 " +
                               " where user_id = ? ";

            st = db.prepare( sql );
            st.setInt( 1, user.getId() );
            st.executeUpdate();

        }
        
        finally {
            Utils.close( st );
        }
        
    }
    
    /**
     *  returns the tracks the user has listened to that haven't been scrobbled yet
     * 
     *  @param user
     * 
     *  @return
     * 
     */
    
    protected List<Track> getNonScrobbledTracks( final User user ) throws SQLException {
        
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
            
            final Database db = getDatabase();
            // use the date the track was played instead of the date it
            // was added to the collection.
            final String sql = Utils.replaceAll( "t.date_added", "l.date_played", Track.getSelectSql() ) +
                              " from play_log l " +
                                  " inner join tracks t " +
                                  " on t.id = l.track_id " +
                                  " inner join artists ar " +
                                  " on ar.id = t.artist_id " +
                                  " inner join albums al " +
                                  " on al.id = t.album_id " +
                                  " inner join genres g " +
                                  " on g.id = t.genre_id " +
                              " where l.user_id = ? " +
                                  " and l.scrobbled = 0 ";

            st = db.prepare( sql );
            st.setInt( 1, user.getId() );
            rs = st.executeQuery();

            return Track.createListFromResultSet( rs );

        }

        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
    }
    
    /**
     *  updates a users details
     * 
     *  @throws java.io.IOException
     * 
     */
    
    private void update() throws IOException, BadRequestException, SQLException {
       
        requireLogin();        
        getUpdateSubmission().validate();        
        updateUser();
        showUserUpdated();

    }

    /**
     *  shows the page to inform the user their profile has been updated
     * 
     *  @throws java.io.IOException
     * 
     */
    
    protected void showUserUpdated() throws IOException, SQLException {
    
        final TUserUpdated tpl = new TUserUpdated();

        getResponse().showHtml( tpl );

    }
    
    /**
     *  returns the submission object for validating updating users
     * 
     *  @return
     * 
     */
    
    protected Submission getUpdateSubmission() {
        
        final Submission s = new Submission( getRequest(), getLocale() );
        
        s.addField( "email", Submission.FIELD_EMAIL, "www.error.invalidEmail" );
        s.addMatchingFields( "pass1", "pass2", "www.error.passwordsDontMatch" );
        
        return s;
        
    }
    
    /**
     *  updates the user with the submitted details
     * 
     */
    
    protected void updateUser() throws SQLException {
        
        PreparedStatement st = null;
        
        try {
            
            final User user = getUser();
            final Request req = getRequest();
            final Database db = getDatabase();
            final String sql = " update users " +
                               " set email = ?, " +
                                   " pass = ? " +
                               " where id = ? ";
            
            st = db.prepare( sql );
            st.setString( 1, req.getArgument("email") );
            st.setString( 2, Utils.md5(req.getArgument("pass1")) );
            st.setInt( 3, user.getId() );
            st.execute();
            
        }
        
        finally {
            Utils.close( st );
        }
        
    }
    
    /**
     *  makes sure the user is logged in, and redirects them to the login
     *  page if they're not
     * 
     *  @throws java.io.IOException
     * 
     */
    
    protected void requireLogin() throws IOException {
        
        if ( getUser() == null )
            getResponse().redirect( getProperties().getUrl("/user/login"));
        
    }
    
    /**
     *  shows the page with the users profile on it to edit
     * 
     *  @throws IOException
     * 
     */
    
    private void edit() throws IOException, SQLException {
       
        requireLogin();
        showUserEdit();
        
    }

    /**
     *  shows the form to edit the current user
     * 
     *  @throws java.io.IOException
     * 
     */
    
    protected void showUserEdit() throws IOException, SQLException {

        final TUserEdit tpl = new TUserEdit();

        getResponse().showHtml( tpl );

    }
    
    @Override
    public boolean requiresLogin() {
        return false;
    }
    
    public void register() throws IOException, BadRequestException, SQLException {

        final Properties p = getProperties();
        final Request req = getRequest();
        final User user = getUser();
        final Locale locale = getLocale();
        
        if ( p.get(Constants.WWW_USERS_DISABLE_REGISTRATION).equals(p.YES) )
            throw new BadRequestException( locale.getString("www.error.registrationDisabled"), 403 );
        if ( user != null )
            throw new BadRequestException( locale.getString("www.error.alreadyLoggedIn"), 403 );

        final String todo = req.getArgument( "todo" );
        
        // try and register the user?
        if ( todo.equals("register") )            
            registerUser();
        
        // just show the register form
        else showUserRegister();
        
    }

    /**
     *  shows the form for the user to register
     * 
     */
    
    protected void showUserRegister() throws IOException, SQLException {

        final TUserRegister tpl = new TUserRegister();
        
        getResponse().showHtml( tpl );
        
    }
    
    /**
     *  tries to register the user with the system, if all goes
     *  well they'll see a page confirming this
     * 
     *  @param req the request object
     *  @param res the response object
     * 
     *  @throws BadRequestException
     *  @throws SQLException
     *  @throws IOException
     * 
     */

    protected void registerUser() throws BadRequestException, SQLException, IOException {
    
        final Request req = getRequest();
        final Locale locale = getLocale();
        final Properties p = getProperties();
        
        final String name = req.getArgument( "name" ).trim();
        final String pass1 = req.getArgument( "pass1" );
        final String pass2 = req.getArgument( "pass2" );
        final String email = req.getArgument( "email" ).trim();
        
        final Database db = getDatabase();
        final Validater v = new Validater( db );
        
        if ( !v.checkRequiredFields(new String[]{name,pass1,email}) )
            throw new BadRequestException( locale.getString("www.error.missingField") );
        if ( !pass1.equals(pass2) )
            throw new BadRequestException( locale.getString("www.error.passwordsDontMatch") );
        if ( !v.isValidEmail(email) )
            throw new BadRequestException( locale.getString("www.error.invalidEmail") );
        if ( v.usernameExists(name) )
            throw new BadRequestException( locale.getString("www.error.duplicateUsername") );
        if ( v.emailExists(email) )
            throw new BadRequestException( locale.getString("www.error.duplicateEmail") );

        User newUser = new User( -1, name, pass1, email );
        newUser.setActive( !p.get(Constants.WWW_USERS_REQUIRE_ACTIVATION).equals(p.YES) );
        newUser.save( db );

        if ( newUser.isActive() ) {
            loginUser( name, pass1 );
        }

        showUserRegistered( newUser );

    }
    
    /**
     *  shows the page with confirmation the user has registered
     * 
     *  @param newUser
     * 
     *  @throws java.io.IOException
     * 
     */
    
    protected void showUserRegistered( final User newUser ) throws IOException, SQLException {
        
        final TUserRegistered tpl = new TUserRegistered();
        final Response res = getResponse();
        
        res.setUser( newUser );
        res.showHtml( tpl );
        
    }

    /**
     *  shows the login form initially, then tries to log the user
     *  in if the right data is submitted
     * 
     *  @param req the request object
     *  @param res the response object
     * 
     */
    
    public void login() throws IOException, SQLException, BadRequestException {
        
        final Request req = getRequest();
        final User user = getUser();
        final Locale locale = getLocale();
        
        if ( user != null ) {
            log.debug( "User appears logged in: " +user.getId()+ " = '" +user.getName()+ "'" );
            throw new BadRequestException( locale.getString("www.error.alreadyLoggedIn"), 403 );
        }

        final String todo = req.getArgument( "todo" );
        
        if ( todo.equals("login") )
            loginUser();
        
        else showUserLogin();

    }

    /**
     *  shows the page for users to log in
     * 
     *  @throws java.io.IOException
     * 
     */
    
    protected void showUserLogin() throws IOException, SQLException {

        final TUserLogin tpl = new TUserLogin();

        getResponse().showHtml( tpl );
        
    }
    
    /**
     *  tries to log the user in.  if all goes well then a session will be
     *  created and they'll be redirected to the home page
     * 
     *  @param req the request object
     *  @param res the response object
     * 
     */
    
    protected void loginUser() throws IOException, SQLException, BadRequestException {
        
        final Request req = getRequest();
        final Response res = getResponse();

        final String name = req.getArgument( "name" );
        final String pass = req.getArgument( "pass" );

        loginUser( name, pass );
        
        res.redirect( getProperties().getUrl("/"));

    }
    
    /**
     *  tried to log a user in and create a session for them.  if the user isn't
     *  valid then it'll throw a BadRequestException.  if all goes well then a
     *  User object will be returned for the user that was logged in.
     * 
     *  @param name
     *  @param pass
     * 
     *  @return user that was logged in
     * 
     *  @throws java.sql.SQLException
     *  @throws com.pugh.sockso.web.BadRequestException
     * 
     */
    
    protected void loginUser( final String name, final String pass ) throws SQLException, BadRequestException {

        log.debug( "Login user with '" +name+ "' identified by '" +pass+ "'" );

        for ( final Authenticator auth : authenticators ) {
            
            try {

                if ( auth.authenticate(name,pass) ) {

                    log.debug( "Authentication ok, creating session" );

                    final User user = findOrCreateUser( name, pass );
                    final Session sess = new Session(
                        getDatabase(),
                        getRequest(),
                        getResponse()
                    );

                    sess.create( user.getId() );

                    log.debug( "Session created!" );

                    return;

                }

            }

            catch ( final Exception e ) {
                log.debug( "Error authenticating: " +e.getMessage() );
                throw new BadRequestException( e.getMessage() );
            }
            
        }

        throw new BadRequestException(
            getLocale().getString( "www.error.loginFailed" )
        );
        
    }

    /**
     * Tries to find a user with the specified name, if they don't exist then
     * they are created with the specified password.  The user is then returned.
     *
     * @param name
     * @param pass
     *
     * @return
     *
     * @throws SQLException
     * 
     */
    
    public User findOrCreateUser( final String name, final String pass ) throws SQLException {
        
        ResultSet rs = null;
        PreparedStatement st = null;

        try {

            final Database db = getDatabase();
            final String sql = " select id, name " +
                               " from users " +
                               " where name = ? " +
                               " limit 1 ";

            st = db.prepare( sql );
            st.setString( 1, name );
            rs = st.executeQuery();

            if ( rs.next() ) {
                return new User( rs.getInt("id"), rs.getString("name") );
            }

            else {
                final User user = new User( name, pass, "", false );
                user.save( db );
                return user;
            }

        }

        finally {
            st.close();
            rs.close();
        }

    }

    /**
     *  logs a user out by setting the session cookies to expire now
     * 
     *  @param req the request object
     *  @param res the response object
     * 
     */
    
    public void logout() throws IOException {
        
        final Response res = getResponse();
        final Session sess = new Session( null, null, res );

        sess.destroy();

        res.redirect(getProperties().getUrl("/"));

    }

}
