/*
 * AbstractResources.java
 * 
 * Created on Jul 24, 2007, 12:48:10 AM
 * 
 * Superclass for all resource providers
 * 
 */

package com.pugh.sockso.resources;

import java.util.Hashtable;
import java.util.Vector;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public abstract class AbstractResources implements Resources {

    private Hashtable<String,Locale> locales = null;
    private String localeCode = DEFAULT_LOCALE;
    private String[] localeCodes;
    
    /**
     *  inits the resources by loading in all data needed for the specified
     *  locale.
     * 
     */
    
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
     *  returns a specific locale, returns default if it doesn't exist
     * 
     *  @param locale the locale name
     *  @return locale object
     * 
     */
    
    public Locale getLocale( final String locale ) {

        // we may need to load locale information if this is the first time
        // this method has been called
        if ( locales == null ) {
            final Locale defaultLocale = new FileLocale( this, DEFAULT_LOCALE );
            locales = new Hashtable<String,Locale>();
            for ( final String lc : localeCodes )
                locales.put( lc, new FileLocale(this,lc,defaultLocale) );
        }

        // try and return correct locale if we have it, otherwise just send
        // back the default
        if ( locales.get(locale) != null )
            return locales.get(locale);
        return locales.get( DEFAULT_LOCALE );
        
    }
    
    /**
     *  returns the default locale
     * 
     *  @return Locale
     * 
     */
    
    public Locale getCurrentLocale() {
        return getLocale( localeCode );
    }
    
    public void setCurrentLocaleCode( final String newLocale ) {
        localeCode = newLocale;
    }
    
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

        final Vector<String> codes = new Vector<String>();

        for ( int i=0; i<files.length; i++ ) {
            
            final Pattern p = Pattern.compile( "sockso.(.*).txt" );
            final Matcher m = p.matcher( files[i] );
            
            if ( m.matches() )
                codes.addElement( m.group(1) );
            
        }

        return (String[]) codes.toArray( new String[codes.size()] );
        
    }

}
