
package com.pugh.sockso.tests;

import com.pugh.sockso.resources.Locale;

import java.util.Set;

public class TestLocale implements Locale {

    public String getLangCode() {
        return "en";
    }

    public String getString( final String name ) {
        return "";
    }

    public String getString( final String name, final String[] replacements ) {
        return "";
    }

    public Set<String> getNames() {
        return null;
    }

}
