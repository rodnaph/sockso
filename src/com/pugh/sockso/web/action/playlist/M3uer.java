/*
 * M3uer.java
 * 
 * Created on Jun 18, 2007, 11:53:13 PM
 * 
 * Creates m3u playlists
 * 
 */

package com.pugh.sockso.web.action.playlist;

import com.pugh.sockso.templates.TM3u;

public class M3uer extends Playlister {

    public void init( final String protocol ) {
        super.init( protocol, "m3u" );
    }

    public PlaylistTemplate getPlaylistTemplate() {
        return new TM3u();
    }

}
