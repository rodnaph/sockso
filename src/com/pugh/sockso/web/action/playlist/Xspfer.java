/*
 * Xspfer.java
 * 
 * Created on Jun 19, 2007, 12:56:18 AM
 * 
 * Generates playlists in the XSPF format
 * 
 */

package com.pugh.sockso.web.action.playlist;

import com.pugh.sockso.templates.TXspf;

public class Xspfer extends Playlister {

    public void init( final String protocol ) {
        super.init( protocol, "xspf" );
    }

    public PlaylistTemplate getPlaylistTemplate() {
        return new TXspf();
    }
    
}
