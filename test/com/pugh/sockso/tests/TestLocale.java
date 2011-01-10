
package com.pugh.sockso.tests;

import com.pugh.sockso.resources.Locale;

import java.util.Set;
import java.util.Hashtable;

public class TestLocale implements Locale {

    private Hashtable<String,String> strings;

    public TestLocale() {
        strings = new Hashtable<String,String>();
    }

    public String getLangCode() {
        return "en";
    }

    public String getString( final String name ) {
        return strings.containsKey( name )
            ? strings.get( name )
            : "";
    }

    public String getString( final String name, final String[] replacements ) {
        return "";
    }

    public Set<String> getNames() {
        return null;
    }
    
    public void setString( final String name, final String value ) {
        strings.put( name, value );
    }

}
