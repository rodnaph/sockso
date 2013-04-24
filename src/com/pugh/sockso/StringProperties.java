
package com.pugh.sockso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  An in-memory properties class
 * 
 *  @author rod
 * 
 */

public class StringProperties implements Properties {

    /**
     *  The property data
     *
     */
    protected final Map<String,String> data;

    /**
     *  Listeners registered for property change events
     *
     */
    private final List<PropertiesListener> listeners;

    /**
     *  creates the properties object.
     *
     *  @param db the database connection
     *
     */

    public StringProperties() {

        data = new HashMap<String,String>();
        listeners = new ArrayList<PropertiesListener>();

    }

    /**
     *  inits the object
     *
     *  @throws java.lang.Exception
     *
     */

    @Override
    public void init() throws Exception {}

    /**
     *  returns an array of all the property names we have stored
     *
     *  @return
     *
     */

    @Override
    public String[] getProperties() {

        final Set<String> props = data.keySet();

        return props.toArray( new String[0] );

    }

    /**
     *  registers a class interested in listening for property events
     *
     *  @param listener the interested class
     *
     */

    @Override
    public void addPropertiesListener( final PropertiesListener listener ) {

        listeners.add( listener );

    }

    /**
     *  informs all listeners that an event has taken place
     *
     */

    protected void firePropertiesSavedEvent() {

        for ( final PropertiesListener listener : listeners ) {
            listener.propertiesSaved( this );
        }

    }

    /**
     *  sets the value of a property.  if it already exists then
     *  its old value is just overwritten
     *
     *  @param name the name of the property
     *  @param value the property value
     *
     */

    @Override
    public void set( final String name, final String value ) {

        data.put( name, value );
        
    }

    /**
     *  allows setting a value with a boolean, this will then use
     *  the value of the YES and NO static members
     *
     *  @param name property name
     *  @param value boolean (YES or NO)
     *
     */

    @Override
    public void set( final String name, final boolean value ) {

        set( name, value ? YES : NO );

    }

    /**
     *  sets a property to an long value
     *
     *  @param name property name
     *  @param value integer value
     *
     */

    @Override
    public void set( final String name, final long value ) {

        set( name, Long.toString(value) );

    }

    /**
     *  returns the value of a named property.  if the property
     *  doesn't exist then you'll get the empty string
     *
     *  @param name the property name to fetch
     *  @return the property's value
     *
     */

    @Override
    public String get( final String name ) {

        return get( name, "" );

    }

    /**
     *  this method fetches a value from the properties, but you can specify
     *  a default to return if the property is not set (null or "")
     *
     *  @param name the name of the property
     *  @param defaultValue the default value to use
     *  @return the properties value
     *
     */

    @Override
    public String get( final String name, final String defaultValue ) {

        final String value = data.get( name );

        return value == null || value.equals("")
                ? defaultValue : value;
        
    }

    /**
     *  same as other get(), only takes a long as the default and returns
     *  an integer (if the property isn't a long then the default will
     *  be returned)
     *
     *  @param name property name
     *  @param defaultValue default long
     *
     *  @return property value
     *
     */

    @Override
    public long get( final String name, final long defaultValue ) {

        long returnValue = defaultValue;

        try {
            returnValue = Long.parseLong(
                get( name, Long.toString(defaultValue) )
            );
        }

        catch ( final NumberFormatException e ) {
            /* ignore - we'll return default */
        }

        return returnValue;

    }

    /**
     *  returns Map of matched values
     *
     *  @param name
     *  @return
     *
     */

    @Override
    public Map<String,String> getMatches( final String name ) {

        final Map<String,String> hash = new HashMap<String,String>();
        final int nameLength = name.length();

        for ( final String key : data.keySet() ) {
            if ( key.length() >= nameLength && key.substring(0,nameLength).equals(name) ) {
                hash.put( key.substring(nameLength+1), get(key) );
            }
        }

        return hash;

    }

    /**
     *  saves any changes to the properties so they are written
     *  to the database.
     *
     */

    @Override
    public void save() {

        firePropertiesSavedEvent();

    }

    /**
     *  deletes a property
     *
     *  @param name
     *
     */

    @Override
    public void delete( final String name ) {

        data.remove( name );

    }

    /**
     *  indicates if a property exists
     *
     *  @param name
     *
     *  @return
     *
     */

    @Override
    public boolean exists( final String name ) {

        return ( data.get(name) != null );

    }
    
    /**
     *  Returns a URL with base path/skin resolved
     *
     *  @param url
     *
     *  @return
     *
     */

    @Override
    public String getUrl( final String url ) {

        String newUrl = url;
        String basepath = this.get( Constants.SERVER_BASE_PATH, "/" );

        if ( newUrl.startsWith("http://") || newUrl.startsWith("https://") ) {
            return newUrl;
        }
                
        if ( newUrl.startsWith("/") ) {
            newUrl = newUrl.substring( 1 );
        }
        
        if ( newUrl.startsWith("<skin>/")) {
            newUrl = newUrl.replace("<skin>", "file/skins/"+this.get(Constants.WWW_SKIN, "original" ));
        }
        

        if ( !basepath.endsWith("/") ) {
            basepath += "/";
        }

        if (!basepath.startsWith("/") && !basepath.startsWith("http://") && !basepath.startsWith("https://")) {
            basepath = "/"+basepath;
        }
        
        return basepath+newUrl;

    }
    
}
