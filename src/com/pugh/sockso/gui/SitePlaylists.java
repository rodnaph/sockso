
package com.pugh.sockso.gui;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.music.CollectionManager;

/**
 *  shows playlists created by the site owner
 * 
 */

public class SitePlaylists extends Playlists {

    public SitePlaylists( final Database db, final CollectionManager cm, final Resources r ) {
        super( db, cm, r, Playlists.SITE_PLAYLISTS );
    }
    
}
