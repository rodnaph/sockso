/*
 * Downloader.java
 * 
 * Allows downloading of single files, or multiple files packed
 * up into zip archives.
 *  
 */

package com.pugh.sockso.web.action;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

public class Downloader extends BaseAction {
    
    private static final String VARIOUS_ARTISTS = "various_artists";
    private static final String VARIOUS_ALBUMS = "various_albums";

    private static final String DEFAULT_ARTIST = "artist";
    private static final String DEFAULT_ALBUM = "album";
    
    private static final Logger log = Logger.getLogger( Downloader.class );

    /**
     *  handles the "download" command.  allows the downloading of single files
     *  or multiple files in archives
     * 
     *  @param req the request object
     *  @param res the response object
     *  @param user current user
     * 
     *  @throws SQLException
     *  @throws IOException
     *  @throws BadRequestException
     * 
     */
    
    public void handleRequest() throws SQLException, IOException, BadRequestException {

        final Request req = getRequest();
        final Response res = getResponse();
        final Locale locale = getLocale();
        final Properties p = getProperties();

        if ( p.get(Constants.WWW_DOWNLOADS_DISABLE).equals(p.YES) )
            throw new BadRequestException( locale.getString("www.error.downloadsDisabled"), 403 );
        
        final Database db = getDatabase();
        final String[] args = req.getPlayParams( false );
        final Vector<Track> tracks = Track.getTracksFromPlayArgs( db, args );
        final String fileName = getFileName( tracks );

        res.addHeader( "Content-length", Long.toString(getContentLength(tracks)) );
        res.addHeader( "Content-type", "application/zip" );
        res.addHeader( "Content-Disposition", "inline; filename=\"" + fileName + "\"" );
        res.sendHeaders();
 
        ZipOutputStream zip = null;
        
        try {
            
            zip = new ZipOutputStream( res.getOutputStream() );

            for ( final Track track : tracks )
                addTrackToZip( zip, track );

        }

        finally { zip.close(); }
        
    }
    
    /**
     *  returns the size in bytes of all the tracks in the vector
     * 
     *  @param tracks the tracks to get the size for
     *  @return the total size
     * 
     */
    
    private long getContentLength( final Vector<Track> tracks ) {
        
        long total = 0;
        
        for ( final Track track : tracks )
            total += new File(track.getPath()).length();

        return total;
        
    }
    
    /**
     *  adds the specified track to the zip archive
     * 
     *  @param zip the archive to add to
     *  @param track the track to add
     * 
     *  @throws IOException
     * 
     */
    
    private void addTrackToZip( final ZipOutputStream zip, final Track track ) throws IOException {

        final byte[] buf = new byte[4096];
        int retval;

        FileInputStream is = null;
        
        try {

            // entries are named by artist/album/number - track.ext
            final String name = getTrackZipPath( track );
            is = new FileInputStream(track.getPath());
            zip.putNextEntry( new ZipEntry(name) );

            do {
                retval = is.read( buf, 0, 4096 );
                if ( retval != -1 )
                    zip.write( buf, 0, retval );
            }
            while ( retval != -1 );

            zip.closeEntry();
            
        }

        finally { Utils.close(is); }
        
    }

    /**
     *  returns the path to use in the zip file for this track
     * 
     *  @param track
     * 
     *  @return
     * 
     */
    
    protected String getTrackZipPath( final Track track ) {
        
        final int number = track.getNumber();

        return track.getArtist().getName() + "/" +
                    track.getAlbum().getName() + "/" +
                    (number == 0 ? "" : padTens(number) + " - ") +
                    track.getName() + "." + Utils.getExt(track.getPath());

    }
    
    /**
     *  pads numbers less than 10 with a leading 0.  if things
     *  go wrong then the number is returned
     * 
     *  @param number the number to pad
     *  @return the padded number
     * 
     */
    
    private String padTens( final int number ) {
        
        final String strNum = Integer.toString( number );
        
        return number < 10 ? "0" + strNum : strNum;
        
    }

    /**
     *  If all the tracks have the same artist, then append the artist name for
     *  the beginning of the file.  Otherwise, if there are different artists
     *  append VARIOUS_ARTISTS.  Then append the album name if all the tracks
     *  are from the same album, otherwise use VARIOUS_ALBUMS
     *
     *  @param tracks Vector<Track>
     *
     *  @return the name of the downloadable zip file - format: <artist>-<album>.zip
     *
     */

    protected String getFileName( final Vector<Track> tracks ) {

        return getArtistName( tracks ) +
               "-" +
               getAlbumName( tracks ) +
               ".zip";
        
    }

    /**
     *  Returns the artist name to use from the track
     *
     *  @param track Track
     *
     *  @TODO this is *so* similair to getAlbumName()
     *
     *  @return the String value of the artist's name.  Empty String if no name exists.
     *
     */

    private String getArtistName( final Vector<Track> tracks ) {

        String previousArtist = null;

        for ( final Track track : tracks ) {

            final String artistName = track.getArtist().getName();

            if ( previousArtist != null && !artistName.equalsIgnoreCase(previousArtist) ) {
                return VARIOUS_ARTISTS;
            }

            previousArtist = artistName;

        }

        return tracks.isEmpty()
            ? DEFAULT_ARTIST
            : tracks.get(0).getArtist().getName();

    }

    /**
     *  Returns the album name to use.  If all tracks are on the same album
     *  then returns album name, otherwise returns VARIOUS_ALBUMS
     * 
     *  @param track Track
     *
     *  @TODO this is *so* similair to getArtistName()
     *
     *  @return the String value of the album's name.  "album" if no name exists.
     *
     */
    
    private String getAlbumName( final Vector<Track> tracks ) {

        String previousAlbum = null;

        for ( final Track track : tracks ) {

            final String albumName = track.getAlbum().getName();

            if ( previousAlbum != null && !albumName.equalsIgnoreCase(previousAlbum) ) {
                return VARIOUS_ALBUMS;
            }

            previousAlbum = albumName;
            
        }

        return tracks.isEmpty()
            ? DEFAULT_ALBUM
            : tracks.get(0).getAlbum().getName();

    }
    
}
