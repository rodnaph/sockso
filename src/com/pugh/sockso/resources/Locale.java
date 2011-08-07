
package com.pugh.sockso.resources;

import java.util.Set;

public interface Locale {

    /**
     *  returns the language code for this locale
     * 
     *  @return 2 char language code (eg. en, cy, etc...)
     * 
     */
    
    public String getLangCode();
    
    /**
     *  returns a string for the locale key
     * 
     *  @param name the locale key
     *  @return locale string
     * 
     */

    public String getString( final String name );
    
    /**
     *  returns some locale text, but with replacements made using the
     *  array supplied.  ie.  %1 becomes replacements[0], etc...
     * 
     *  @param name locale key
     *  @param replacements array of replacement text
     * 
     */
    
    public String getString( final String name, final String[] replacements );
    
    /**
     *  returns an array of all the names of the translations that
     *  have been loaded for this locale
     * 
     *  @return String[]
     * 
     */
    
    public Set<String> getNames();
    
}
