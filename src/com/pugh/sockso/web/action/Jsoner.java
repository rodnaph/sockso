
package com.pugh.sockso.web.action;

import com.pugh.sockso.cache.CacheException;
import com.pugh.sockso.cache.ObjectCache;
import com.pugh.sockso.Constants;
import com.pugh.sockso.web.*;
import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.music.CollectionManager;
import com.pugh.sockso.music.MusicSearch;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.music.Artist;
import com.pugh.sockso.templates.json.TSearch;
import com.pugh.sockso.templates.json.TString;
import com.pugh.sockso.templates.json.TFolders;
import com.pugh.sockso.templates.json.TResolvePath;
import com.pugh.sockso.templates.json.TTracks;
import com.pugh.sockso.templates.json.TTracksForPath;
import com.pugh.sockso.templates.json.TSimilarArtists;
import com.pugh.sockso.templates.json.TServerInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

public class Jsoner extends BaseAction {

    private static final Logger log = Logger.getLogger( Jsoner.class );
    
    private final CollectionManager cm;

    private final ObjectCache cache;

    @Inject
    public Jsoner( final CollectionManager cm, final ObjectCache cache ) {
        
        this.cm = cm;
        this.cache = cache;
        
    }

    /**
     *  handles the json command which generates json documents
     * 
     *  @param res the response object
     *  @param args the command arguments
     *  @param user current user
     * 
     *  @throws SQLException
     *  @throws IOException
     *  @throws BadRequestException
     * 
     */
    
    public void handleRequest() throws SQLException, IOException, BadRequestException, CacheException {
        
        final Request req = getRequest();
        final String type = req.getUrlParam( 1 );
        
        if ( type.equals("search") )
            search();
        else if ( type.equals("savePlaylist") )
            savePlaylist();
        else if ( type.equals("deletePlaylist") )
            deletePlaylist();
        else if ( type.equals("folder") )
            folder();
        else if ( type.equals("resolvePath") )
            resolvePath();
        else if ( type.equals("tracksForPath") )
            tracksForPath();
        else if ( type.equals("similarArtists") )
            similarArtists();
        else if ( type.equals("tracks") )
            tracks();
        else if ( type.equals("serverinfo") )
            serverinfo();

        else throw new BadRequestException( "Unknown json request (" + type + ")", 400 );

    }

    /**
     *  given some url arguments (ar123, al456, etc...) queries for the tracks
     *  that belong to these items
     * 
     *  @throws java.io.IOException
     *  @throws java.sql.SQLException
     *  @throws com.pugh.sockso.web.BadRequestException
     * 
     */
    
    protected void tracks() throws IOException, SQLException, BadRequestException {
    
        final Request req = getRequest();
        final List<Track> tracks = Track.getTracksFromPlayArgs( getDatabase(), req.getPlayParams(true) );
        
        showTracks( tracks );
        
    }

    /**
     *  shows the tracks specified
     * 
     *  @param tracks
     * 
     *  @throws java.io.IOException
     * 
     */
    
    protected void showTracks( final List<Track> tracks ) throws IOException {

        final TTracks tpl = new TTracks();
        
        tpl.setTracks( tracks );
        
        getResponse().showJson( tpl.makeRenderer() );

    }
    
    /**
     *  queries audioscrobbler for artists similar to the one specified, and then
     *  check against our artists to see which ones we have which are similar
     * 
     *  @throws BadRequestException
     *  @throws SQLException
     *  @throws IOException
     * 
     */
    
    protected void similarArtists() throws BadRequestException, SQLException, IOException, CacheException {
        
        final AudioScrobbler scrobbler = new AudioScrobbler( getDatabase(), cache );
        final RelatedArtists related = new RelatedArtists( getDatabase(), scrobbler );
        final Request req = getRequest();
        final int artistId = Integer.parseInt( req.getUrlParam(2) );
        
        showSimilarArtists( related.getRelatedArtistsFor(artistId) );

    }

    /**
     *  shows the specified artists
     * 
     *  @param artists
     * 
     */
    
    protected void showSimilarArtists( final Artist[] artists ) throws IOException {
        
        final TSimilarArtists tpl = new TSimilarArtists();
        
        tpl.setArtists( artists );
        
        getResponse().showJson( tpl.makeRenderer() );

    }
    
    /**
     *  tries to delete a users playlist.  needs to check things like did they
     *  create it, etc...  if all goes ok then sends back the ID so that the
     *  javascript handler can do whatever...
     * 
     *  @throws BadRequestException
     *  @throws SQLException
     *  @throws IOException
     * 
     */
    
    protected void deletePlaylist() throws BadRequestException, SQLException, IOException {

        final Request req = getRequest();
        final User user = getUser();
        final Locale locale = getLocale();

        if ( user == null ) throw new BadRequestException( locale.getString("www.json.error.notLoggedIn"), 403 );

        final Database db = getDatabase();
        final int id = Integer.parseInt( req.getUrlParam(2) );
        final String sql = " select 1 " +
                           " from playlists p " +
                           " where p.id = ? " +
                               " and p.user_id = ? ";

        ResultSet rs = null;
        PreparedStatement st = null;
        
        try {

            // check user owns playlist before deleting it
            st = db.prepare( sql );
            st.setInt( 1, id );
            st.setInt( 2, user.getId() );
            rs = st.executeQuery();
            if ( !rs.next() )
                throw new BadRequestException( "You don't own that playlist", 403 );

            cm.removePlaylist( id );

            // done, send success response
            final TString tpl = new TString();
            tpl.setResult( Integer.toString(id) );
            getResponse().showJson( tpl.makeRenderer() );

        }

        finally {
            Utils.close( rs );
            Utils.close( st );
        }

    }
    
    /**
     *  this method outputs the tracks that lie below a given path
     * 
     *  @throws com.pugh.sockso.web.BadRequestException
     *  @throws java.sql.SQLException
     *  @throws java.io.IOException
     * 
     */
    
    protected void tracksForPath() throws BadRequestException, SQLException, IOException {

        Utils.checkFeatureEnabled( getProperties(), Constants.WWW_BROWSE_FOLDERS_ENABLED );

        ResultSet rs = null;
        PreparedStatement st = null;
        
        try {
        
            final Database db = getDatabase();
            final Request req = getRequest();
            final String path = req.getArgument( "path" );
            
            final TTracksForPath tpl = new TTracksForPath();
            tpl.setTracks( Track.getTracksFromPath(db,path) );
            getResponse().showJson( tpl.makeRenderer() );
            
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
    }
    
    /**
     *  this action allows you to give the path of a track on disk, and it will
     *  be resolved to the tracks internal ID.  this can then be used normally
     *  for playing music.  the path coming in is assumed to have forward slashes
     *  to delimit path components, but this needs to be converted to whatever
     *  the actual path separator is for the current system BEFORE we try and
     *  query the database, otherwise, well, it just won't work.
     * 
     *  NB!  ATM, this feature is only here for the the folder browsing stuff,
     *  so if that's not turned on this this won't work.
     * 
     * 
     */
    
    protected void resolvePath() throws BadRequestException, SQLException, IOException {
       
        // check folder browsing is enabled
        Utils.checkFeatureEnabled( getProperties(), "browse.folders.enabled" );
        
        ResultSet rs = null;
        PreparedStatement st = null;

        try {
            
            final Database db = getDatabase();
            final Locale locale = getLocale();
            final Request req = getRequest();
            final String path = convertPath( req.getArgument("path") );
            final String sql = Track.getSelectFromSql() +
                    " where t.path = ? ";
            
            st = db.prepare( sql );
            st.setString( 1, path );
            rs = st.executeQuery(); 
            
            if ( !rs.next() )
                throw new BadRequestException( locale.getString("www.error.trackNotFound"), 404 );
            
            final Track track = Track.createFromResultSet( rs );
            final TResolvePath tpl = new TResolvePath();
            
            tpl.setTrack( track );
            getResponse().showJson( tpl.makeRenderer() );
            
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
    }

    /**
     *  converts a path with / as the path separator to be correct for the
     *  current system.
     * 
     *  @param path
     *  @return
     * 
     */
    
    protected static String convertPath( final String path ) {
        
        final String separator = System.getProperty( "file.separator" );
        
        return path.replaceAll( "\\/", "\\" + separator );

    }
    
    /**
     *  this method extracts the full path in the request where the relative
     *  path after the json action name is prefixed by the collection path
     *  specified in the query string.
     * 
     *  e.g. /json/action/File/System/Path?collectionId=2
     * 
     *  Will return /home/rod/File/System/Path because the collection with id = 2
     *  is rooted at /home/rod
     * 
     *  @return String
     * 
     */
    
    private String getPathFromRequest() throws SQLException, BadRequestException {

        final Request req = getRequest();
        final Locale locale = getLocale();
        final int collectionId = Integer.parseInt( req.getArgument("collectionId") );

        String path = "";
        ResultSet rs = null;
        PreparedStatement st = null;

        for ( int i=2; i<req.getParamCount(); i++ ) {
            final String pathElement = req.getUrlParam( i );
            // don't allow going up directories
            if ( !pathElement.equals("..") )
                path += "/" + req.getUrlParam( i );
        }
                
        try {

            final Database db = getDatabase();
            final String sql = " select c.path " +
                               " from collection c " +
                               " where c.id = ? ";
            st = db.prepare( sql );
            st.setInt( 1, collectionId );
            rs = st.executeQuery();

            // check the collection exists and we got it's root path
            if ( rs.next() ) {
                // we need to trim the trailing slash off the collection path
                final String collPath = rs.getString( "path" );
                path = collPath.substring(0,collPath.length()-1) + path;
            }
            else
                throw new BadRequestException( locale.getString("www.error.invalidCollectionId"), 404 );

            path = path.replaceAll( "\\/\\/", "\\/" );

        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }

        log.debug( "pathFromRequest: " +path );
        
        return path;
        
    }
    
    /**
     *  if folder browsing is enabled, returns the contents of a folder, otherwise
     *  it'll throw a BadRequestException
     * 
     */
    
    protected void folder() throws BadRequestException, SQLException, IOException {

        // check folder browsing is enabled
        Utils.checkFeatureEnabled( getProperties(), "browse.folders.enabled" );
        
        final String path = getPathFromRequest();
        final File folder = new File( path );

        log.debug( "Path: " +path );

        // check the folder really exists on disk
        if ( !folder.exists() )
            throw new BadRequestException( "www.error.folderDoesntExist", 404 );

        final TFolders tpl = new TFolders();
        tpl.setFiles( getOrderedFiles(folder.listFiles()) );
        getResponse().showJson( tpl.makeRenderer() );
            
    }

    /**
     *  Takes an array of files, and returns another array sorted by ascending filename
     * 
     *  @param contents
     * 
     *  @return
     * 
     */

    protected File[] getOrderedFiles( final File[] contents ) {

        final File[] toSort = contents.clone();

        Arrays.<File>sort( toSort, new Comparator<File>() {
            public int compare( final File file1, final File file2 ) {
                return file1.getName().compareTo( file2.getName() );
            }
        });

        return toSort;
        
    }
    
    /**
     *  saves a playlist to the database for the current user.  outputs
     *  a single integer which is the playlist ID if all goes well, otherwise
     *  you'll get a description of the problem.
     * 
     *  @throws IOException
     * 
     */
    
    protected void savePlaylist() throws IOException, SQLException, BadRequestException {
        
        final Request req = getRequest();
        final User user = getUser();
        final Locale locale = getLocale();
        final String name = req.getUrlParam( 2 ).trim();
        final String[] args = req.getPlayParams( 2 );

        String result = locale.getString("www.json.error.unknown");

        // make sure data is ok first
        if ( name.equals("") )
            result = locale.getString("www.json.error.noName");
        else if ( args.length == 0 )
            result = locale.getString("www.json.error.noArguments");
        else if ( user == null )
            result = locale.getString("www.json.error.notLoggedIn");
        else {
            
            final Database db = getDatabase();
            final List<Track> vTracks = Track.getTracksFromPlayArgs( db, args );
            final Track[] tracks = new Track[ vTracks.size() ];
            
            for ( int i=0; i<vTracks.size(); i++ )
                tracks[i] = vTracks.get( i );
            
            result = Integer.toString(
                cm.savePlaylist( name, tracks, user )
            );
            
        }

        final TString tpl = new TString();
        tpl.setResult( result );
        getResponse().showJson( tpl.makeRenderer() );

    }
        
    /**
     *  performs a search on the music collection for the specified string and
     *  then creates a json results page
     * 
     *  @throws SQLException
     *  @throws IOException
     * 
     */
    
    protected void search() throws SQLException, IOException {
        
        final MusicSearch musicSearch = new MusicSearch( getDatabase() );
        
        final Request req = getRequest();
        final String query = req.getUrlParam( 2 );

        final TSearch tpl = new TSearch();
        tpl.setItems( musicSearch.search(query) );
        getResponse().showJson( tpl.makeRenderer() );

    }

    /**
     *  Returns information about this server (nothing secret)
     *
     */

    protected void serverinfo() throws IOException {

        final TServerInfo tpl = new TServerInfo();
        tpl.setProperties( getProperties() );

        getResponse().showJson( tpl.makeRenderer() );

    }

    /**
     *  Login is not required when requesting serverinfo
     *
     *  @return
     *
     */

    @Override
    public boolean requiresLogin() {

        return !getRequest().getUrlParam( 1 )
                            .equals( "serverinfo" );
        
    }
    
}
