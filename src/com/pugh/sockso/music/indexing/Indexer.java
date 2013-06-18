
package com.pugh.sockso.music.indexing;

import java.io.File;

public interface Indexer {


    public enum ScanFilter {
        MODIFICATION_DATE,
        NONE
    }

    public enum ScanScope {
        NEW_FILES,
        EXISTING_FILES,
        ALL_FILES
    }
    
    /**
     *  Performs a scan on the index
     *
     */
    
    public void scan();

    public void scan( ScanFilter filter, ScanScope scope );

    /**
     *  Scans a particular directory that is part of the specified collection
     *
     *  @param collectionId
     *  @param directory
     *
     *  @throws Exception
     *
     */

    public void scanDirectory( final int collectionId, final File directory ) throws Exception;

    /**
     *  Adds a listener for index events
     *
     *  @param listener
     *
     */

    public void addIndexListener( final IndexListener listener );

}
