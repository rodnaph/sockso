/*
 * AbstractResources.java
 * 
 * Created on Jul 24, 2007, 12:48:10 AM
 * 
 * Superclass for all resource providers
 * 
 */

package com.pugh.sockso.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractResources implements Resources {

    private Map<String,Locale> locales = null;
    private String localeCode = DEFAULT_LOCALE;
    private String[] localeCodes;
    
    /**
     *  inits the resources by loading in all data needed for the specified
     *  locale.
     * 
     */
    
    @Override
    public void init( final String localeCode ) {
        
        this.localeCodes = getLocales();
        this.localeCode = localeCode;

    }
    
    /**
     *  returns an array of lang codes of the locale files sockso has
     * 
     *  @return
     * 
     */
    
    public abstract String[] getLocales();
    
    /**
     *  returns an array of land codes from an array of locale file names (and
     *  possibly other files as well, but they'll be ignored)
     * 
     *  @param files
     * 
     *  @return
     * 
     */
    
    protected static String[] getLocalesFromFiles( String[] files ) {

        final List<String> codes = new ArrayList<String>();

        for ( int i=0; i<files.length; i++ ) {
            
            final Pattern p = Pattern.compile( "sockso.(.*).txt" );
            final Matcher m = p.matcher( files[i] );
            
            if ( m.matches() )
                codes.add( m.group(1) );
            
        }

        return codes.toArray( new String[codes.size()] );
        
    }

}
