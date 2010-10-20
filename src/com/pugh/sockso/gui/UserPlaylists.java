
package com.pugh.sockso.gui;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.music.CollectionManager;

/**
 *  holds a list of user playlists.  these can then be deleted
 * 
 */

public class UserPlaylists extends Playlists {

    public UserPlaylists( final Database db, final CollectionManager cm, final Resources r ) {
        super( db, cm, r, Playlists.USER_PLAYLISTS );
    }
    
}
