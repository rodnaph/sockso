
package com.pugh.sockso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JsonUtils {

    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

    /**
     *  Creates a JSON string from the specified string
     * 
     *  @param string
     * 
     *  @return 
     * 
     */
    
    public static String string( final String string ) {
        
        return "\"" +string.replace("\"","\\\"")+ "\"";
        
    }

    public static String formatDate( final Date date ) {

        return new SimpleDateFormat(DATE_FORMAT).format(date);

    }
}
