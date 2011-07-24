
package com.pugh.sockso;

public class JsonUtils {

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
    
}
