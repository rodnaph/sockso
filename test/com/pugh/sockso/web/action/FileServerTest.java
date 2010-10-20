
package com.pugh.sockso.web.action;

import com.pugh.sockso.Properties;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.tests.SocksoTestCase;

import java.io.File;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.easymock.EasyMock.*;

public class FileServerTest extends SocksoTestCase {

    public void testGetMimeType() {
        assertEquals( "text/css", FileServer.getMimeType("default.css") );
        assertEquals( "audio/mpeg", FileServer.getMimeType("/home/me/default.mp3") );
        assertEquals( "audio/mpegurl", FileServer.getMimeType("c:\\Users\\Me\\file.m3u") );
    }

    public void testGetLocalCoverDirectories() throws Exception {
        
        final ResultSet rs = createMock( ResultSet.class );
        expect( rs.next() ).andReturn( true ).times( 1 );
        expect( rs.getString("path") ).andReturn( "something" );
        expect( rs.next() ).andReturn( true ).times( 1 );
        expect( rs.getString("path") ).andReturn( "something" );
        expect( rs.next() ).andReturn( false ).times( 1 );
        rs.close();
        replay( rs );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        expect( st.executeQuery() ).andReturn( rs );
        st.close();
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st );
        replay( db );
                
        final FileServer fs = new FileServer( null );
        fs.setDatabase( db );
        final File[] dirs = fs.getLocalCoverDirectories( "ar123" );
        
        assertNotNull( fs );
        assertEquals( 1, dirs.length );
        assertTrue( dirs[0].getName().equals("something") );
        
        verify( db );
        verify( st );
        verify( rs );
        
    }
    
    public void testIsArtist() {
        
        final FileServer fs = new FileServer( null );
        
        assertTrue( fs.isArtist("ar123") );
        assertFalse( fs.isArtist("al456") );
        assertFalse( fs.isArtist("") );
        assertFalse( fs.isArtist(null) );
        
    }
    
    public void testGetLocalCoverFileName() {
        
        final Properties p = createNiceMock( Properties.class );
        expect( p.get((String)anyObject(),(String)anyObject()) ).andReturn( "artist" ).times( 1 );
        expect( p.get((String)anyObject(),(String)anyObject()) ).andReturn( "album" ).times( 1 );
        replay( p );
        
        final FileServer fs = new FileServer( null );
        fs.setProperties( p );
        
        assertTrue( fs.getLocalCoverFileName("ar123").equals("artist") );
        assertTrue( fs.getLocalCoverFileName("al456").equals("album") );
        
        verify( p );
        
    }
    
    public void testGetLocalCoverFilesAlbum() {
        
        final FileServer fs = new FileServer( null );
        final String coverFileName = "album",
                     folder = "/home/music/album",
                     file = folder+ "/track.mp3";
        final File[] trackDirs = new File[] {
            new File( file )
        };
        final File[] files = fs.getLocalCoverFiles( trackDirs, coverFileName, false );
        
        assertNotNull( files );
        assertEquals( 3, files.length );
        assertEquals( new File(folder+ "/album.jpg"), files[0] );
        
    }

    public void testGetLocalCoverFilesArtist() {

        final FileServer fs = new FileServer( null );
        final String coverFileName = "artist",
                     folder = "/home/music/album",
                     file = folder+ "/track.mp3";
        final File[] trackDirs = new File[] {
            new File( file )
        };
        final File[] files = fs.getLocalCoverFiles( trackDirs, coverFileName, true );
        
        assertNotNull( files );
        assertEquals( 6, files.length );
        assertEquals( new File(folder+ "/artist.jpg"), files[0] );
        assertEquals( new File("/home/music/artist.jpg"), files[3] );

    }
    
}
