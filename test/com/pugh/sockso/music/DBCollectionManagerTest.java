
package com.pugh.sockso.music;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.indexing.Indexer;
import com.pugh.sockso.music.tag.Tag;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.easymock.EasyMock.*;

public class DBCollectionManagerTest extends SocksoTestCase {
    
    private Properties p;
    private Indexer indexer;
    
    @Override
    protected void setUp() throws Exception {

        super.setUp();

        p = createMock( Properties.class );
        expect( p.get(Constants.COLLMAN_SCAN_ONSTART) ).andReturn( Properties.NO );
        expect( p.get(Constants.COLLMAN_SCAN_INTERVAL,5) ).andReturn( (long) 20 );
        replay( p );

        indexer = createNiceMock( Indexer.class );

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCheckTrackTagInfo() throws SQLException {
        
        final String trackName = "some name";
        final int trackNumber = 1;
        
        final Database db = createNiceMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( createNiceMock(PreparedStatement.class) ).anyTimes();
        replay( db );
        
        Track.Builder builder = new Track.Builder();
        builder.artist(null)
                .album(null)
                .genre(null)
                .id(-1)
                .name(trackName)
                .number(trackNumber)
                .path("")
                .dateAdded(null);
        final Track track = builder.build();

        final Tag sameTag = createMock( Tag.class );
        expect( sameTag.getTrack() ).andReturn( trackName );
        expect( sameTag.getTrackNumber() ).andReturn( trackNumber );
        replay( sameTag  );

        final Tag diffTag = createMock( Tag.class );
        expect( diffTag.getTrack() ).andReturn( trackName + " change" ).times( 2 );
        expect( diffTag.getTrackNumber() ).andReturn( trackNumber + 1 ).times( 2 );
        replay( diffTag  );

        try {

            final DBCollectionManager colMan = new DBCollectionManager( db, p, indexer );

            // no change
            colMan.checkTrackTagInfo( sameTag, track  );

            // tag changed
            colMan.checkTrackTagInfo( diffTag, track  );

            //verify( db );
            //verify( p );

        }

        catch ( final SQLException e ) {
            fail( e.getMessage() );
        }
        
    }

    public void testAddDirectoryToDb() throws SQLException {
        
        final String dirPath = "/some/dir/";
        final int newColId = 1726;

        final ResultSet rs = createMock( ResultSet.class );
        expect( rs.next() ).andReturn( true );
        expect( rs.getInt("new_id") ).andReturn( newColId );
        rs.close();
        replay( rs );
        
        final PreparedStatement st = createNiceMock( PreparedStatement.class );
        expect( st.executeQuery() ).andReturn( rs );
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 2 );
        replay( db );
        
        final DBCollectionManager colMan = new DBCollectionManager( db, p, indexer );
        
        try {
            
            final File dir = new File( dirPath );
            
            final int actualColId = colMan.addDirectoryToDb( dir );
            
            assertEquals( newColId, actualColId );
            
            verify( db );
            verify( rs );
            
        }
        
        catch ( final SQLException e ) {
            fail( e.getMessage() );
        }
        
    }
    
    public void testRemoveTrack() throws SQLException {
        
        final Database db = createMock( Database.class );
        expect( db.update((String)anyObject()) ).andReturn( 1 ).times( 3 );
        replay( db );
        
        final DBCollectionManager colMan = new DBCollectionManager( db, p, indexer );
        colMan.removeTrack( 123 );
        
        verify( db );
        
    }
    
    public void testAddCollectionManagerListener() {
        
        DBCollectionManager colMan = new DBCollectionManager( null, p, indexer );
        
        colMan.addCollectionManagerListener( new CollectionManagerListener() {
            public void collectionManagerChangePerformed( final int type, final String change ) {}
        });

    }
    
    public void testFireCollectionManagerEvent() {
        
        final DBCollectionManager colMan = new DBCollectionManager( null, p, indexer );
        final CollectionManagerListener listener = new CollectionManagerListener() {
            public void collectionManagerChangePerformed( final int type, final String change ) {
                // @TODO - check method is called
            }
        };
        
        colMan.fireCollectionManagerEvent( -1, "hello" );

    }
 
    public void testGetArtistBrowseName() throws Exception {
        
        final DBCollectionManager cm = new DBCollectionManager( null, null, indexer );
        final String[] prefixes = { "The ", "Qwe" };

        assertEquals( "Fugees", cm.getArtistBrowseName( prefixes, "The Fugees" ) );
        assertEquals( " rty", cm.getArtistBrowseName( prefixes, "Qwe rty" ) );
        assertEquals( "The Fugees", cm.getArtistBrowseName( new String[] { "", "Fugees" }, "The Fugees" ) );
        assertEquals( "Fugees", cm.getArtistBrowseName( new String[] { "the " }, "The Fugees" ) );
        
    }

    public void testGetArtistPrefixesToRemove() throws Exception {
        
        final Properties p = createMock( Properties.class );
        expect( p.get((String)anyObject()) ).andReturn( "The ,Die,Ha" ).times( 1 );
        replay( p );

        final DBCollectionManager cm = new DBCollectionManager( null, p, indexer );
        final String[] prefixes = cm.getArtistPrefixesToRemove();
        
        assertEquals( "The ", prefixes[0] );
        assertEquals( "Die", prefixes[1] );
        assertEquals( "Ha", prefixes[2] );
        
        verify( p );
        
    }
    
    public void testCheckArtistTagInfo() throws IOException, SQLException {

        final TestDatabase db = new TestDatabase();
        final DBCollectionManager cm = new DBCollectionManager( db, p, indexer );
        final String artistName = "My Artist";
        
        int firstArtistId = 0, secondArtistId = 0;

        db.fixture( "checkArtistTagChange" );

        Tag newTag = createMock( Tag.class );
        expect( newTag.getArtist() ).andReturn( artistName ).times(2);
        replay( newTag );
        
        ResultSet rs = db.query( "select * from albums where id = 1" );
        
        while (rs.next())
            firstArtistId = rs.getInt( "artist_id" );

        Track track = cm.getTrack( 3 );
        cm.checkArtistTagInfo( newTag, track );
        track = cm.getTrack( 3 );
        
        rs = db.query( "select * from albums where id = 3" );
        
        while (rs.next())
            secondArtistId = rs.getInt( "artist_id" ); 

        assertEquals( firstArtistId, secondArtistId );
 
    }

    public void testGetTrack() throws Exception {

        final TestDatabase db = new TestDatabase();
        final DBCollectionManager cm = new DBCollectionManager( db, p, indexer );

        db.fixture( "singleTrack" );

        final Track track = cm.getTrack( 1 );
        
        assertTrue( track.getPath().equals("/music/track.mp3") );
        
    }

    public void testGetTrackNotFound() throws Exception {

        final Database db = new TestDatabase();
        final DBCollectionManager cm = new DBCollectionManager( db, p, indexer );

        boolean gotException = false;

        try {
            cm.getTrack( -1 );
        }
        catch ( final SQLException e ) {
            gotException = true;
        }

        assertTrue( gotException );

    }

    public void testSaveAndRemovePlaylist() throws Exception {

        final TestDatabase db = new TestDatabase();
        final DBCollectionManager cm = new DBCollectionManager( db, p, indexer );
        final String name = Utils.getRandomString( 20 );

        db.fixture( "singleTrack" );

        final Track[] tracks = new Track[] {
            cm.getTrack( 1 )
        };

        final int playlistId = cm.savePlaylist( name, tracks );

        assertTableSize( db, "playlists", tracks.length );

        cm.removePlaylist( playlistId );

        assertTableSize( db, "playlists", 0 );

    }

    public void testRemoveEmptyArtistsAndAlbums() throws Exception {

        final TestDatabase db = new TestDatabase();
        final DBCollectionManager cm = new DBCollectionManager( db, p, indexer );

        db.fixture( "emptyArtistsAndAlbums" );

        assertTableSize( db, "artists", 1 );
        assertTableSize( db, "albums", 1 );
        assertTableSize( db, "tracks", 0 );
        
        cm.removeOrphans();

        assertTableSize( db, "artists", 0 );
        assertTableSize( db, "albums", 0 );
        assertTableSize( db, "tracks", 0 );

        db.fixture( "singleTrack" );

        assertTableSize( db, "artists", 1 );
        assertTableSize( db, "albums", 1 );
        assertTableSize( db, "tracks", 1 );

        cm.removeOrphans();

        assertTableSize( db, "artists", 1 );
        assertTableSize( db, "albums", 1 );
        assertTableSize( db, "tracks", 1 );

    }

}
