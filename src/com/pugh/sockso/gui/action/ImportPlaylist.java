
package com.pugh.sockso.gui.action;

import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.gui.PlaylistFileFilter;
import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.music.CollectionManager;
import com.pugh.sockso.music.playlist.PlaylistFile;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import java.io.File;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 *  an action to import a playlist
 * 
 */

public class ImportPlaylist implements ActionListener {

    private static final Logger log = Logger.getLogger( ImportPlaylist.class );
    
    private final JFrame parent;
    private final Database db;
    private final CollectionManager cm;
    private final Resources r;
    
    public ImportPlaylist( final JFrame parent, final Database db, final CollectionManager cm, final Resources r ) {

        this.parent = parent;
        this.db = db;
        this.cm = cm;
        this.r = r;
        
    }

    public void actionPerformed( ActionEvent evt ) {

        String error = "";

        final JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter( new PlaylistFileFilter(r.getCurrentLocale()) );

        if ( fc.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION ) {

            try {

                final File file = fc.getSelectedFile();
                final String playlistName = getPlaylistName( file );
                final int playlistId = importPlaylist( file, playlistName );
                final Locale locale = r.getCurrentLocale();

                if ( playlistId == -1 ) {
                    error = locale.getString("gui.message.playlistImportFailed");
                }
                else {
                    JOptionPane.showMessageDialog(
                        parent, locale.getString("gui.message.playlistImported"),
                        "Sockso", JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }

            }

            catch ( final Exception e ) {
                e.printStackTrace();
                log.error( e );
                error = e.getMessage();
            }

            JOptionPane.showMessageDialog(
                parent, error, "Sockso",
                JOptionPane.ERROR_MESSAGE
            );

        }

    }

    /**
     *  Import a playlist from the specified file with the specified name
     * 
     *  @param file
     *  @param playlistName
     * 
     *  @return
     * 
     *  @throws SQLException
     *  @throws Exception
     *  @throws IOException 
     * 
     */
    
    protected int importPlaylist(final File file, final String playlistName) throws SQLException, Exception, IOException {
        
        final PlaylistFile playlistFile = PlaylistFile.getPlaylistFile( file.getAbsoluteFile() );
        
        if ( playlistFile == null ) {
            throw new Exception( "Unsupported playlist type" );
        }
        
        final Track[] tracks = getTracksFromPlaylist( playlistFile );
        final int playlistId = cm.savePlaylist( playlistName, tracks );
        
        return playlistId;
        
    }

    /**
     *  Looks through the tracks in the playlist and tries to find them in the
     *  collection.
     *
     *  @param playlistFile
     *
     *  @return
     *
     *  @throws SQLException
     *
     */

    protected Track[] getTracksFromPlaylist( final PlaylistFile playlistFile ) throws SQLException {

        final ArrayList<Track> tracks = new ArrayList<Track>();

        ResultSet rs = null;
        PreparedStatement st = null;

        try {

            for ( final String path : playlistFile.getPaths() ) {

                final String sql = Track.getSelectFromSql() +
                                   " where t.path = ? ";
                st = db.prepare( sql );
                st.setString( 1, path );
                rs = st.executeQuery();

                if ( rs.next() ) {
                    tracks.add( Track.createFromResultSet(rs) );
                }

            }

        }

        finally {
            Utils.close( rs );
            Utils.close( st );
        }

        return tracks.toArray( new Track[]{} );

    }

    /**
     *  returns a newName to use for the playlist.  this will be the filename
     *  without the extension, and a number added on to the end if there
     *  are duplicates in the database already (eg. "playlist (2)")
     * 
     *  @param file
     * 
     *  @return
     * 
     */

    protected String getPlaylistName( final File file ) throws SQLException {

        ResultSet rs = null;
        PreparedStatement st = null;

        try {

            String newName = file.getName();

            // remove extension
            newName = newName.substring( 0, newName.indexOf(".") );

            final String sql = " select p.name " +
                               " from playlists p ";
            st = db.prepare( sql );
            rs = st.executeQuery();

            int clashes = 0;

            while ( rs.next() ) {
                final String name = rs.getString( "name" );
                if ( name.equals(newName) || name.matches("^" +newName+ " \\(\\d+\\)") )
                    clashes++;
            }

            if ( clashes > 0 )
                newName += " (" +clashes+ ")";

            return newName;

        }

        finally {
            Utils.close( rs );
            Utils.close( st );
        }

    }
    
}
