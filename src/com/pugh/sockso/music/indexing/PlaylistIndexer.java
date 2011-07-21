
package com.pugh.sockso.music.indexing;

import com.pugh.sockso.db.Database;

import java.io.FileFilter;

/**
 *  Indexes tracks for the collection
 * 
 */

public class PlaylistIndexer extends BaseIndexer {

    
    /**
     *  Constructor
     * 
     *  @param db
     * 
     */

    public PlaylistIndexer( final Database db ) {
        
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

        return " select p.id as file_id, p.path as file_path, " +
                   " i.last_modified as index_last_modified, i.id as index_id " +
               " from playlists p " +
                   " left outer join indexer i " +
                   " on (i.fid = p.id and i.type = ? )";

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

        return new PlaylistFileFilter();

    }

}
