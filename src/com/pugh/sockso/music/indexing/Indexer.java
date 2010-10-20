
package com.pugh.sockso.music.indexing;

import java.io.File;

public interface Indexer {

    /**
     *  Performs a scan on the index
     *
     */
    
    public void scan();

    /**
     *  Scans a particluar directory that is part of the specified collection
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
