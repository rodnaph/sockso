
package com.pugh.sockso.music;

import com.pugh.sockso.Utils;

import java.util.Arrays;

public class Files {

    public static final String DEFAULT_MIME_TYPE = "text/plain";

    /**
     * Valid mime types of accepted media
     */
    private static final String[] mimeTypes = {
        "audio/mpg",
        "audio/mpeg",
        "application/ogg",
        "audio/x-ms-wma",
        "audio/flac"
    };

    /**
     * Valid file extentions
     */
    private static final String[] fileExtensions = {
        "mp3",
        "wma",
        "ogg",
        "asf",
        "flac",
        "m4a"
    };

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

        for ( int i=0; i<mimes.length; i+=2 ) {
            if ( mimes[i].equals(ext) ) return mimes[i+1];
        }

        return DEFAULT_MIME_TYPE;
        
    }

    /**
     *  checks if we support this mime type.  returns true if we do,
     *  false otherwise
     * 
     *  @param mimeType the content type to check
     * 
     *  @return true if ok, false otherwise
     * 
     */
    
    public static boolean isValidMimeType( final String mimeType ) {

        return Arrays.asList( mimeTypes )
                     .contains( mimeType );

    }

    /**
     * Checks if the given file extension is valid
     * 
     * @param ext
     * 
     * @return 
     * 
     */
    
    public static boolean isValidFileExtension( final String ext ) {

        return Arrays.asList( fileExtensions )
                     .contains( ext.toLowerCase() );
        
    }
 
}
