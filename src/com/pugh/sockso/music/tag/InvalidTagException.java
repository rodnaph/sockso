/*
 * NoTagException.java
 * 
 * Created on Jun 8, 2007, 10:45:16 PM
 * 
 * This exception indicates that there was an error reading the tag
 * for an audio file.  This could be that there is just no tag there.
 * 
 */

package com.pugh.sockso.music.tag;

import java.io.File;

public class InvalidTagException extends Exception {

    public InvalidTagException( final File file ) {
        super( "No Tag (" + file.getAbsolutePath() + ")" );
    }

}
