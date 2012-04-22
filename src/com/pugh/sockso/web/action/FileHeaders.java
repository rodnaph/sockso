
package com.pugh.sockso.web.action;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.music.Files;
import com.pugh.sockso.web.Response;

import java.text.SimpleDateFormat;

import java.util.Date;

public class FileHeaders {
    
    private final Response res;

    private final Properties p;

    public FileHeaders( final Response res, Properties p ) {

        this.res = res;
        this.p = p;
        
    }

    /**
     *  send the headers for serving a resource, just need to give the name of
     *  the file we're serving to work out content types and stuff
     * 
     *  @param filename
     * 
     */
    
    public void sendHeaders( final String filename ) {

        final SimpleDateFormat formatter = new SimpleDateFormat( Constants.HTTP_DATE_FORMAT );

        final Date dateNow = new Date();
        final Date dateModified = new Date( dateNow.getTime() - Constants.ONE_WEEK_IN_MILLIS );
        final Date dateExpires = new Date( dateNow.getTime() + Constants.ONE_WEEK_IN_MILLIS );

        if ( !p.get(Constants.DEV_ENABLED).equals(Properties.YES) ) {
            res.addHeader( "Date", formatter.format(dateNow) );
            res.addHeader( "Last-Modified", formatter.format(dateModified) );
            res.addHeader( "Expires", formatter.format(dateExpires) );
        }

        res.addHeader( "Cache-Control", "public" );
        res.addHeader( "Pragma", "public" );
        res.addHeader( "Content-type", Files.getMimeType(filename) );
        res.sendHeaders();

    }

}
