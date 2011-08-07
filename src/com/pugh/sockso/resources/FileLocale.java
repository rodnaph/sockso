/*
 * Locale.java
 * 
 * Created on Oct 7, 2007, 10:22:03 PM
 * 
 * An implementation of locales that pulls it's information
 * from text files.
 * 
 */

package com.pugh.sockso.resources;

import com.pugh.sockso.Utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.Hashtable;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;

@Singleton
public class FileLocale implements Locale {

    private static final Logger log = Logger.getLogger( Locale.class );
    
    private final Hashtable<String,String> strings;
    private final String langCode;

    /**
     *  creates a new locale object
     * 
     *  @param r the resource to read locale info from
     *  @param langCode the language code for this locale (en, cy, etc...)
     * 
     */
    
    public FileLocale( final Resources r, final String langCode  ) {
        this( r, langCode, null );
    }
    
    /**
     *  creates a new locale object, but (optionally if not null) uses the
     *  default locale object to fill in any missing translations for this
     *  locale.
     * 
     *  @param r
     *  @param langCode
     *  @param defaultLocale
     * 
     */
    
    public FileLocale( final Resources r, final String langCode, final Locale defaultLocale ) {

        this.strings = new Hashtable<String,String>();
        this.langCode = langCode;
        
        BufferedReader in = null;
        
        try {

            final String localeFile = "locales/sockso." + langCode + ".txt";
            final Pattern p = Pattern.compile( "^(.*?)=(.*)$" );

            String line = "";
            
            in = new BufferedReader( new InputStreamReader(r.getResourceAsStream(localeFile)) );

            while ( (line = in.readLine()) != null ) {
                final Matcher m = p.matcher( line );
                if ( m.matches() )
                    strings.put( m.group(1), m.group(2) );
            }

            // if we have a default locale, then use this to fill in
            // any missing translations in this locale
            if ( defaultLocale != null )
                for ( final String name : defaultLocale.getNames() ) {
                    if ( getString(name).equals("") )
                        strings.put( name, defaultLocale.getString(name) );
                }

        }

        catch ( final IOException e ) {
            log.error( "Error loading locale: " + e.getMessage() );
        }
        
        finally { Utils.close(in); }

    }

    /**
     *  returns the language code for this locale
     * 
     *  @return 2 char language code (eg. en, cy, etc...)
     * 
     */
    
    public String getLangCode() {
        
        return langCode;
        
    }
    
    /**
     *  returns a string for the locale key
     * 
     *  @param name the locale key
     *  @return locale string
     * 
     */

    public String getString( final String name ) {

        String value = strings.get( name );
        
        if ( value == null )
            value = "";

        return value;

    }
    
    /**
     *  returns some locale text, but with replacements made using the
     *  array supplied.  ie.  %1 becomes replacements[0], etc...
     * 
     *  @param name locale key
     *  @param replacements array of replacement text
     * 
     */
    
    public String getString( final String name, final String[] replacements ) {
        
        String value = getString( name );

        for ( int i=0; i<replacements.length; i++ )
            value = value.replaceAll( "%"+(i+1), replacements[i] );

        return value;
        
    }
    
    /**
     *  returns all names in this locale
     * 
     *  @return
     * 
     */
    
    public Set<String> getNames() {
        
        return strings.keySet();
        
    }
    
}
