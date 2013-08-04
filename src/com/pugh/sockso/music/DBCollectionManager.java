
package com.pugh.sockso.music;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.indexing.CoverArtIndexer;
import com.pugh.sockso.music.indexing.IndexEvent;
import com.pugh.sockso.music.indexing.IndexListener;
import com.pugh.sockso.music.indexing.Indexer;
import com.pugh.sockso.music.tag.AudioTag;
import com.pugh.sockso.music.tag.InvalidTagException;
import com.pugh.sockso.music.tag.Tag;
import com.pugh.sockso.web.User;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class DBCollectionManager extends Thread implements CollectionManager, IndexListener {
    
    private static final Logger log = Logger.getLogger( DBCollectionManager.class );

    private final Database db;
    private final Properties p;
    private final List<CollectionManagerListener> listeners;
    private final Indexer indexer;

    /**
     *  constructor
     *
     */

    @Inject
    public DBCollectionManager( final Database db, final Properties p, final Indexer indexer ) {

        this.db = db;
        this.p = p;
        this.indexer = indexer;

        listeners = new ArrayList<CollectionManagerListener>();

    }

    /**
     *  The indexer has detected a change in the tracks we're indexing
     *
     *  @param evt
     *
     */

    public void indexChanged( final IndexEvent evt ) {

        try {

            switch ( evt.getType() ) {

                case UNKNOWN:
                    addFile( evt.getFileId(), evt.getFile() );
                    break;

                case CHANGED:
                    checkTrack( getTrack(evt.getFileId()), evt.getFile() );
                    break;

                case MISSING:
                    removeTrack( evt.getFileId() );
                    break;

                case COMPLETE:
                    removeOrphans();
                    fireCollectionManagerEvent( CollectionManagerListener.UPDATE_COMPLETE, "Collection Updated!" );
                    break;

            }

        }

        catch ( final Throwable t ) {
            log.debug( "indexChanged error on file '" + evt.getFile().getAbsolutePath() + "'", t );
        }

    }

    /**
     *  Extracts a track by ID
     *
     *  @param trackId
     *
     *  @return
     *
     *  @throws java.sql.SQLException
     *
     */

    protected Track getTrack( final int trackId ) throws SQLException {
        
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
        
            final String sql = Track.getSelectFromSql() +
                               " where t.id = ? ";

            st = db.prepare( sql );
            st.setInt( 1, trackId );
            rs = st.executeQuery();

            if ( !rs.next() ) {
                throw new SQLException( "Invalid track id" );
            }

            return Track.createFromResultSet( rs );

        }

        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
    }

    /**
     *  Tries to add an audio file to the collection, creating artist/album info
     *  if required.
     *
     *  @param collectionId
     *  @param file
     *
     *  @throws com.pugh.sockso.music.tag.InvalidTagException
     *  @throws java.io.IOException
     *
     */

    protected void addFile( final int collectionId, final File file ) throws InvalidTagException, IOException {

        final Tag tag = AudioTag.getTag( file );

	log.debug( tag.toString() );

        final int artistId = addArtist( tag.getArtist() );
        int albumArtistId = artistId;

        if ( tag.getAlbumArtist() != null && tag.getAlbumArtist().equals("") ) {
            albumArtistId = addArtist( tag.getAlbumArtist() );
        }

        final int albumId  = addAlbum( albumArtistId, tag.getAlbum(), tag.getAlbumYear() );
        final int genreId  = addGenre( tag.getGenre() );
        final int trackId  = addTrack( artistId, albumId, tag.getTrack(),
                tag.getTrackNumber(), file, collectionId, genreId );

        if ( Utils.isFeatureEnabled( p, Constants.COLLMAN_SCAN_COVERS ) ) {

            final BufferedImage coverArt = tag.getCoverArt();
            
            if ( coverArt != null ) {
                addCoverArt( albumArtistId, "ar", coverArt );
                addCoverArt( albumId, "al", coverArt );
                addCoverArt( trackId, "tr", coverArt );
            }
        }

    }

    /**
     *  Adds cover art extracted from the tag for this track
     * 
     *  @param itemId
     *  @param itemType 
     *  @param coverArt 
     * 
     */

    protected void addCoverArt( final int itemId, final String itemType, final BufferedImage coverArt ){

        log.debug("Adding Cover Art for itemId: " + itemId + ", itemType: " + itemType);

        // -1 if nothing inserted into db
        if (itemId < 0) {
            log.warn("addCoverArt: itemId param was -1");
            return;
        }

        // TODO Would be nice if we had some sort of helper to build a coverId from arbitrary music item types
        String coverId = itemType + itemId;

        final CoverArtIndexer coverArtIndexer = new CoverArtIndexer(p);
        coverArtIndexer.indexCover(new CoverArt( coverId, coverArt ));
    }

    /**
     *  Scans a folder for new files
     *
     *  @param collectionId
     *  @param directory
     *
     */
    
    public void scanDirectory( final int collectionId, final File directory ) {

        try {
            indexer.scanDirectory( collectionId, directory );
        }

        catch ( final Exception e ) {
            log.error( e );
        }

    }
    
    /**
     *  checks the collection for updates.  it actually does 2 scans, one
     *  to check for new files, and the second to check the files in the
     *  collection are still there.
     *
     */
    
    public void checkCollection() {

        indexer.scan();

    }

    /**
     * Re-reads tag information from files
     */
    public void rescanTags() {

        indexer.scan(Indexer.ScanFilter.NONE, Indexer.ScanScope.ALL_FILES);
        
    }

    /**
     *  checks if the album tag information has changed, if it has then updates
     *  the database.
     * 
     *  @param artistId
     *  @param tag
     *  @param track
     * 
     *  @throws java.sql.SQLException
     * 
     */
    
    protected void checkAlbumTagInfo( final int artistId, final Tag tag, final Track track ) throws SQLException {

        // need to ignore case because that's how the DB does it
        if ( !track.getAlbum().getName().equalsIgnoreCase(tag.getAlbum()) ||
             !track.getAlbum().getYear().equalsIgnoreCase(tag.getAlbumYear()) ) {

            ResultSet rs = null;
            PreparedStatement st = null;

            try {

                // if the album has changed, first try and fetch an album
                // for this artist of this new name to tag track to...
                String sql = " select id " +
                             " from albums " +
                             " where name = ? " +
                                 " and artist_id = ? ";

                st = db.prepare( sql );
                st.setString( 1, tag.getAlbum() );
                st.setInt( 2, artistId );
                rs = st.executeQuery();

                final int newAlbumId = rs.next()
                    ? rs.getInt( "id" )
                    : addAlbum( artistId, tag.getAlbum(), tag.getAlbumYear() );

                Utils.close( rs );
                Utils.close( st );
                
                // then update track
                sql = " update tracks " +
                      " set album_id = ? " +
                      " where id = ? ";
                
                st = db.prepare( sql );
                st.setInt( 1, newAlbumId );
                st.setInt( 2, track.getId() );
                st.execute();
                
                Utils.close( rs );
                Utils.close( st );

                sql = " update albums " +
                      " set year = ? " +
                      " where id = ? ";

                st = db.prepare( sql );
                st.setString( 1, tag.getAlbumYear() );
                st.setInt( 2, newAlbumId );
                st.execute();

            }
            
            finally {
                Utils.close( rs );
                Utils.close( st );
            }
                
        }
        
    }

    /**
     *  Checks if the artist information has changed.
     *  If it has changed, then the database is updated and the new artist id is
     *  returned.
     *  Otherwise, the artist id that's returned will be the one from the track
     *  that hasn't changed.
     *
     *  @param tag
     *  @param track
     * 
     *  @return
     * 
     *  @throws java.sql.SQLException
     *
     */

    protected int checkArtistTagInfo( final Tag tag, final Track track ) throws SQLException {

        // need to ignore case because that's how the DB does it
        if ( !track.getArtist().getName().equalsIgnoreCase(tag.getArtist()) ) {

            PreparedStatement st = null;
            ResultSet rs = null;
            
            try {
            
                // if the artist has changed, first try and fetch an artist
                // of this new name to tag track to...
                String sql = " select id " +
                             " from artists " +
                             " where name = ? ";
                
                st = db.prepare( sql );
                st.setString( 1, tag.getArtist() );
                rs = st.executeQuery();

                final int newArtistId = rs.next()
                    ? rs.getInt("id")
                    : addArtist(tag.getArtist());

                Utils.close( rs );
                Utils.close( st );
                
                // then update track
                sql = " update tracks " +
                      " set artist_id = ? " +
                      " where id = ? ";
                
                st = db.prepare( sql );
                st.setInt( 1, newArtistId );
                st.setInt( 2, track.getId() );
                st.execute();

                Utils.close( rs );
                Utils.close( st );

                sql = " update albums " +
                      " set artist_id = ? " +
                      " where id = ? ";

                st = db.prepare( sql );
                st.setInt( 1, newArtistId );
                st.setInt( 2, track.getAlbum().getId() );
                st.execute();

                return newArtistId;
            
            }
            
            finally {
                Utils.close( rs );
                Utils.close( st );
            }

        }

        // name not changed, but make sure browse_name is up to date
        // @TODO - maybe it'd be better to extract the browse_name with the artist
        // information (in Track), then we can check if it needs changing...
        // is this more unneeded overhead though?
        else {
            updateArtistBrowseName( track.getArtist().getId(), tag.getArtist() );
        }

        return track.getArtist().getId();
        
    }

    /**
     *  Updates the artists browse name from the real name
     * 
     *  @param artistId
     *  @param realName
     * 
     *  @throws java.sql.SQLException
     * 
     */

    protected void updateArtistBrowseName( final int artistId, final String realName ) throws SQLException {

        PreparedStatement st = null;

        try {

            final String browseName = getArtistBrowseName( getArtistPrefixesToRemove(), realName );
            final String sql = " update artists " +
                               " set browse_name = ? " +
                               " where id = ? ";

            st = db.prepare( sql );
            st.setString( 1, browseName );
            st.setInt( 2, artistId );

            st.execute();

        }

        finally {
            Utils.close( st );
        }

    }

    /**
     *  checks if a tracks tag has changed, and updates the database with the
     *  new information if it has.
     * 
     *  @param tag
     *  @param track
     * 
     *  @throws java.sql.SQLException
     * 
     */

    protected void checkTrackTagInfo( final Tag tag, final Track track ) throws SQLException {

        if ( !track.getName().equals(tag.getTrack()) || (track.getNumber() != tag.getTrackNumber()) ) {

            PreparedStatement st = null;

            try {

                final String sql = " update tracks " +
                                   " set name = ?, " +
                                       " track_no = ? " +
                                   " where id = ? ";

                st = db.prepare( sql );
                st.setString( 1, tag.getTrack() );
                st.setInt( 2, tag.getTrackNumber() );
                st.setInt( 3, track.getId() );
                st.execute();

            }
            
            finally {
                Utils.close( st );
            }

        }

    }

    /**
     * checks the genre's tag has changed and updates the database with the
     * new information if it has.
     *
     * @param tag
     * @param track
     *
     * @throws java.sql.SQLException
     *
     */
    
    private void checkGenreTagInfo( final Tag tag, final Track track ) throws SQLException {

        final String genre = ( track.getGenre() == null ? null : track.getGenre().getName() );

        if ( !tag.getGenre().equalsIgnoreCase(genre) ) {

            PreparedStatement st = null;
            ResultSet rs = null;

            try {

                // if the genre has changed, first try and fetch the genre
                // of this new name to tag track to...
                String sql = " select id " +
                             " from genres " +
                             " where name = ? ";

                st = db.prepare( sql );
                st.setString( 1, tag.getGenre() );
                rs = st.executeQuery();

                final int newGenreId = rs.next()
                    ? rs.getInt("id")
                    : addGenre(tag.getGenre());

                Utils.close( rs );
                Utils.close( st );

                // then update the track with the new genre
                sql = " update tracks " +
                      " set genre_id = ? " +
                      " where id = ? ";

                st = db.prepare( sql );
                st.setInt( 1, newGenreId );
                st.setInt( 2, track.getId() );
                st.execute();

            }

            finally {
                Utils.close( rs );
                Utils.close( st );
            }

        }

    }

    /**
     *  checks that a track is up to date with the tag information of it's
     *  file on disk (it may have been edited between updates to the collection)
     *
     *  @param track the track to check
     *  @param file the audio file on disk
     * 
     */
    
    private void checkTrack( final Track track, final File file ) {

        try {
            
            final Tag tag = AudioTag.getTag( file );

            // has track info changed?
            checkTrackTagInfo( tag, track );
            
            // has the artist information changed?  if it has we'll get a new
            // artist id, otherwise we'll get the same one as the track is
            // assigned to when we passed in
            final int artistId = checkArtistTagInfo( tag, track );

            // has album info changed?
            checkAlbumTagInfo( artistId, tag, track );

            // has the genre info changed?
            checkGenreTagInfo( tag, track );

        }

        catch ( SQLException e ) { log.debug(e); }
        catch ( InvalidTagException e ) { log.debug(e); }
        catch ( IOException e ) { log.debug(e); }
        
    }
    
    /**
     *  Given an artists name, removes any prefixes we've been asked to.
     *
     *  @param prefixes
     *  @param name
     * 
     *  @return
     * 
     */

    protected String getArtistBrowseName( final String[] prefixes, final String name ) {

        for ( final String prefix : prefixes ) {
            if ( name.substring(0,prefix.length()).equalsIgnoreCase(prefix) ) {
                return name.substring( prefix.length() );
            }
        }

        return name;

    }

    /**
     *  Returns an array of the artist prefixes we need to remove
     * 
     *  @return
     * 
     */

    protected String[] getArtistPrefixesToRemove() {

        return p.get( Constants.COLLMAN_ARTIST_REMOVE_PREFIXES ).split( "," );

    }

    /**
     *  removes a track from the collection
     * 
     *  @param s statement object to use
     *  @param trackId the track id to remove
     * 
     */

    protected void removeTrack( final int trackId ) throws SQLException {

        String sql = " delete from play_log " +
                " where track_id = '" +trackId+ "' ";
        db.update( sql );

        sql = " delete from playlist_tracks " +
                " where track_id = '" +trackId+ "' ";
        db.update( sql );

        sql = " delete from tracks " +
                " where id = '" +trackId+ "' ";
        db.update( sql );
                
    }
    
    /**
     *  allows components to register for collection activity messages
     * 
     *  @param listener the listener to register
     *
     */
    
    public void addCollectionManagerListener( final CollectionManagerListener listener ) {

        listeners.add( listener );

    }
    
    /**
     *  signals all listeners that a collection manager event
     *  has just occurred
     * 
     *  @param type the event type
     *  @param message the event description
     * 
     */
    
    public void fireCollectionManagerEvent( final int type, final String message ) {

        for ( final CollectionManagerListener listener : listeners ) {
            listener.collectionManagerChangePerformed( type, message );
        }

    }

    /**
     *  adds a directory to the database and returns it's new collectionId
     * 
     *  @param dir
     * 
     *  @return
     * 
     *  @throws java.sql.SQLException
     *  @throws java.sql.SQLException
     * 
     */
    
    protected int addDirectoryToDb( final File dir ) throws SQLException, SQLException {

        ResultSet rs = null;
        PreparedStatement st = null;
        
        try {

            // add to the database
            String sql = " insert into collection ( path ) " +
                         " values ( ? ) ";
            
            st = db.prepare( sql );
            st.setString( 1, Utils.getPathWithSlash(dir) );
            st.execute();

            Utils.close( st );
            
            // extract new id
            sql = " select max(c.id) as new_id " +
                  " from collection c ";
            
            st = db.prepare( sql );
            rs = st.executeQuery();

            if (!rs.next())
                throw new SQLException("unable to retrieve new id");

            return rs.getInt("new_id");
            
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }

    }

    /**
     *  adds a directory to the collection
     * 
     *  @param dir the directory to add
     *
     */
    
    public int addDirectory( final File dir ) {
        
        try {

            // add to database
            int collectionId = addDirectoryToDb( dir );

            indexer.scanDirectory( collectionId, dir );

            removeOrphans();

            fireCollectionManagerEvent( CollectionManagerListener.UPDATE_COMPLETE, "Update Finished" );

            return collectionId;
            
        }

        catch ( final Exception e ) {
            log.error( "Error adding folder to collection: " + e.getMessage() );
            fireCollectionManagerEvent( CollectionManagerListener.ERROR, e.getMessage() );
        }
        
        return -1;
        
    }
    
    /**
     *  adds an artist to the collection (if it doesn't already
     *  exist) and returns its id
     *
     */

    private int addArtist( String name ) {

        if ( name.equals("") ) {
            name = "Unknown Artist";
        }

        ResultSet rs = null;
        PreparedStatement st = null;

        try {

            try {
                
                final String browseName = getArtistBrowseName( getArtistPrefixesToRemove(), name );

                st = db.prepare(
                    " insert into artists ( name, date_added, browse_name ) " +
                    " values ( ?, current_timestamp, ? ) "
                );
                st.setString( 1, name );
                st.setString( 2, browseName );
                st.execute();
                
                log.debug( "Added Artist: " + name );
                
            }
            catch ( final Exception e ) {}
            finally {
                Utils.close( st );
            }

            st = db.prepare(
                " select id " +
                " from artists " +
                " where name = ? "
            );
            st.setString( 1, name );
            rs = st.executeQuery();

            if ( rs.next() ) {
                fireCollectionManagerEvent( CollectionManagerListener.ARTIST_ADDED, name );
                return rs.getInt( "id" );
            }
        }
        
        catch ( final Exception e ) {
            log.error( "Error Adding Artist: " + e );
        }

        finally {
            Utils.close( rs );
            Utils.close( st );
            System.gc();
        }
        
        return -1;

    }

    /**
     *  adds an album to the collection (if it doesn't already
     *  exist) and returns its id
     *
     */

    private int addAlbum( final int artistId, String name, String year ) {

        if ( name.equals("") ) {
            name = "Unknown Album";
        }

        ResultSet rs = null;
        PreparedStatement st = null;

        try {

            try {
                
                st = db.prepare(
                        " insert into albums ( artist_id, name, year, date_added ) " +
                        " values ( ?, ?, ?, current_timestamp ) "
                );
                st.setInt( 1, artistId );
                st.setString( 2, name );
                st.setString(3, year);
                st.execute();
                
                log.debug( "Added Album: " + name + " " + year );
                
            }
            catch ( final Exception e ) {}
            finally {
                Utils.close( st );
            }

            final String sql = " select id " +
                               " from albums " +
                               " where artist_id = ? " +
                                   " and name = ? ";
            
            st = db.prepare( sql );
            st.setInt( 1, artistId );
            st.setString( 2, name );
            rs = st.executeQuery();

            if ( rs.next() ) {
                fireCollectionManagerEvent( CollectionManagerListener.ALBUM_ADDED, name );
                return rs.getInt( "id" );
            }

        }
        
        catch ( final Exception e ) {
            log.error( "Error Adding Album (" + name + "): " + e.getMessage() );
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
        return -1;

    }


    /**
     *  adds a genre to the collection (if it doesn't already
     *  exist) and returns its id
     *
     */

    private int addGenre( String name ) {

        if ( name.equals("") ) {
            name = "Unknown Genre";
        }

        ResultSet rs = null;
        PreparedStatement st = null;

        try {

            try {

                st = db.prepare(
                    " insert into genres ( name ) " +
                    " values ( ? ) "
                );
                st.setString( 1, name );
                st.execute();

                log.debug( "Added Genre: " + name );

            }
            catch (Exception e) {}
            finally {
                Utils.close( st );
            }

            st = db.prepare(
                " select id " +
                " from genres " +
                " where name = ? "
            );
            st.setString( 1, name );
            rs = st.executeQuery();

            if ( rs.next() ) {
                fireCollectionManagerEvent( CollectionManagerListener.GENRE_ADDED, name );
                return rs.getInt( "id" );
            }
        }

        catch ( final Exception e ) {
            log.error( "Error Adding Genre: " + e );
        }

        finally {
            Utils.close( rs );
            Utils.close( st );
        }

        return -1;

    }

    /**
     *  adds a track to the collection (if it doesn't already
     *  exist) and returns its id
     *
     */

    private int addTrack( final int artistId, final int albumId, String name,
            final int trackNo, final File file, final int collectionId, final int genreId ) {

        if ( name.equals("") )
            name = "Unknown Track (" + trackNo + ")";

        ResultSet rs = null;
        PreparedStatement st = null;
        
        try {

            try {

                final String sql = " insert into tracks ( artist_id, album_id, name, path, " +
                                   " length, collection_id, date_added, track_no, genre_id ) " +
                                   " values ( ?, ?, ?, ?, 100, ?, current_timestamp, ?, ? ) ";

                st = db.prepare( sql );
                st.setInt( 1, artistId );
                st.setInt( 2, albumId );
                st.setString( 3, name );
                st.setString( 4, file.getAbsolutePath() );
                st.setInt( 5, collectionId );
                st.setInt( 6, trackNo );
                st.setInt( 7, genreId);
                st.execute();

                log.debug( "Added Track: " + name );

            }
            catch ( final Exception e ) {}
            
            finally {
                Utils.close( st );
            }

            final String sql = " select id " +
                " from tracks " +
                " where artist_id = ? " +
                    " and album_id = ? " +
                    " and name = ? ";
            
            st = db.prepare( sql );
            st.setInt( 1, artistId );
            st.setInt( 2, albumId );
            st.setString( 3, name );
            rs = st.executeQuery();

            if ( rs.next() ) {
                fireCollectionManagerEvent( CollectionManagerListener.TRACK_ADDED, name );
                return rs.getInt( "id" );
            }

        }
        
        catch ( final Exception e ) {
            log.error( "Error Adding Track: " + e.getMessage() );
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
        return -1;
        
    }
    
    /**
     *  removes a directory from the collection
     *
     *  @param path the path of the directory to remove
     * 
     */
    
    public boolean removeDirectory( final String path ) {

        ResultSet rs = null;
        PreparedStatement st = null;

        try {

            // first we need to get the collection id
            String sql = " select id " +
                         " from collection c " +
                         " where path = ? ";
            st = db.prepare( sql );
            st.setString( 1, Utils.getPathWithSlash(path) );
            rs = st.executeQuery();

            if ( rs.next() ) {

                final int collectionId = rs.getInt( "id" );

                Utils.close( rs );
                Utils.close( st );
                
                // remove items from the play_log
                sql = " delete from play_log " +
                        " where track_id in ( " +
                            " select id " +
                            " from tracks t " +
                            " where collection_id = ? " +
                        " ) ";
                st = db.prepare( sql );
                st.setInt( 1, collectionId );
                st.execute();
                
                Utils.close( st );

                // remove tracks from playlists
                sql = " delete from playlist_tracks " +
                        " where track_id in ( select id " +
                            " from tracks " +
                            " where collection_id = ? ) ";
                st = db.prepare( sql );
                st.setInt( 1, collectionId );
                st.execute();
                
                Utils.close( st );

                // remove tracks from the collection
                sql = " delete from tracks " +
                        " where collection_id = ? ";
                st = db.prepare( sql );
                st.setInt( 1, collectionId );
                st.execute();
                
                Utils.close( st );

                // remove the collection
                sql = " delete from collection " +
                        " where id = ? ";
                st = db.prepare( sql );
                st.setInt( 1, collectionId );
                st.execute();

                Utils.close( st );

                removeOrphans();
                fireCollectionManagerEvent( CollectionManagerListener.UPDATE_COMPLETE, "Directory Removed" );

                return true;

            }            
            
        }
        
        catch ( final SQLException e ) {
            log.error( e.getMessage() );
        }

        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
        return false;
        
    }

    /**
     * Clean up unreferenced artists, albums, genres from database
     *
     * @throws SQLException
     */
    protected void removeOrphans() throws SQLException {

        removeOrphanedArtists();
        removeOrphanedAlbums();
        removeOrphanedGenres();

    }

    /**
     *  Removes any artists from the collection that don't
     *  have any tracks associated with them
     *
     *  @throws SQLException
     *
     */
    
    protected void removeOrphanedArtists() throws SQLException {

        // remove any artists left without tracks OR albums
        // (artist can be an "album artist" and have zero tracks)
        String sql = " delete from artists " +
                     " where id not in ( select artist_id from albums ) " +
                     "   and id not in ( select artist_id from tracks ) ";

        db.update( sql );

    }

    /**
     *  Removes any albums from the collection that don't
     *  have any tracks associated with them
     *
     *  @throws SQLException
     *
     */

    protected void removeOrphanedAlbums() throws SQLException {

        // remove any albums left without tracks
        String sql = " delete from albums " +
                     " where id not in ( select album_id " +
                                       " from tracks ) ";

        db.update( sql );

    }

    /**
     *  removes any albums from the collection that don't
     *  have any tracks associated with them
     *
     *  @throws SQLException
     *
     */

    protected void removeOrphanedGenres() throws SQLException {

        // remove any genres left without tracks
        String sql = " delete from genres " +
                     " where id not in ( select genre_id " +
                                       " from tracks ) ";
        db.update( sql );

    }

    public int savePlaylist( final String name, final Track[] tracks ) {

        return savePlaylist( name, tracks, null );
        
    }

    /**
     *  saves a playlist for a user to the collection
     * 
     *  @param name the name of the playlist
     *  @param tracks track ids for the playlist
     * 
     */
    
    public int savePlaylist( final String name, final Track[] tracks, final User user ) {
        
        ResultSet rs = null;
        PreparedStatement st = null;

        try {
            
            int playlistId = -1;
            
            // see if old playlist exists

            String sql = " select id " +
                         " from playlists p " +
                         " where name = ? ";
            st = db.prepare( sql );
            st.setString( 1, name );
            rs = st.executeQuery();
            if ( rs.next() )
                removePlaylist( rs.getInt("id") );
            
            Utils.close( rs );
            Utils.close( st );

            // create playlist

            sql = " insert into playlists ( name, user_id, date_created, date_modified ) " +
                    " values ( ?, ?, current_timestamp, current_timestamp ) ";
            st = db.prepare( sql );
            st.setString( 1, name );
            if ( user == null )
                st.setNull( 2, Types.INTEGER );
            else
                st.setInt( 2, user.getId() );
            st.execute();

            Utils.close( rs );
            Utils.close( st );
            
            // fetch new id
            
            sql = " select max(p.id) as new_id " +
                  " from playlists p ";
            st = db.prepare( sql );
            rs = st.executeQuery();
            if ( rs.next() )
                playlistId = rs.getInt( "new_id" );
            else
                throw new SQLException( "couldn't get new playlist id" );
            
            Utils.close( rs );
            Utils.close( st );

            // then add tracks to playlist

            sql = " insert into playlist_tracks ( playlist_id, track_id ) " +
                    " values ( ?, ? ) ";
            st = db.prepare( sql );

            for ( final Track track : tracks ) {
            
                st.setInt( 1, playlistId );
                st.setInt( 2, track.getId() );
                st.execute();
                
            }

            fireCollectionManagerEvent( CollectionManagerListener.PLAYLISTS_CHANGED, name  );
            
            return playlistId;

        }
        
        catch ( final SQLException e ) {
            log.error( e.getMessage() ); 
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
        return -1;

    }
    
    /**
     *  tries to remove a playlist from the collection, returns a boolean
     *  indicating if it was successful
     * 
     *  @param id id of playlist to remove
     *  @return boolean indicating success
     * 
     */
    
    public boolean removePlaylist( final int id ) {

        PreparedStatement st = null;
        
        try {

            String sql = " delete from playlist_tracks " +
                         " where playlist_id = ? ";
            
            st = db.prepare( sql );
            st.setInt( 1, id );
            st.execute();

            Utils.close( st );

            sql = " delete from playlists " +
                  " where id = ? ";
            st = db.prepare( sql );
            st.setInt( 1, id );
            st.execute();

            fireCollectionManagerEvent( CollectionManagerListener.PLAYLISTS_CHANGED, "Playlist removed"  );
            
        }
        
        catch ( final SQLException e ) {
            log.error( e );
            return false;
        }

        finally {
            Utils.close( st );
        }
        
        return true;
        
    }
    
}
