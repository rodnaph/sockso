/*
 * Uploader.java
 * 
 * Created on Nov 7, 2007, 11:44:28 PM
 * 
 * Handles uploading of files.
 *
 */

package com.pugh.sockso.web.action;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Utils;
import com.pugh.sockso.Validater;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.CollectionManager;
import com.pugh.sockso.music.CollectionManagerListener;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.User;
import com.pugh.sockso.web.UploadFile;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.templates.web.TUpload;
import com.pugh.sockso.templates.web.TUploadComplete;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.Properties;

import java.io.File;
import java.io.IOException;

import java.sql.SQLException;

import org.apache.log4j.Logger;

public class Uploader extends BaseAction {

    private static final Logger log = Logger.getLogger( Uploader.class );
    
    private final CollectionManager cm;
    
    public Uploader( final CollectionManager cm ) {

        this.cm = cm;

    }
    
    /**
     *  handles requests for this controller
     * 
     */
    
    public void handleRequest() throws IOException, BadRequestException, SQLException {
        
        final Request req = getRequest();
        final String type = req.getUrlParam( 1 );

        checkPermissions();
        
        if ( type.equals("do") )
            uploadFile();
        else
            showUploadForm();
        
    }
    
    /**
     *  handles uploading and storing of a file from the user, then shows
     *  them a confirmation page when it's done
     * 
     *  @param req request object
     *  @param res response object
     *  @param user current user
     *  @param locale the selected locale
     * 
     */
    
    private void uploadFile() throws BadRequestException {

        checkUploadLooksOk();

        final Properties p = getProperties();
        final Request req = getRequest();
        final Locale locale = getLocale();
        final UploadFile file = req.getFile( "musicFile" );
        final String artist = req.getArgument( "artist" );
        final String album = req.getArgument( "album" );
        final String track = req.getArgument( "title" );

        // if we get here everything looks good, so lets try and save the file
        try {

            // make sure we've a directory to put the track in
            final Database db = getDatabase();
            final File dir = new File( Utils.getUploadsPath(db,p) + "/"+artist+" - "+album );
            if ( !dir.exists() )
                if ( !dir.mkdir() )
                    throw new BadRequestException( locale.getString("www.error.cantCreateTrackFolder"), 500 );

            // write the track to disk
            final File tempFile = file.getTemporaryFile();
            final File newFile = new File( getNewUploadFilename(dir,track,Utils.getExt(file.getFilename())) );

            tempFile.renameTo( newFile );

            // rescan the folder to add new track to collection
            final int collectionId = Integer.parseInt( p.get(Constants.WWW_UPLOADS_COLLECTION_ID) );
            cm.scanDirectory( collectionId, dir );
            cm.fireCollectionManagerEvent( CollectionManagerListener.UPDATE_COMPLETE, "Collection Updated!" );

            // show user confirmation
            final TUploadComplete tpl = new TUploadComplete();            
            tpl.setProperties( p );
            tpl.setUser( getUser() );
            tpl.setLocale( getLocale() );
            getResponse().showHtml( tpl.makeRenderer() );

        }

        catch ( IOException e ) {
            log.debug( e );
        }
        
    }
    
    /**
     *  does some error checking to see if the upload looks ok,  checks required
     *  arguments, etc...  if something is missing then throws an exception
     * 
     *  @throws com.pugh.sockso.web.BadRequestException
     * 
     */
    
    private void checkUploadLooksOk() throws BadRequestException {

        final Request req = getRequest();
        final Locale locale = getLocale();
        final UploadFile file = req.getFile( "musicFile" );

        if ( file == null )
            throw new BadRequestException( locale.getString("www.error.noFileUploaded") );

        // check mime type to see if it looks ok
        final String contentType = file.getContentType();
        log.debug( "File content type: " + contentType );
        if ( !isValidContentType(contentType) )
            throw new BadRequestException( locale.getString("www.error.unsupportedAudioFormat") );
        
        // check required fields
        final Database db = getDatabase();
        final Validater v = new Validater( db );
        final String artist = req.getArgument( "artist" );
        final String album = req.getArgument( "album" );
        final String track = req.getArgument( "title" );
        if ( !v.checkRequiredFields( new String[] { artist, album, track }) )
            throw new BadRequestException( locale.getString("www.error.missingField") );

    }
    
    /**
     *  this method tries to get an unused filename on disk that we can use
     *  to write the uploaded file to.  it tries appending 1,2,3, etc... onto
     *  the track name.  it'll try a maximum of 100 times then give up
     * 
     *  @param dir the directory for the file
     *  @param track the track title of the file
     *  @param ext the file's extension
     * 
     *  @return absolute path if we can get one, exception otherwise
     * 
     *  @throws com.pugh.sockso.web.BadRequestException
     * 
     */
    
    private String getNewUploadFilename( final File dir, final String track, final String ext ) throws BadRequestException {
        
        final Locale locale = getLocale();
        
        int i = -1;
        
        // only try 100 times to avoid infinite loop, is 100 enough?  probably.
        while ( i++ < 100 ) {
            final File file = new File(
                dir.getAbsolutePath() + "/" + track + (i==0?"":i) + "." + ext
            );
            if ( !file.exists() )
                return file.getAbsolutePath();
        }
        
        throw new BadRequestException( locale.getString("www.error.couldNotCreateUniqueFilename"), 500 );

    }
    
    /**
     *  checks if we support this content type.  returns true if we do,
     *  false otherwise
     * 
     *  @param contentType the content type to check
     *  @return true if ok, false otherwise
     * 
     */
    
    protected boolean isValidContentType( final String contentType ) {

        final String validContentTypes[] = {
            "audio/mpg",
            "audio/mpeg",
            "application/ogg",
            "audio/x-ms-wma"
        };
        
        for ( final String validContentType : validContentTypes )
            if ( validContentType.equals(contentType) )
                return true;
        
        return false;

    }
    
    /**
     *  checks that everything is ok for doing uploads, ie. they're enabled, the
     *  user is logged in if required, etc...
     * 
     *  @throws BadRequestException
     * 
     */
    
    private void checkPermissions() throws BadRequestException {
        
        final User user = getUser();
        final Locale locale = getLocale();
        final Properties p = getProperties();

        // check uploads are enabled
        Utils.checkFeatureEnabled( p, "uploads.enabled" );
        
        // if the user isn't logged in then make sure that anonymous
        // uploads have been enabled
        if ( user == null )
            if ( !p.get(Constants.WWW_UPLOADS_ALLOW_ANONYMOUS).equals(p.YES) )
                throw new BadRequestException( locale.getString("www.error.noAnonymousUploads"), 403 );

        // check there is a valid collection set for uploads
        final Database db = getDatabase();
        final String uploadsPath = Utils.getUploadsPath( db, p );
        if ( uploadsPath.equals("") )
            throw new BadRequestException( locale.getString("www.error.noUploadsDirectory"), 500 );
        final File uploadsDir = new File( uploadsPath );
        if ( !uploadsDir.canWrite() )
            throw new BadRequestException( locale.getString("www.error.uploadsDirNotWritable"), 500 );

    }
    

    /**
     *  shows the form where users can upload music
     * 
     */
    
    protected void showUploadForm() throws IOException, BadRequestException, SQLException {

        final TUpload tpl = new TUpload();
        
        getResponse().showHtml( tpl );
        
    }
    
}
