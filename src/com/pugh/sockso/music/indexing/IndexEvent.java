
package com.pugh.sockso.music.indexing;

import java.io.File;

/**
 *  An event fired by the Indexer to indicate a some kind of change in the index
 * 
 */

public class IndexEvent {

    public enum Type {
        UNKNOWN,
        MISSING,
        CHANGED,
        COMPLETE
    }

    private final Type type;
    private final int fileId;
    private final File file;

    /**
     *  Constructor
     * 
     *  @param type
     *  @param fileId
     *  @param file
     * 
     */

    public IndexEvent( Type type, final int fileId, final File file ) {
        
        this.type = type;
        this.fileId = fileId;
        this.file = file;
        
    }

    /**
     *  Returns the event type
     * 
     *  @return
     * 
     */

    public Type getType() {

        return type;

    }

    /**
     *  Returns the file ID associated with the event
     * 
     *  @return
     * 
     */

    public int getFileId() {

        return fileId;

    }

    /**
     *  Returns the file (or directory) associated with the event
     * 
     *  @return
     * 
     */

    public File getFile() {

        return file;

    }

    /**
     *  Returns the String representation of this event
     * 
     *  @return
     * 
     */

    @Override
    public String toString() {

        return "IndexEvent, type: " +type+ ", fileId: " +fileId+ ", file: " +file.getAbsolutePath();

    }

}
