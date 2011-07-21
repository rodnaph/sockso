
package com.pugh.sockso.music.indexing;

import com.pugh.sockso.db.Database;

import java.io.FileFilter;

/**
 *  Indexes tracks for the collection
 * 
 */

public class TrackIndexer extends BaseIndexer {

    
    /**
     *  Constructor
     * 
     *  @param db
     * 
     */

    public TrackIndexer( final Database db ) {
        
        super( db );
   
    }

    /**
     *  Returns the contents of the index
     *
     *  @return
     *
     *  @throws java.sql.SQLException
     *
     */

    protected String getFilesSql() {

        return " select t.id as file_id, t.path as file_path, " +
                   " i.last_modified as index_last_modified, i.id as index_id " +
               " from tracks t " +
                   " left outer join indexer i " +
                   " on (i.fid = t.id and i.type = ? )";

    }

    /**
     *  Returns the root folders of the collection
     * 
     *  @return
     * 
     */

    protected String getDirectoriesSql() {

        return " select c.id, c.path " +
               " from collection c ";

    }

    /**
     *  Returns a filter for the audio files we support (and directories)
     * 
     *  @return
     * 
     */

    protected FileFilter getFileFilter() {

        return new TrackFileFilter();

    }

}
