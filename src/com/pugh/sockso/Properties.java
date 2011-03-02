/*
 * Properties.java
 * 
 * Created on May 30, 2007, 11:14:07 PM
 * 
 *  This class provides a singleton which can be used to easily
 *  persist name/value pairs to the database.
 * 
 */

package com.pugh.sockso;

import java.util.Hashtable;

import org.apache.log4j.Logger;

public interface Properties {

    // standard properties
    public static final String YES = "yes";
    public static final String NO = "no";

    public static final Logger log = Logger.getLogger( Properties.class );

    /**
     *  init the properties object
     * 
     *  @throws java.lang.Exception
     * 
     */
    
    public void init() throws Exception;
    
    /**
     *  registers a class interested in listening for property events
     * 
     *  @param listener the interested class
     * 
     */
    
    public void addPropertiesListener( final PropertiesListener listener );
    
    /**
     *  sets the value of a property.  if it already exists then
     *  its old value is just overwritten
     * 
     *  @param name the name of the property
     *  @param value the property value
     * 
     */
    
    public void set( final String name, final String value );
    
    /**
     *  allows setting a value with a boolean, this will then use
     *  the value of the YES and NO static members
     * 
     *  @param name property name
     *  @param value boolean (YES or NO)
     *
     */
    
    public void set( final String name, final boolean value );

    /**
     *  sets a property to an integer value
     * 
     *  @param name property name
     *  @param value integer value
     * 
     */
    
    public void set( final String name, final long value );
    
    /**
     *  returns the value of a named property.  if the property
     *  doesn't exist then you'll get the empty string
     * 
     *  @param name the property name to fetch
     *  @return the property's value
     * 
     */
    
    public String get( final String name );
    
    /**
     *  this method fetches a value from the properties, but you can specify
     *  a default to return if the property is not set (null or "")
     * 
     *  @param name the name of the property
     *  @param defaultValue the default value to use
     *  @return the properties value
     * 
     */
    
    public String get( final String name, final String defaultValue );

    /**
     *  same as other get(), only takes a long as the default
     * 
     *  @param name property name
     *  @param defaultValue default long
     *  @return property value
     * 
     */
    
    public long get( final String name, final long defaultValue );

    /**
     *  returns a hash table of all the properties which start with the string
     *  given.  ie.  given the following properties:
     *  
     *    prop.a = b
     *    prop.b = c
     * 
     *  this function when passed "prop" will return { a => b, b => c }
     * 
     *  @param name property name substring
     * 
     *  @return Hashtable of values matched
     * 
     */
    
    public Hashtable<String,String> getMatches( final String name );

    /**
     *  returns the names of all the properties
     * 
     *  @return array of names
     * 
     */
    
    public String[] getProperties();
    
    /**
     *  saves any changes to the properties so they are written
     *  to the database.
     * 
     */
    
    public void save();
    
    /**
     *  deletes a property if it exists
     * 
     *  @param name
     * 
     */
    
    public void delete( final String name );
    
    /**
     *  indicates if a given property exists in the current property object (though
     *  this property may ot may not have been saved yet)
     * 
     *  @param name
     * 
     */
    
    public boolean exists( final String name );
    
    /**
     * Resolves the url passed to the correct full url
     * If starts by <skin> get the resource based on the configured skin
     * @param url
     * @return 
     */
    public String getUrl(String url);

}
