
package com.pugh.sockso.music.indexing;

import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileFilter;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 *  Scans and indexes files in the collection.
 *
 *  @TODO - need to be able to handle requests to scan when we're already
 *  scanning the collection.
 *
 *  @TODO - find way to index modified time of files on first scan
 *
 */

public abstract class BaseIndexer implements Indexer {

    public static Logger log = Logger.getLogger( BaseIndexer.class );

    private final List<IndexListener> indexListeners;

    private final Database db;
    private final IndexCache cache;

    private AtomicBoolean isIndexing;

    /**
     *  Constructor
     * 
     *  @param db
     * 
     */

    public BaseIndexer( final Database db ) {

        this( db, new IndexCache() );

    }

    /**
     *  Constructor, allows injecting the IndexCache to use
     * 
     *  @param db
     *  @param cache
     * 
     */

    public BaseIndexer( final Database db, final IndexCache cache ) {

        this.db = db;
        this.cache = cache;

        indexListeners = new ArrayList<IndexListener>();
        isIndexing = new AtomicBoolean(false);
        
    }

    /**
     *  Returns a FileFilter for the specific type of files that are to be
     *  indexed - BUT should also return directories assuming these are to
     *  be scanned recursively
     * 
     *  @return
     * 
     */
    
    protected abstract FileFilter getFileFilter();

    /**
     *  Returns the sql to use to extract the file information
     *
     *  @return
     *
     */

    protected abstract String getFilesSql();

    /**
     *  Returns the sql to use to extract the root directories to scan
     *
     *  @return
     *
     */

    protected abstract String getDirectoriesSql();

    /**
     *  scans for changes on disk (both new and updated files).  IndexEvent
     *  events are then fired to listeners.
     * 
     */

    @Override
    public void scan() {
        
        scan( ScanFilter.MODIFICATION_DATE, ScanScope.ALL_FILES );
    }


    public void scan( final ScanFilter filter, final ScanScope scope ) {

        if ( isIndexing.compareAndSet( false, true ) ) {

            final long start = System.currentTimeMillis();

            log.debug( "scan() starting..." );

            checkIntegrity( filter );

            log.debug( "integrity check done: " + (System.currentTimeMillis() - start) );

            if ( !ScanScope.EXISTING_FILES.equals(scope) ) {
                checkForNewFiles();
            }

            log.debug( "scan() finished: " + (System.currentTimeMillis() - start) );

            fireIndexEvent(
                new IndexEvent( IndexEvent.Type.COMPLETE, -1, new File("") )
            );

            isIndexing.set( false );
        }

    }

    /**
     *  Checks for new files to add to the index.  This assumes that the integrity
     *  check has already been run so the cache is up to date with all the files
     *  we currently have in the collection.
     *
     *  New files are signaled with a IndexEvent.UNKNOWN event.
     * 
     */

    protected void checkForNewFiles() {

        ResultSet rs = null;
        
        try {

            updateCache();

            rs = getDirectories();

            while ( rs.next() ) {
                scan( rs.getInt("id"), new File(rs.getString("path")) );
            }

        }

        catch ( final SQLException e ) {
            log.error( e );
        }

        finally {
            Utils.close( rs );
        }
        
    }

    /**
     *  Checks the files current in the index still are, if they are not then
     *  a IndexEvent.MISSING event is fired.
     *
     * If they appear changed then a IndexEvent.CHANGED event is fired.
     * 
     */

    protected void checkIntegrity( final ScanFilter filter ) {

        ResultSet rs = null;

        try {

            rs = getFiles();

            while ( rs.next() ) {

                final String path = rs.getString( "file_path" );
                final int id = rs.getInt( "file_id" );
                final File file = new File( path );

                if ( checkExists(file, id) ) {

                    boolean changed = false;

                    if ( ScanFilter.MODIFICATION_DATE.equals(filter) ) {
                        changed = checkModified(file, rs.getDate("index_last_modified"));
                    }
                    else if ( ScanFilter.NONE.equals(filter) ) {
                        changed = true;
                    }

                    if ( changed ) {
                        fireIndexEvent(new IndexEvent(IndexEvent.Type.CHANGED, id, file));
                        markFileModified(id, rs.getInt("index_id"));
                    }

                }
                
            }

        }
        
        catch ( final SQLException e ) {
            log.debug( e );
        }

        finally {
            Utils.close( rs );
        }

    }

    /**
     *  Updates the cache with the files we have indexed
     * 
     *  @throws java.sql.SQLException
     * 
     */

    protected void updateCache() throws SQLException {
        
        ResultSet rs = null;
        
        try {
            
            cache.clear();

            rs = getFiles();
            
            while ( rs.next() ) {
                cache.add( rs.getString("file_path") );
            }

        }
                
        finally {
            Utils.close( rs );
        }
        
    }

    /**
     *  Scans a directory for new files, but as the index requires the IndexCache
     *  to work properly runs an integrity check (should be fast)
     *
     *  @param directoryId
     *  @param directory
     *
     */
    
    @Override
    public void scanDirectory( final int directoryId, final File directory ) throws SQLException {

        updateCache();

        scan( directoryId, directory );

    }

    /**
     *  Scans a directory (and all sub-directories) for new files to index.
     *
     *  This uses the IndexCache to check if files already exist.  This will
     *  be populated with the results of the last index integrity scan.
     *
     *  @param directoryId
     *  @param directory
     *
     */

    protected void scan( final int directoryId, final File directory ) {

        if ( !directory.exists() || !directory.canRead() ) return; // make sure the directory exists

        for ( final File file : directory.listFiles(getFileFilter()) ) {

            if ( file.isDirectory() ) {
                scan( directoryId, file );
            }

            else if ( !cache.exists(file.getAbsolutePath()) ) {
                fireIndexEvent(
                    new IndexEvent( IndexEvent.Type.UNKNOWN, directoryId, file )
                );
            }

        }

    }

    /**
     *  returns a ResultSet with the root directories to scan
     *
     *  @return
     *
     *  @throws java.sql.SQLException
     *
     */

    protected ResultSet getDirectories() throws SQLException {
        
        final String sql = getDirectoriesSql();
        final PreparedStatement st = db.prepare( sql );
        
        return st.executeQuery();
        
    }

    /**
     *  Returns the contents of the index
     *
     *  @return
     *
     *  @throws java.sql.SQLException
     *
     */

    protected ResultSet getFiles() throws SQLException {

        final String sql = getFilesSql();
        final PreparedStatement st = db.prepare( sql );

        return st.executeQuery();

    }

    /**
     *  marks a file in the index as being changed.  if the file hasn't been indexed
     *  yet then the indexId should be -1
     *
     *  @param id
     *
     *  @return true if index updated, false otherwise
     *
     */

    public boolean markFileModified( final int fileId, final int indexId ) {

        PreparedStatement st = null;

        try {

            String sql = "";

            if ( indexId == 0 ) {
                sql = " insert into indexer ( id, last_modified ) " +
                      " values ( ?, current_timestamp ) ";
                st = db.prepare( sql );
                st.setInt( 1, fileId );
            }

            else {
                sql = " update indexer " +
                      " set last_modified = current_timestamp " +
                      " where id = ? ";
                st = db.prepare( sql );
                st.setInt( 1, indexId );
            }

            st.execute();

            return true;

        }

        catch ( final SQLException e ) {
            log.debug( e );
        }

        finally {
            Utils.close( st );
        }

        return false;

    }

    /**
     *  Checks if an indexed file exists.  If it doesn't then an IndexEvent.TRACK_MISSING
     *  event is fired and false returned.
     * 
     *  @param file
     *  @param id
     * 
     *  @return true if file exists, false otherwise
     * 
     *  @throws java.sql.SQLException
     * 
     */

    protected boolean checkExists( final File file, final int id ) throws SQLException {

        if ( !file.exists() ) {
            fireIndexEvent(
                new IndexEvent( IndexEvent.Type.MISSING, id, file )
            );
            return false;
        }

        return true;

    }

    /**
     *  Checks if a file has been modified.  If it has then an IndexEvent.Type.CHANGED
     *  event is fired and true returned.
     * 
     *  @param file
     *  @param id
     *  @param lastModified
     * 
     *  @return true if file modified, false otherwise
     * 
     *  @throws java.sql.SQLException
     * 
     */

    protected boolean checkModified( final File file, final Date lastModified ) {
        
        return ( lastModified == null || lastModified.getTime() < file.lastModified() );

    }

    /**
     *  Fires an IndexEvent to all listeners
     *
     *  @param evt
     *
     */

    protected void fireIndexEvent( final IndexEvent evt ) {

        log.debug( evt );

        for ( IndexListener listener : indexListeners ) {
            listener.indexChanged( evt );
        }

    }

    /**
     *  Adds a listener for index events
     *
     *  @param listener
     *
     */

    @Override
    public void addIndexListener( final IndexListener listener ) {

        indexListeners.add( listener );

    }

}
