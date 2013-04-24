
package com.pugh.sockso.web;

import com.pugh.sockso.Properties;
import com.pugh.sockso.resources.Locale;

import java.util.List;

import org.jamon.Renderer;

/**
 *  interface for all web pages
 * 
 */

public interface PageTemplate {

    public Renderer makeRenderer();

    public PageTemplate setRecentUsers( final List<User> recentUsers );
    public PageTemplate setProperties( final Properties properties );
    public PageTemplate setLocale( final Locale locale );
    public PageTemplate setUser( final User user );
    
}
