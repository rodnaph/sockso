
package com.pugh.sockso.web.action;

import com.pugh.sockso.Properties;
import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.Response;
import com.pugh.sockso.web.User;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import java.util.Vector;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

public abstract class BaseAction implements WebAction {
    
    private static final Logger log = Logger.getLogger( BaseAction.class );
    
    private Request req;
    private Response res;
    private User user;
    private Locale locale;
    private Database db;
    private Properties p;
    
    /**
     *  Indicates if this web action requires a login to be executed.  If it does
     *  not require a login then a session doesn't need to be started.
     * 
     *  @return boolean true if login required, false otherwise
     * 
     */
    
    public boolean requiresLogin() {

        return true;

    }

    /**
     *  Some getters for the standard objects
     * 
     */
    
    protected Request getRequest() { return req; }
    protected Response getResponse() { return res; }
    protected User getUser() { return user; }
    protected Locale getLocale() { return locale; }
    protected Database getDatabase() { return db; }
    protected Properties getProperties() { return p; }

    /**
     *  And setters (used for DI during testing)
     * 
     */
    
    @Override
    @Inject
    public void setDatabase( final Database db ) {
        
        this.db = db;
    
    }
    
    @Override
    @Inject
    public void setProperties( final Properties p ) {
        
        this.p = p;
    
    }
    
    @Override
    public void setUser( final User user ) { this.user = user; }
    @Override
    public void setResponse( final Response res ) { this.res = res; }
    @Override
    public void setRequest( final Request req ) { this.req = req; }
    @Override
    public void setLocale( final Locale locale ) { this.locale = locale; }
    
    /**
     *  fetches the latest tracks (limited by limit, obv...)
     * 
     *  @param int limit
     * 
     *  @return
     * 
     *  @throws java.sql.SQLException
     *
     *  @todo move to Track class
     * 
     */
    
    protected Vector<Track> getLatestTracks( final int limit ) throws SQLException {
        
        ResultSet rs = null;
        PreparedStatement st = null;
        
        try {
            
            // latest tracks
            String sql = Track.getSelectFromSql() +
                    " order by t.date_added desc " +
                    " limit ? ";
            st = db.prepare( sql );
            
            st.setInt( 1, limit );
            rs = st.executeQuery();
            
            return Track.createVectorFromResultSet( rs );
        
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
    }

}
