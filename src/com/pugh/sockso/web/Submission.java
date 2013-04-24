
package com.pugh.sockso.web;

import com.pugh.sockso.resources.Locale;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 *  Encapsulates validating some data that has been submitted
 * 
 */

public class Submission {

    public static final int FIELD_TEXT = 1;
    public static final int FIELD_EMAIL = 2;
    public static final int FIELD_DATE = 3;
    public static final int FIELD_INTEGER = 4;
    
    private final List<String> errors;
    private final Request req;
    private final Locale locale;
    
    public Submission( final Request req, final Locale locale ) {
        
        this.req = req;
        this.locale = locale;
        this.errors = new ArrayList<String>();
        
    }
    
    /**
     *  adds a field to be validated (field assumed to be required)
     * 
     *  @param name
     *  @param type
     * 
     */
    
    public void addField( final String name, final int type ) {
        addField( name, type, true, "" );
    }
    
    
    /**
     *  adds a field to be validated (using locale key for error)
     * 
     *  @param name
     *  @param type
     *  @param localeKey
     * 
     */
    
    public void addField( final String name, final int type, final String localeKey ) {
        addField( name, type, true, localeKey );
    }
    
    /**
     *  adds a field to be validated
     * 
     *  @param name
     *  @param type
     *  @param isRequired
     * 
     */
    
    public void addField( final String name, final int type, final boolean isRequired ) {
        addField( name, type, isRequired, "" );
    }
    
    /**
     *  adds a field to be validated (error message will use locale text)
     * 
     *  @param name
     *  @param type
     *  @param isRequired
     *  @param localeKey
     * 
     */
    
    public void addField( final String name, final int type, final boolean isRequired, final String localeKey  ) {
        
        final String value = req.getArgument( name );
        
        // check for missing required fields
        if ( value.equals("") && isRequired ) {
            addError( "required field missing", localeKey  );
        }
        
        // before we check the format of the field, if it's blank
        // and not required then it's fine, so make sure we have something.
        else if ( isRequired && !value.equals("") ) {
            
            switch ( type ) {
                
                case FIELD_EMAIL:
                    if ( !isValidEmail(value) ) {
                        addError( "invalid email", localeKey  );
                    }
                    break;

                case FIELD_INTEGER:
                    if ( !isValidInteger(value) ) {
                        addError( "invalid integer", localeKey  );
                    }
                    break;

                case FIELD_DATE:
                    if ( !isValidDate(value) ) {
                        addError( "invalid date", localeKey  );
                    }
                    break;
                
                case FIELD_TEXT:
                    // any kind of text is valid
                    break;
                    
                default:
                    addError( "unknown field type (dev error?)", localeKey );
                    
            }

        }
        
    }

    /**
     *  Adds a check that two fields match
     *
     *  @param field1
     *  @param field2
     *  @param localeKey
     *
     */
    public void addMatchingFields( final String field1, final String field2, final String localeKey ) {

        final String value1 = req.getArgument( field1 );
        final String value2 = req.getArgument( field2 );

        if ( !value1.equals(value2) ) {
            addError( "fields dont match", localeKey );
        }
        
    }
    
    /**
     *  checks if this submission is valid, and if not throws an exception
     * 
     *  @throws BadRequestException
     * 
     */
    
    public void validate() throws BadRequestException {
        
        if ( !isValid() )
            throw new BadRequestException( getErrors() );
        
    }
    
    /**
     *  adds an error message using the locale text if it's specified, or just
     *  using the normal message otherwise
     * 
     *  @param message
     *  @param localeKey
     * 
     */
    
    public void addError( final String message, final String localeKey ) {
        
        errors.add(
            localeKey.equals("")
                ? message
                : locale.getString(localeKey)
        );
        
    }
    
    /**
     *  returns the errors
     * 
     *  @return
     * 
     */
    
    public String[] getErrors() {
        
        return errors.toArray( new String[] {} );
        
    }

    /**
     *  checks if a string seems to be a valid date
     * 
     *  @param value
     * 
     *  @return
     * 
     */
    
    private boolean isValidDate( final String value ) {
        
        try {
            DateFormat df = DateFormat.getDateInstance( DateFormat.SHORT );
            df.setLenient( true );
            df.parse(value);
        }
        
        catch ( final ParseException e ) {
            return false;
        }
        
        catch ( final IllegalArgumentException e ) {
            return false;
        }
        
        return true;
        
    }
    
    /**
     *  checks if a value is a valid integer
     * 
     *  @param integer
     * 
     *  @return
     * 
     */
    
    private boolean isValidInteger( final String integer ) {
       
        try {
            Integer.parseInt( integer );
            return true;
        }
        
        catch ( final NumberFormatException e ) {
            return false;
        }
                
    }
    
    /**
     *  checks if the value is a valid email
     * 
     *  @param email
     * 
     *  @return
     * 
     */
    
    private boolean isValidEmail( final String email ) {
        
        final String pattern = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";

        return email.matches( pattern );
        
    }
    
    /**
     *  determines if the submission is valid.  returns a boolean.
     * 
     *  @return boolean
     * 
     */
    
    public boolean isValid() {
        
        return ( errors.size() == 0 );
        
    }
    
}
