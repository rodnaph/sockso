/*
 * UploadFile.java
 * 
 * Created on Nov 18, 2007, 6:01:35 PM
 * 
 * Represents a file that has been uploaded from the user
 *
 */

package com.pugh.sockso.web;

import java.io.File;

public class UploadFile {

    private final String name, contentType, filename, data;
    private final File temporaryFile;
    
    public UploadFile( final String name, final String contentType, final String data, final String filename, final File temporaryFile ) {
        this.name = name;
        this.contentType = contentType;
        this.data = data;
        this.filename = filename;
        this.temporaryFile = temporaryFile;
    }
    
    public String getName() { return name; }
    public String getContentType() { return contentType; }
    public String getData() { return data; }
    public String getFilename() { return filename; }
    public File getTemporaryFile() { return temporaryFile; }


}
