/*
 * PlaylistTemplate.java
 * 
 * Created on Jul 13, 2007, 6:31:00 PM
 * 
 * An interface for playlist templates to implement.
 * 
 */

package com.pugh.sockso.web.action.playlist;

import com.pugh.sockso.Properties;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.User;
import com.pugh.sockso.music.Track;

import java.util.Vector;

import org.jamon.Renderer;

public interface PlaylistTemplate {

    public Renderer makeRenderer();

    public PlaylistTemplate setTracks( final Track[] tracks );
    public PlaylistTemplate setRequest( final Request request );
    public PlaylistTemplate setProtocol( final String protocol );
    public PlaylistTemplate setProperties( final Properties properties );
    public PlaylistTemplate setUser( final User user );

}
