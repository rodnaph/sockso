
package com.pugh.sockso.gui.action;

import com.pugh.sockso.db.Database;
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

    public void testConstructor() {
        
        final ImportPlaylist ip = new ImportPlaylist( null, null, null, null );
        
        assertNotNull( ip );
        
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
        TestDatabase db = new TestDatabase();
        db.fixture( "playlistTracks" );
        String track1 = "/home/user/music/01 - some track.mp3";
        M3uFile playlist = new M3uFile( null );
        playlist.loadLines(new String[] { track1, "/home/other/file.ogg" });
        ImportPlaylist ip = new ImportPlaylist( null, db, null, null );
        Track[] files = ip.getTracksFromPlaylist( playlist );
        assertEquals( 1, files.length );
        assertEquals( track1, files[0].getPath() );
    }
    
    public void testGettingTracksFromM3uFileWithWindowsPaths() throws Exception {
        TestDatabase db = new TestDatabase();
        db.fixture( "playlistTracks" );
        String track1 = "S:\\My Music\\James Hunter - All Through Cryin'.mp3";
        M3uFile playlist = new M3uFile( null );
        playlist.loadLines(new String[] { track1, "/home/other/file.ogg" });
        ImportPlaylist ip = new ImportPlaylist( null, db, null, null );
        Track[] files = ip.getTracksFromPlaylist( playlist );
        assertEquals( 1, files.length );
        assertEquals( track1, files[0].getPath() );
    }

    public void testGettingTracksFromExtM3uFile() throws Exception {
        TestDatabase db = new TestDatabase();
        db.fixture( "playlistTracks" );
        String track1 = "/home/user/music/01 - some track.mp3";
        M3uFile playlist = new M3uFile( null );
        playlist.loadLines(new String[] {
            "#EXTM3U",
            "#EXTINF:320,Wyclef Jean - Something About Mary",
            track1
        });
        ImportPlaylist ip = new ImportPlaylist( null, db, null, null );
        Track[] files = ip.getTracksFromPlaylist( playlist );
        assertEquals( 1, files.length );
        assertEquals( track1, files[0].getPath() );
    }

}
