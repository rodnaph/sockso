
package com.pugh.sockso.resources;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Hashtable;

@Singleton
public class LocaleFactory {

    private String defaultLocaleCode;
    private Hashtable<String,Locale> locales = null;
    
    private final Resources r;
    
    @Inject
    public LocaleFactory( final Resources r ) {
        
        this.r = r;
        
    }
    
    /**
     *  Initialise the factory with a default locale
     * 
     *  @param defaultLocaleCode 
     * 
     */
    
    public void init( final String defaultLocaleCode ) {

        this.defaultLocaleCode = defaultLocaleCode;
        
    }
    
    /**
     *  Fetches a locale object for the specified locale code
     * 
     *  @param localeCode
     * 
     *  @return 
     * 
     */
    
    public Locale getLocale( final String localeCode ) {
        
        if ( locales == null ) {
            
            final Locale defaultLocale = new FileLocale( r, Resources.DEFAULT_LOCALE );
            
            locales = new Hashtable<String,Locale>();
            
            for ( final String lc : ((AbstractResources) r).getLocales() ) {
                locales.put( lc, new FileLocale(r,lc,defaultLocale) );
            }
            
        }

        if ( locales.get(localeCode) != null ) {
            return locales.get(localeCode);
        }
        
        return locales.get( Resources.DEFAULT_LOCALE );
        
    }
    
    /**
     *  Returns the locale object for the configured default
     * 
     *  @return
     * 
     */
    
    public Locale getDefaultLocale() {
        
        return getLocale( defaultLocaleCode );
        
    }
    
}
