/*
 * Plser.java
 * 
 * Created on Jul 13, 2007, 9:32:48 AM
 * 
 * Creates a playlist in the PLS format
 * 
 */

package com.pugh.sockso.web.action.playlist;

import com.pugh.sockso.templates.TPls;

public class Plser extends Playlister {

    public void init( final String protocol ) {
        super.init( protocol, "pls" );
    }

    public PlaylistTemplate getPlaylistTemplate() {
        return new TPls();
    }

}
