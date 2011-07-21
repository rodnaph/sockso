
package com.pugh.sockso.music.indexing;

import com.pugh.sockso.Utils;

import java.io.File;
import java.io.FileFilter;

public class PlaylistFileFilter implements FileFilter {

    /**
     *  Decides if this file is a playlist or a directory
     *
     *  @param file
     *
     *  @return
     *
     */

    public boolean accept( final File file ) {

        final String ext = Utils.getExt( file.getName() ).toLowerCase();

        return (file.isDirectory() ||                                                // is a directory
            ( ext.equals("m3u")  )) 
                && !file.getName().substring( 0, 1 ).equals( "." );                     // AND not hidden file
        
    }

}
