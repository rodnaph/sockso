
package com.pugh.sockso.music.indexing;

import com.pugh.sockso.Utils;
import com.pugh.sockso.music.Files;

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

        return (file.isDirectory() || Files.isValidFileExtension(ext))         // is a directory or audio file
            && !file.getName().substring( 0, 1 ).equals( "." );                     // AND not hidden file
        
    }

}
