
package com.pugh.sockso.music.indexing;

import com.pugh.sockso.Utils;

import java.io.File;
import java.io.FileFilter;

public class TrackFileFilter implements FileFilter {

    /**
     *  Decides if this file is a track or a directory
     *
     *  @param file
     *
     *  @return
     *
     */

    public boolean accept( final File file ) {

        final String ext = Utils.getExt( file.getName() ).toLowerCase();

        return (file.isDirectory() ||                                                // is a directory
            ( ext.equals("mp3") || ext.equals("wma") || ext.equals("ogg")           // OR is an audio file
              || ext.equals("asf") || ext.equals("flac") || ext.equals("m4a") ))
            && !file.getName().substring( 0, 1 ).equals( "." );                     // AND not hidden file
        
    }

}
