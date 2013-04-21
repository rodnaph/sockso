
package com.pugh.sockso.web.action.browse;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Collection;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestResponse;
import com.pugh.sockso.web.BadRequestException;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FoldererTest extends SocksoTestCase {

    public void testGetCollections() throws SQLException {
        
        final ResultSet rs = createNiceMock( ResultSet.class );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( true );
        expect( rs.next() ).andReturn( false );
        replay( rs );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        expect( st.executeQuery() ).andReturn( rs ).times( 1 );
        st.close();
        replay( st );
        
        final Database db = createMock( Database.class );
        expect( db.prepare((String)anyObject()) ).andReturn( st ).times( 1 );
        replay( db );
        
        final Folderer b = new Folderer();
        b.setDatabase( db );
        
        final List<Collection> folders = b.getCollections();
        
        assertNotNull( folders );
        assertEquals( 2, folders.size() );
        
        verify( db );
        verify( st );
        verify( rs );
        
    }
    
    public void testGetCollectionsQuery() throws Exception {
        
        final Database db = new TestDatabase();
        final Folderer b = new Folderer();
        
        b.setDatabase( db );
        b.getCollections();
        
    }

    public void testFolderBrowsingDisabled() throws SQLException, IOException {
        
        final Properties p = createMock( Properties.class );
        expect( p.get(Constants.WWW_BROWSE_FOLDERS_ENABLED) ).andReturn( "" ).times( 1 );
        replay( p );
        
        final Folderer b = new Folderer();
        boolean gotException = false;
        
        try {
            b.setProperties( p );
            b.handleRequest();
        }
        catch ( final BadRequestException e ) {
            gotException = true;
        }
        
        assertTrue( gotException );
        
        verify( p );
        
    }
    
    public void testShowFolders() throws Exception {
        
        final TestResponse res = new TestResponse();
        final Folderer b = new Folderer();
        final List<Collection> folders = new ArrayList<Collection>();
        
        folders.add( new Collection(12321,"/some/path") );
        
        b.setResponse( res );
        b.showFolders( folders );
        
        final String data = res.getOutput();

        assertTrue( data.contains("12321") );
        
    }


}
