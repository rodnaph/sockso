
package com.pugh.sockso.web.action.covers;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.tests.SocksoTestCase;

import java.io.File;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.easymock.EasyMock.*;


public class LocalCovererTest extends SocksoTestCase {


    private LocalCoverer coverer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        coverer = new LocalCoverer();
    }

    @Override
    protected void tearDown() throws Exception {
        coverer = null;
    }

    public void testGetLocalCoverFileName() {

        final Properties p = createNiceMock(Properties.class);
        expect(p.get((String) anyObject(), (String) anyObject())).andReturn("artist").times(1);
        expect(p.get((String) anyObject(), (String) anyObject())).andReturn("album").times(1);
        replay(p);

        coverer.setProperties(p);

        assertTrue(coverer.getLocalCoverFileName("ar123").equals("artist"));
        assertTrue(coverer.getLocalCoverFileName("al456").equals("album"));

        verify(p);
    }

    public void testIsArtist() {

        assertTrue(coverer.isArtist("ar123"));
        assertFalse(coverer.isArtist("al456"));
        assertFalse(coverer.isArtist(""));
        assertFalse(coverer.isArtist(null));
    }

    public void testGetLocalCoverDirectories() throws Exception {

        final ResultSet rs = createMock(ResultSet.class);
        expect(rs.next()).andReturn(true).times(1);
        expect(rs.getString("path")).andReturn("something");
        expect(rs.next()).andReturn(true).times(1);
        expect(rs.getString("path")).andReturn("something");
        expect(rs.next()).andReturn(false).times(1);
        rs.close();
        replay(rs);

        final PreparedStatement st = createMock(PreparedStatement.class);
        expect(st.executeQuery()).andReturn(rs);
        st.close();
        replay(st);

        final Database db = createMock(Database.class);
        expect(db.prepare((String) anyObject())).andReturn(st);
        replay(db);

        coverer.setDatabase(db);
        final File[] dirs = coverer.getLocalCoverDirectories("ar123");

        assertNotNull(coverer);
        assertEquals(1, dirs.length);
        assertTrue(dirs[0].getName().equals("something"));

        verify(db);
        verify(st);
        verify(rs);
    }

    public void testGetLocalCoverFilesAlbum() {

        coverer.setProperties(new StringProperties());

        final String coverFileName = "album",
                folder = "/home/music/album",
                file = folder + "/track.mp3";
        final File[] trackDirs = new File[]{
            new File(file)
        };
        final File[] files = coverer.getLocalCoverFiles(trackDirs, coverFileName, false);

        assertNotNull(files);
        assertEquals(3, files.length);
        assertEquals(new File(folder + "/album.jpg"), files[0]);
    }

    public void testGetLocalCoverFilesArtist() {

        coverer.setProperties(new StringProperties());

        final String coverFileName = "artist",
                folder = "/home/music/album",
                file = folder + "/track.mp3";
        final File[] trackDirs = new File[]{
            new File(file)
        };
        final File[] files = coverer.getLocalCoverFiles(trackDirs, coverFileName, true);

        assertNotNull(files);
        assertEquals(6, files.length);
        assertEquals(new File(folder + "/artist.jpg"), files[0]);
        assertEquals(new File("/home/music/artist.jpg"), files[3]);

    }

    public void testGetlocalCoverFilesFallbackAlbum() {

        coverer.setProperties(new StringProperties());

        final String coverFileName = "album",
                folder = "test/data/covers/artist - album",
                file = folder + "/track.mp3";

        final File[] trackDirs = new File[]{
            new File(file)
        };

        File[] files = coverer.getLocalCoverFiles(trackDirs, coverFileName, false);

        assertNotNull(files);
        assertEquals(3, files.length);
        assertEquals(new File(folder + "/album.jpg"), files[0]);

        // Fallback property set to NO should behave identically
        // as set to NULL
        coverer.getProperties().set(Constants.COVERS_FILE_FALLBACK, Properties.NO);
        files = coverer.getLocalCoverFiles(trackDirs, coverFileName, false);
        assertNotNull(files);
        assertEquals(3, files.length);
        assertEquals(new File(folder + "/album.jpg"), files[0]);

        coverer.getProperties().set(Constants.COVERS_FILE_FALLBACK, Properties.YES);
        files = coverer.getLocalCoverFiles(trackDirs, coverFileName, false);
        assertNotNull(files);
        assertEquals(4, files.length);
        assertEquals(new File(folder + "/album.jpg"), files[0]);
        assertEquals(new File(folder + "/fallback-file.png"), files[3]);
    }

    public void testGetLocalCoverFilesFallbackArtist() {

        coverer.setProperties(new StringProperties());

        final String coverFileName = "artist",
                folder = "test/data/covers/artist - album",
                file = folder + "/track.mp3";
        final File[] trackDirs = new File[]{
            new File(file)
        };
        File[] files = coverer.getLocalCoverFiles(trackDirs, coverFileName, true);

        assertNotNull(files);
        assertEquals(6, files.length);
        assertEquals(new File(folder + "/artist.jpg"), files[0]);
        assertEquals(new File("test/data/covers/artist.jpg"), files[3]);

        // Fallback property set to NO should behave identically
        // as set to NULL
        coverer.getProperties().set(Constants.COVERS_FILE_FALLBACK, Properties.NO);
        files = coverer.getLocalCoverFiles(trackDirs, coverFileName, true);
        assertNotNull(files);
        assertEquals(6, files.length);
        assertEquals(new File(folder + "/artist.jpg"), files[0]);
        assertEquals(new File("test/data/covers/artist.jpg"), files[3]);

        coverer.getProperties().set(Constants.COVERS_FILE_FALLBACK, Properties.YES);
        files = coverer.getLocalCoverFiles(trackDirs, coverFileName, true);
        assertNotNull(files);
        assertEquals(7, files.length);
        assertEquals(new File(folder + "/artist.jpg"), files[0]);
        assertEquals(new File("test/data/covers/artist.jpg"), files[3]);
        assertEquals(new File(folder + "/fallback-file.png"), files[6]);
    }
}
