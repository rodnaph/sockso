
package com.pugh.sockso.gui.action;

import com.pugh.sockso.StringProperties;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.DBCollectionManager;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.music.playlist.M3uFile;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.io.File;

import static org.easymock.EasyMock.*;

public class ImportPlaylistTest extends SocksoTestCase {

    private ImportPlaylist ip;
    
    private TestDatabase db;
    
    @Override
    public void setUp() {
        db = new TestDatabase();
        ip = new ImportPlaylist(
            null,
            db,
            new DBCollectionManager( db, new StringProperties(), null ),
            null
        );
    }
    
    public void testGetPlaylistName() throws SQLException {
        
        final ResultSet rs = createMock( ResultSet.class );
        expect( rs.next() ).andReturn( true );
        expect( rs.getString("name") ).andReturn( "myPlaylist" ).times( 1 );
        expect( rs.next() ).andReturn( true );
        expect( rs.getString("name") ).andReturn( "myPlaylist (1)" ).times( 1 );
        expect( rs.next() ).andReturn( false );
        rs.close();
        replay( rs );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        expect( st.executeQuery() ).andReturn( rs ).times( 1 );
        st.close();
        replay( st );

        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );
        
        final File playlistFile = new File( "myPlaylist.m3u" );
        final ImportPlaylist ip = new ImportPlaylist( null, db, null, null );
        
        assertEquals( "myPlaylist (2)", ip.getPlaylistName(playlistFile) );
        
        verify( db );
        verify( st );
        verify( rs );
        
    }

    public void testGettingTracksFromM3uFile() throws Exception {
        db.fixture( "playlistTracks" );
        String track1 = "/home/user/music/01 - some track.mp3";
        M3uFile playlist = new M3uFile( null );
        playlist.loadLines(new String[] { track1, "/home/other/file.ogg" });
        Track[] files = ip.getTracksFromPlaylist( playlist );
        assertEquals( 1, files.length );
        assertEquals( track1, files[0].getPath() );
    }
    
    public void testGettingTracksFromM3uFileWithWindowsPaths() throws Exception {
        db.fixture( "playlistTracks" );
        String track1 = "S:\\My Music\\James Hunter - All Through Cryin'.mp3";
        M3uFile playlist = new M3uFile( null );
        playlist.loadLines(new String[] { track1, "/home/other/file.ogg" });
        Track[] files = ip.getTracksFromPlaylist( playlist );
        assertEquals( 1, files.length );
        assertEquals( track1, files[0].getPath() );
    }

    public void testGettingTracksFromExtM3uFile() throws Exception {
        db.fixture( "playlistTracks" );
        String track1 = "/home/user/music/01 - some track.mp3";
        M3uFile playlist = new M3uFile( null );
        playlist.loadLines(new String[] {
            "#EXTM3U",
            "#EXTINF:320,Wyclef Jean - Something About Mary",
            track1
        });
        Track[] files = ip.getTracksFromPlaylist( playlist );
        assertEquals( 1, files.length );
        assertEquals( track1, files[0].getPath() );
    }
    
    public void testImportingAPlaylistCreatesItWithAllMatchedTracks() throws Exception {
        db.fixture( "playlistTracks" );
        ip.importPlaylist( new File("test/data/testImport.m3u"), "Some Name" );
        assertTableSize( db, "playlists", 1 );
        assertTableSize( db, "playlist_tracks", 4 );
    }

}
