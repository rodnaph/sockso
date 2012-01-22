
package com.pugh.sockso.web.action;

import com.pugh.sockso.music.CoverArt;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.db.Database;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import static org.easymock.EasyMock.*;

public class AbstractCoverSearchTest extends SocksoTestCase {

    /**
     *  empty implementation for testing
     *
     */

    class MyCoverSearch extends AbstractCoverSearch {
        public MyCoverSearch( final Database db ) {
            super( db );
        }
        public CoverArt getCover( final String name ) { return null; }
    }

    public void testGetCustomTypeFromAbbrev() {

        final MyCoverSearch s = new MyCoverSearch( null );

        assertEquals( "album", s.getCustomTypeFromAbrev("al") );
        assertEquals( "artist", s.getCustomTypeFromAbrev("ar") );
        assertEquals( "playlist", s.getCustomTypeFromAbrev("pl") );
        assertEquals( "track", s.getCustomTypeFromAbrev("tr") );
        assertNull( s.getCustomTypeFromAbrev("BAD") );

    }

    public void testGetMusicItemName() throws SQLException {

        final ResultSet rs = createMock( ResultSet.class );
        expect( rs.next() ).andReturn( true );
        expect( rs.getString("name") ).andReturn( "foo" );
        rs.close();
        replay( rs );

        final PreparedStatement st = createMock( PreparedStatement.class );
        st.setInt( 1, 123 );
        expect( st.executeQuery() ).andReturn( rs ).times( 1 );
        st.close();
        replay( st );

        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );

        final MyCoverSearch s = new MyCoverSearch( db );
        final String name = s.getMusicItemName( "ar123" );

        assertEquals( name, "foo" );

        verify( db );
        verify( st );
        verify( rs );

    }

    public void testGetMusicItemNameNoMatch() throws SQLException {

        final ResultSet rs = createMock( ResultSet.class );
        expect( rs.next() ).andReturn( false );
        rs.close();
        replay( rs );

        final PreparedStatement st = createMock( PreparedStatement.class );
        st.setInt( 1, 345 );
        expect( st.executeQuery() ).andReturn( rs ).times( 1 );
        st.close();
        replay( st );

        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );

        final MyCoverSearch s = new MyCoverSearch( db );
        final String name = s.getMusicItemName( "tr345" );

        assertNull( name );

        verify( db );
        verify( st );
        verify( rs );

    }

    public void testGetMusicItemNameBadArgument() throws SQLException {

        final Database db = createMock( Database.class );
        replay( db );

        final MyCoverSearch s = new MyCoverSearch( db );
        boolean gotException = false;

        try {
            s.getMusicItemName( "BAD NAME" );
        }
        catch ( final NumberFormatException e ) {
            gotException = true;
        }

        assertTrue( gotException );

        verify( db );

    }

    public void testGetArtistName() throws SQLException {

        final int albumId = 123;

        final ResultSet rs = createMock( ResultSet.class );
        expect( rs.next() ).andReturn( true );
        expect( rs.getString("name") ).andReturn( "foo" );
        rs.close();
        replay( rs );

        final PreparedStatement st = createMock( PreparedStatement.class );
        st.setInt( 1, albumId );
        expect( st.executeQuery() ).andReturn( rs ).times( 1 );
        st.close();
        replay( st );

        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );

        final MyCoverSearch s = new MyCoverSearch( db );
        final String name = s.getArtistName( albumId );

        assertEquals( "foo", name );

        verify( db );
        verify( st );
        verify( rs );

    }

    public void testGetArtistNameNoMatch() throws SQLException {

        final int albumId = 123;

        final ResultSet rs = createMock( ResultSet.class );
        expect( rs.next() ).andReturn( false );
        rs.close();
        replay( rs );

        final PreparedStatement st = createMock( PreparedStatement.class );
        st.setInt( 1, albumId );
        expect( st.executeQuery() ).andReturn( rs ).times( 1 );
        st.close();
        replay( st );

        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );

        final MyCoverSearch s = new MyCoverSearch( db );
        final String name = s.getArtistName( albumId );

        assertEquals( "", name );

        verify( db );
        verify( st );
        verify( rs );

    }

    public void testRemoveUseLessWords() {

        final MyCoverSearch s = new MyCoverSearch( null );
        final String keywords = "name CD1 disc 2 CD 2";

        assertEquals( "name", s.removeUselessWords(keywords) );

        // check no sode effects
        assertEquals( "name CD1 disc 2 CD 2", keywords );

    }

}
