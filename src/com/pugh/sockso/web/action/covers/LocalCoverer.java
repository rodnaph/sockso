
package com.pugh.sockso.web.action.covers;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.Utils;
import com.pugh.sockso.cache.CacheException;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.CoverArt;
import com.pugh.sockso.cache.CoverArtCache;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashSet;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

public class LocalCoverer extends BaseCoverer {

    private static final Logger log = Logger.getLogger( LocalCoverer.class );
    
    /**
     *  Try to serve a local cover from a well known path
     * 
     *  @param itemName
     * 
     *  @return
     * 
     *  @throws SQLException
     *  @throws IOException 
     * 
     */

    public boolean serveCover( final String itemName ) throws SQLException, IOException, CacheException {

        final String localPath = getLocalCoverPath( itemName );

        if ( localPath != null ) {
            serveLocalCover( itemName, localPath );
            return true;
        }

        return false;

    }

    /**
     *  serves a local cover that the user has stored with their collection, this
     *  file may be any size so we need to resize it if it's not what we want.
     *
     *  @param itemName
     *  @param localPath
     *
     *  @throws java.io.IOException
     *
     */

    protected void serveLocalCover( final String itemName, final String localPath ) throws IOException, CacheException {

        final Properties p = getProperties();
        final BufferedImage originalImage = ImageIO.read( new File( localPath ) );
        final CoverArt cover = new CoverArt(itemName, originalImage);

        cover.scale(
                (int) p.get(Constants.DEFAULT_ARTWORK_WIDTH, 115),
                (int) p.get(Constants.DEFAULT_ARTWORK_HEIGHT, 115));

        // only cache local cover images if we've been explicitly
        // told to do so (improves performance)

        serveCover(
            cover,
            itemName,
            p.get( Constants.COVERS_CACHE_LOCAL ).equals( Properties.YES )
        );

    }

    /**
     *  returns the filename for local covers.  there are defaults for both
     *  artists ("artist") and albums ("album"), but the user can override this themselves
     * 
     *  @param itemName
     * 
     *  @return
     * 
     */
    
    protected String getLocalCoverFileName( final String itemName ) {

        final Properties p = getProperties();
        final String typeName = isArtist(itemName) ? "artist" : "album";

        return p.get(
            isArtist(itemName) ? Constants.COVERS_ARTIST_FILE : Constants.COVERS_ALBUM_FILE,
            typeName
        );

    }

     /**
     *  looks on the filesystem to see if the user has cover art stored with
     *  their music.  if found it returns the path to the file, otherwise null.
     *  The itemName is the name of the music item, eg. ar123, al456, etc...
     * 
     *  @param itemName
     * 
     *  @return
     * 
     *  @throws SQLException
     * 
     */
    
    protected String getLocalCoverPath( final String itemName ) throws SQLException {
       
        final String coverFileName = getLocalCoverFileName( itemName );
        final File[] trackDirs = getLocalCoverDirectories( itemName );
        final File[] possibleCovers = getLocalCoverFiles( trackDirs, coverFileName, isArtist(itemName) );

        for ( final File possibleCover : possibleCovers ) {
            if ( possibleCover.exists() )
                return possibleCover.getAbsolutePath();
        }

        return null;

    }

    /**
     *  determines if an item name (eg. ar123) is an artist
     * 
     *  @param itemName
     * 
     *  @return
     * 
     */
    
    protected boolean isArtist( final String itemName ) {
        
        return itemName != null
            && itemName.length() > 2
            && itemName.substring( 0, 2 ).toLowerCase().equals( "ar" );
        
    }

    /**
     *  returns unique directories associated with an itemName (eg. ar123).  this
     *  is worked out by getting all the tracks for this item, and using the
     *  directory that they're in.
     * 
     *  @param itemName (eg. ar123)
     * 
     *  @return
     * 
     *  @throws java.sql.SQLException
     * 
     */
    
    protected File[] getLocalCoverDirectories( final String itemName ) throws SQLException {

        final ArrayList<File> dirs = new ArrayList<File>();

        ResultSet rs = null;
        PreparedStatement st = null;
        
        try {

            final HashSet<String> taken = new HashSet<String>();
            final String type = itemName.substring( 0, 2 );
            final int id = Integer.parseInt( itemName.substring(2) );
            final boolean isArtist = type.equals( "ar" );
            final String typeName = isArtist ? "artist" : "album";
            final String sql = " select t.path as path " +
                               " from tracks t " +
                               " where t." +typeName+ "_id = " +id;
            final Database db = getDatabase();
            
            st = db.prepare( sql );
            rs = st.executeQuery();
        
            while ( rs.next() ) {
                final String path = rs.getString( "path" );
                if ( !taken.contains(path) ) {
                    dirs.add( new File(path) );
                    taken.add( path );
                }
            }
            
        }

        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
        return dirs.toArray( new File[0] );
        
    }
    
    /**
     *  returns an array of files to test that could possibly be local covers.
     *
     *  @param trackDirs
     *  @param coverFileName
     *
     *  @return
     *
     */

    protected File[] getLocalCoverFiles( final File[] trackDirs, final String coverFileName, final boolean isArtist ) {

        final ArrayList<File> files = new ArrayList<File>();
        final String[] exts = CoverArtCache.CACHE_IMAGE_EXTENSIONS;
        final Properties p = getProperties();

        for ( final File track : trackDirs ) {

            final String[] dirs = {
                track.getParent(),
                isArtist ? track.getParentFile().getParent() : null
            };

            // look for album info in this directory, but artist info in the
            // directory one level up as well (maybe "/My Music/artist/album/"
            // structure)
            for ( final String directory : dirs ) {
                if ( directory == null ) continue; // will be null if album
                for ( final String ext : exts ) {
                    final String path = directory + File.separator + coverFileName + "." + ext;
                    files.add( new File(path) );
                }
            }

            // Should we fallback and search for the first image
            // file in the track folder, regardless of its name ?
            if ( p.get(Constants.COVERS_FILE_FALLBACK).equals(Properties.YES) ) {
                final File fallbackFile = checkForFallbackFile( exts, track );
                if ( fallbackFile != null ) {
                    files.add( fallbackFile );
                }
            }

            
        }

        return files.toArray( new File[0] );
        
    }
    
    /**
     *  Checks for a file in the track directory to use as a fallback
     * 
     *  @param files
     *  @param exts
     *  @param track 
     * 
     */
    protected File checkForFallbackFile( final String[] exts, final File track ) {
        
        final File[] fallbackFiles = track.getParentFile().listFiles(new FileFilter() {
            @Override
            public boolean accept(File f) {
                
                if (f.isFile()) {
                    for (final String ext : exts) {
                        if (f.getName().endsWith(ext)) {
                            return true;
                        }
                    }
                }
                
                return false;
                
            }
        });

        if ( fallbackFiles != null && fallbackFiles.length > 0 ) {
            log.debug("Found " + fallbackFiles.length + " fallback cover files."
                            + " Picking first: " + fallbackFiles[0].getAbsolutePath());
            return fallbackFiles[ 0 ];
        }
        
        return null;

    }

}
