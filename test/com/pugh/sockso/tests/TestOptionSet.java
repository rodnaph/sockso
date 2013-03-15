
package com.pugh.sockso.tests;

import java.util.HashMap;
import java.util.Map;

import joptsimple.OptionSetProxy;

public class TestOptionSet extends OptionSetProxy {

    private final Map<String,String> options;

    public TestOptionSet() {
        options = new HashMap<String,String>();
    }

    public void addHas( final String name, final String value ) {
        options.put( name, value );
    }

    @Override
    public boolean has( final String name ) {
        return options.containsKey( name );
    }

    @Override
    public Object valueOf( final String name ) {
        return options.get( name );
    }

}
