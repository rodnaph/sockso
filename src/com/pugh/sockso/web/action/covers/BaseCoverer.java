
package com.pugh.sockso.web.action.covers;

import com.pugh.sockso.Properties;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.CoverArt;
import com.pugh.sockso.music.CoverArtCache;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.Response;
import com.pugh.sockso.web.action.FileHeaders;

import java.awt.image.BufferedImage;

import java.io.IOException;
import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

abstract public class BaseCoverer implements CovererPlugin {

    private static final Logger log = Logger.getLogger( BaseCoverer.class );

    private Response res;
    private Request req;
    private Properties p;
    private Locale locale;
    private Database db;

    @Inject
    protected CoverArtCache coverCache;

    /**
     *  Sets the current database
     * 
     *  @param db 
     * 
     */

    public void setDatabase( final Database db ) {

        this.db = db;
        
    }

    /**
     *  Returns the database connection
     * 
     *  @return 
     * 
     */

    public Database getDatabase() {

        return db;
        
    }
    
    /**
     *  Sets the current locale object
     * 
     *  @param locale 
     * 
     */
    
    public void setLocale( final Locale locale ) {

        this.locale = locale;
        
    }

    /**
     *  Fetches the current locale object
     * 
     *  @return 
     * 
     */

    public Locale getLocale() {

        return locale;

    }

    /**
     *  Set the response object
     * 
     *  @param res 
     * 
     */

    public void setResponse( final Response res ) {

        this.res = res;

    }

    /**
     *  Returns the response object
     * 
     *  @return 
     * 
     */

    protected Response getResponse() {

        return res;
        
    }

    /**
     *  Sets the request object
     * 
     *  @param req 
     * 
     */

    public void setRequest( final Request req ) {

        this.req = req;

    }

    /**
     *  Returns the request object
     * 
     *  @return 
     * 
     */

    protected Request getRequest() {

        return req;
        
    }

    /**
     *  Set application properties
     * 
     *  @param p 
     * 
     */

    public void setProperties( final Properties p ) {

        this.p = p;
        
    }

    /**
     *  Fetch application properties
     * 
     *  @return 
     * 
     */

    public Properties getProperties() {

        return p;
               
    }

    /**
     *  Serves a cover in the response
     * 
     *  @param cover
     *  @param itemName
     *  @param addToCache
     * 
     *  @throws IOException 
     * 
     */

    protected void serveCover( final CoverArt cover, final String itemName, final boolean addToCache) throws IOException {

        if ( addToCache ){
            coverCache.addToCache(cover);
        }

        if ( req.hasArgument("width") && req.hasArgument("height") )  {

            final int width  = Integer.parseInt( req.getArgument("width") );
            final int height = Integer.parseInt( req.getArgument("height") );

            log.debug( "Scaling cover to " +width+ ":" +height );

            cover.scale(width, height);

        }

        final String extension = CoverArtCache.DEFAULT_IMAGE_TYPE;
        final BufferedImage image = cover.getImage();

        sendHeaders( itemName + "." + extension );

        ImageIO.write(
            image,
            extension,
            res.getOutputStream()
        );
        
    }

    /**
     *  Send headers for the file name (sends mime type from extension too)
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
