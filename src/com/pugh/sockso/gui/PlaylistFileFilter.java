
package com.pugh.sockso.gui;

import com.pugh.sockso.Utils;
import com.pugh.sockso.resources.Locale;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 *  A filter for playlist type files
 *
 */

public class PlaylistFileFilter extends FileFilter {

    public static final String[] VALID_EXTENSIONS = new String[] { "m3u" };

    private final Locale locale;

    /**
     *  Constructor
     *
     *  @param locale
     *
     */

    public PlaylistFileFilter( final Locale locale ) {
        this.locale = locale;
    }

    /**
     *  Returns the description of the filter (which can appear in the file chooser)
     *
     *  @return
     *
     */

    public String getDescription() {
        return locale.getString( "gui.label.playlists" );
    }

    /**
     *  Determines if the file is accepted by this filter
     *
     *  @param file
     *
     *  @return
     *
     */

    public boolean accept( final File file ) {

        if ( file.isDirectory() ) return true;

        final String extension = Utils.getExt( file );

        for ( final String validExtension : VALID_EXTENSIONS ) {
            if ( extension.equals(validExtension) ) {
                return true;
            }
        }

        return false;

    }

}
