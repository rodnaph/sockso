
package com.pugh.sockso.web.action;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.Response;

public class FileServer extends WebAction {

    private static final Logger log = Logger.getLogger( FileServer.class );

    private static final String CACHE_IMAGE_TYPE = "jpg";
    
    private final Resources r;
    
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

        if ( type.equals("cover") )
            serveCover();
        else
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
     *  serves a request file to the client
     * 
     *  @throws IOException
     *  @throws BadRequestException
     * 
     */
    
    public void serveFile() throws IOException, BadRequestException {

        final Request req = getRequest();
        String path = "htdocs";
        
        for ( int i=1; i<req.getParamCount(); i++ )
            path += "/" + req.getUrlParam(i);
        
        serveResource( path );

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

        final SimpleDateFormat formatter = new SimpleDateFormat( Constants.HTTP_DATE_FORMAT );

        final Date dateNow = new Date();
        final Date dateModified = new Date( dateNow.getTime() - Constants.ONE_WEEK_IN_MILLIS );
        final Date dateExpires = new Date( dateNow.getTime() + Constants.ONE_WEEK_IN_MILLIS );

        final Response res = getResponse();
        final Properties p = getProperties();
        
        // only cache if not in dev mode
        if ( !p.get(Constants.DEV_ENABLED).equals(Properties.YES) ) {
            res.addHeader( "Date", formatter.format(dateNow) );
            res.addHeader( "Last-Modified", formatter.format(dateModified) );
            res.addHeader( "Expires", formatter.format(dateExpires) );
        }

        res.addHeader( "Cache-Control", "public" );
        res.addHeader( "Pragma", "public" );
        res.addHeader( "Content-type", getMimeType(filename) );
        res.sendHeaders();

    }

    /**
     *  serves a cover of the specified type (determined by the url).  it first
     *  looks in the cache to see if we have it there, then on the filesystem
     *  to see if the user has it stored with their music, then finally if
     *  all else fails tries to scrape it from Amazon.
     * 
     *  @throws IOException
     *  @throws SQLException
     *  @throws BadRequestException
     * 
     */

    public void serveCover() throws IOException, SQLException, BadRequestException {
        
        final Request req = getRequest();
        final Properties p = getProperties();
        final Locale locale = getLocale();
        final String itemName = req.getUrlParam( 2 );
        
        // check feature isn't disabled
        if ( p.get(Constants.COVERS_DISABLED).equals(Properties.YES) )
            throw new BadRequestException( locale.getString("www.error.coversDisabled"), 404 );
        
        // got a cache hit?
        if ( serveCoverCache(itemName) )
            return;
        
        // 1. image isn't in the cache, first see if we can read it from disk
        // somewhere if the user has cover art stored with their music
        
        final String localPath = getLocalCoverPath( itemName );
        
        if ( localPath != null ) {
            serveLocalCover( itemName, localPath );
            return;
        }

        // 2. try searching amazon for a cover image to use (but only if this
        //    feature has not been disabled)
        
        if ( !p.get(Constants.COVERS_DISABLE_REMOTE_FETCHING).equals(Properties.YES) ) {
        
            final Database db = getDatabase();
            final CoverSearch search = new AmazonCoverSearch( db );
            final BufferedImage cover = search.getCover( itemName );

            if ( cover != null ) {
                serveCover( cover, itemName, true );
                return;
            }
        
        }
        
        // 3. if nothing found then just serve up the empty image saying so
        
        serveCover(
            getNoCoverImage(),
            "noCover",
            false
        );

    }

    /**
     *  returns the image to use when no cover art has been found
     * 
     *  @return
     * 
     */
    
    private BufferedImage getNoCoverImage() {

        final Locale locale = getLocale();
        final BufferedImage cover =  new BufferedImage( 115, 115, BufferedImage.TYPE_INT_RGB );
        final Graphics2D g = cover.createGraphics();

        // background
        g.setColor( Color.WHITE );
        g.fillRect( 0,0, 115,115 );

        // border
        g.setColor( new Color(200,200,200) );
        g.drawRect( 0,0, 114,114 );
        
        // text
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON );
        g.setColor( Color.BLACK );
        g.setFont( new Font("Verdana",Font.ITALIC,10) );
        g.drawString( locale.getString("www.text.noCover"), 10, 20 );
        
        return cover;
        
    }
    
    /**
     *  serves up a cover image specified by a buffered image.  the image will
     *  be added to the cache if we're told to do so
     * 
     *  @param cover
     *  @param itemName
     *  @param ext
     *  @param addToCache
     * 
     *  @throws java.io.IOException
     * 
     */
    
    private void serveCover( BufferedImage cover, final String itemName, final boolean addToCache ) throws IOException {

        final Request req = getRequest();

        // add to cache if we've been told to
        if ( addToCache )
            ImageIO.write(
                cover,
                CACHE_IMAGE_TYPE,
                new File( getCoverCachePath(itemName) )
            );

        // check if we've been asked to resize this image        
        if ( req.hasArgument("width") && req.hasArgument("height") )  {

            final int width = Integer.parseInt( req.getArgument("width") );
            final int height = Integer.parseInt( req.getArgument("height") );

            log.debug( "Scaling cover to " +width+ ":" +height );
            
            cover = scale( cover, width, height );

        }
        
        // send headers then image
        sendHeaders( itemName+ "." +CACHE_IMAGE_TYPE );
        ImageIO.write(
            cover,
            CACHE_IMAGE_TYPE,
            getResponse().getOutputStream()
        );

    }

    /**
     *  serves a local cover that the user has stored with their collection, this
     *  file may be any size so we need to resize it if it's not what we want.
     * 
     *  @param itemName
     *  @param localPath
     * 
     *  @throws java.io.IOException
     * 
     */
    
    protected void serveLocalCover( final String itemName, final String localPath ) throws IOException {

        // resize image if it's too big
        final Properties p = getProperties();
        final BufferedImage originalImage = ImageIO.read( new File(localPath) );
        final BufferedImage resizedImage = scale(
            originalImage,
            (int) p.get( Constants.DEFAULT_ARTWORK_WIDTH, 115 ),
            (int) p.get( Constants.DEFAULT_ARTWORK_HEIGHT, 115 )
        );

        // only cache local cover images if we've been explicitly
        // told to do so (improves performance)
        
        serveCover(
            resizedImage,
            itemName,
            p.get(Constants.COVERS_CACHE_LOCAL).equals(Properties.YES)
        );

    }

    /**
     *  scales an image to the specified width and height and returns a new image
     * 
     *  @param origImage
     *  @param width
     *  @param height
     * 
     *  @return
     * 
     */
    
    private BufferedImage scale( final BufferedImage origImage, final int width, final int height ) {

        final int origWidth = origImage.getWidth();
        final int origHeight = origImage.getHeight();

        // check if we need to resize at all
        if ( width >= origWidth && height >= origHeight )
            return origImage;

        return scale(
            origImage,
            calcScalingFactor(
                origWidth, origHeight,
                width, height
            )
        );
    }

    /**
     *  Scale the image by the specified factor
     * 
     *  @param image
     *  @param dScaleFactor
     * 
     *  @return 
     * 
     */
    
    public BufferedImage scale( Image image, double dScaleFactor ) {
        
        // calculate new width and height
        int iWidth = ( int ) ( image.getWidth(null)*dScaleFactor );
        int iHeight = ( int ) ( image.getHeight(null)*dScaleFactor );
     
        // create a BufferedImage instance
        BufferedImage bufferedImage = new BufferedImage( iWidth, iHeight, BufferedImage.TYPE_INT_RGB );
     
        // create the image's graphics
        Graphics2D g = bufferedImage.createGraphics();
     
        // Drawing hints with focus on quality
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
        g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
     
        // Apply scalefactor
        g.drawImage( image, 0, 0, iWidth, iHeight, null );
     
        return bufferedImage;
        
    }

    /**
     *  Calculate the factor to scale the image by
     * 
     *  @param srcWidth
     *  @param srcHeight
     *  @param targetWidth
     *  @param targetHeight
     * 
     *  @return 
     * 
     */
    
    public double calcScalingFactor( int srcWidth, int srcHeight, int targetWidth, int targetHeight ) {
        
        final boolean tall = (srcHeight > srcWidth);
        final double factor =
           (double) (tall ? targetHeight : targetWidth)
	   / (double) (tall ? srcHeight : srcWidth);
        
        return factor;
        
    }


    /**
     *  returns unique directories associated with an itemName (eg. ar123).  this
     *  is worked out by getting all the tracks for this item, and using the
     *  directory that they're in.
     * 
     *  @param itemName (eg. ar123)
     * 
     *  @return
     * 
     *  @throws java.sql.SQLException
     * 
     */
    
    protected File[] getLocalCoverDirectories( final String itemName ) throws SQLException {

        final Vector<File> dirs = new Vector<File>();

        ResultSet rs = null;
        PreparedStatement st = null;
        
        try {

            final HashSet<String> taken = new HashSet<String>();
            final String type = itemName.substring( 0, 2 );
            final int id = Integer.parseInt( itemName.substring(2) );
            final boolean isArtist = type.equals( "ar" );
            final String typeName = isArtist ? "artist" : "album";
            final String sql = " select t.path as path " +
                               " from tracks t " +
                               " where t." +typeName+ "_id = " +id;
            final Database db = getDatabase();
            
            st = db.prepare( sql );
            rs = st.executeQuery();
        
            while ( rs.next() ) {
                final String path = rs.getString( "path" );
                if ( !taken.contains(path) ) {
                    dirs.add( new File(path) );
                    taken.add( path );
                }
            }
            
        }

        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
        return dirs.toArray( new File[0] );
        
    }
    
    /**
     *  determines if an item name (eg. ar123) is an artist
     * 
     *  @param itemName
     * 
     *  @return
     * 
     */
    
    protected boolean isArtist( final String itemName ) {
        
        return itemName != null
            && itemName.length() > 2
            && itemName.substring( 0, 2 ).toLowerCase().equals( "ar" );
        
    }

    /**
     *  returns the filename for local covers.  there are defaults for both
     *  artists ("artist") and albums ("album"), but the user can override this themselves
     * 
     *  @param itemName
     * 
     *  @return
     * 
     */
    
    protected String getLocalCoverFileName( final String itemName ) {

        final Properties p = getProperties();
        final String typeName = isArtist(itemName) ? "artist" : "album";

        return p.get(
            isArtist(itemName) ? Constants.COVERS_ARTIST_FILE : Constants.COVERS_ALBUM_FILE,
            typeName
        );
        
    }
    
    /**
     *  returns an array of files to test that could possibly be local covers.
     *  
     *  @param trackDirs
     *  @param coverFileName
     * 
     *  @return
     * 
     */
    
    protected File[] getLocalCoverFiles( final File[] trackDirs, final String coverFileName, final boolean isArtist ) {
        
        final Vector<File> files = new Vector<File>();
        final String[] exts = { "jpg", "png", "gif" };
        final Properties p = getProperties();

        for ( final File track : trackDirs ) {

            final String[] dirs = {
                track.getParent(),
                isArtist ? track.getParentFile().getParent() : null
            };

            // look for album info in this directory, but artist info in the
            // directory one level up as well (maybe "/My Music/artist/album/"
            // structure)
            for ( final String directory : dirs ) {
                if ( directory == null ) continue; // will be null if album
                for ( final String ext : exts ) {
                    final String path = directory + "/" +coverFileName+ "." +ext;
                    files.add( new File(path) );
                }
            }
            
            // Should we fallback and search for the first image
            // file in the track folder, regardless of its name ?
            if ( p.get(Constants.COVERS_FILE_FALLBACK).equals(Properties.YES) ) {
                final File fallbackFile = checkForFallbackFile( exts, track );
                if ( fallbackFile != null ) {
                    files.add( fallbackFile );
                }
            }

            
        }

        return files.toArray( new File[0] );
        
    }
    
    /**
     *  Checks for a file in the track directory to use as a fallback
     * 
     *  @param files
     *  @param exts
     *  @param track 
     * 
     */
    protected File checkForFallbackFile( final String[] exts, final File track ) {
        
        final File[]fallbackFiles = track.getParentFile().listFiles(new FileFilter() {
            @Override
            public boolean accept(File f) {
                
                if (f.isFile()) {
                    for (final String ext : exts) {
                        if (f.getName().endsWith(ext)) {
                            return true;
                        }
                    }
                }
                
                return false;
                
            }
        });

        if ( fallbackFiles != null && fallbackFiles.length > 0 ) {
            log.debug("Found " + fallbackFiles.length + " fallback cover files."
                            + " Picking first: " + fallbackFiles[0].getAbsolutePath());
            return fallbackFiles[ 0 ];
        }
        
        return null;

    }
    
    /**
     *  looks on the filesystem to see if the user has cover art stored with
     *  their music.  if found it returns the path to the file, otherwise null.
     *  The itemName is the name of the music item, eg. ar123, al456, etc...
     * 
     *  @param itemName
     * 
     *  @return
     * 
     *  @throws SQLException
     * 
     */
    
    protected String getLocalCoverPath( final String itemName ) throws SQLException {
       
        final String coverFileName = getLocalCoverFileName( itemName );
        final File[] trackDirs = getLocalCoverDirectories( itemName );
        final File[] possibleCovers = getLocalCoverFiles( trackDirs, coverFileName, isArtist(itemName) );

        for ( final File possibleCover : possibleCovers ) {
            if ( possibleCover.exists() )
                return possibleCover.getAbsolutePath();
        }
            
        return null;
        
    }

    /**
     *  checks the cover cache for an image, and serves it if it's found.  returns
     *  a boolean to indicate if it did or not
     * 
     *  @param itemName the music argument (eg ar123, al456)
     * 
     *  @return boolean indicating cache hit
     * 
     *  @throws IOException
     * 
     */

    private boolean serveCoverCache( final String itemName ) throws IOException {

        final File coverFile = new File( getCoverCachePath(itemName) );
        
        if ( coverFile.exists() ) {
            serveCover(
                ImageIO.read( coverFile ),
                itemName,
                false
            );
            return true;
        }

        return false;
        
    }
    
    /**
     *  returns the absolute path of the cache file
     * 
     *  @param name
     *  @param ext
     * 
     *  @return the cache file path
     * 
     */
    
    private String getCoverCachePath( final String name ) {
        
        return Utils.getCoversDirectory() + "/" + name + "." + CACHE_IMAGE_TYPE;
        
    }
    
    /**
     *  returns the mime type for the file an the given path.  if the file type
     *  is not known then text/plain is returned (just needs extension really).
     * 
     *  @param path file system path of file
     * 
     *  @return String mime type
     * 
     */
    
    public static String getMimeType( String path ) {

        final String ext = Utils.getExt( path );
        final String[] mimes = {
            "css", "text/css",
            "js", "text/javascript",
            "png", "image/png",
            "gif", "image/gif",
            "ico", "image/x-icon",
            "swf", "application/x-shockwave-flash",
            "jpg", "image/jpeg",
            "mp3", "audio/mpeg",
            "ogg", "application/ogg",
            "wma", "audio/x-ms-wma",
            "asf", "audio/x-ms-asf",
            "flac", "audio/flac",
            "xspf", "application/xspf+xml",
            "pls", "audio/x-scpls",
            "m3u", "audio/mpegurl",
            "xml", "text/xml",
            "m4a", "audio/mp4"
        };

        for ( int i=0; i<mimes.length; i+=2 )
            if ( mimes[i].equals(ext) ) return mimes[i+1];

        return "text/plain";
        
    }

}
