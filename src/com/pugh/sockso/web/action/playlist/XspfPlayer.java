
package com.pugh.sockso.web.action.playlist;

import com.pugh.sockso.Properties;

import org.jamon.Renderer;

public interface XspfPlayer {

    public Renderer makeRenderer();

    public XspfPlayer setProperties( final Properties p );
    public XspfPlayer setPlayArgs( final String[] playArgs );
    public XspfPlayer setExtraArgs( final String extraArgs );

}
