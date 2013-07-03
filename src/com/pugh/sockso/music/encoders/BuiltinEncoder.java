/*
 * The interface that built in sockso encoders need to implement
 * 
 */

package com.pugh.sockso.music.encoders;

public interface BuiltinEncoder extends Encoder {

    /**
     *  returns an array of file extensions for formats this encoder can handle
     * 
     *  @return array of file lowercase extensions
     * 
     */
    
    public String[] getSupportedFormats();
    
    /**
     *  returns the mime type of the data outputted by this encoder
     * 
     *  @return mime type
     * 
     */
    
    public String getOutputMimeType();
    
}
