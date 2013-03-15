
package com.pugh.sockso.tests;

import com.pugh.sockso.resources.Locale;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TestLocale implements Locale {

    private Map<String,String> strings;

    public TestLocale() {
        strings = new HashMap<String,String>();
    }

    @Override
    public String getLangCode() {
        return "en";
    }

    @Override
    public String getString( final String name ) {
        return strings.containsKey( name )
            ? strings.get( name )
            : "";
    }

    @Override
    public String getString( final String name, final String[] replacements ) {
        return "";
    }

    @Override
    public Set<String> getNames() {
        return null;
    }
    
    public void setString( final String name, final String value ) {
        strings.put( name, value );
    }

}
