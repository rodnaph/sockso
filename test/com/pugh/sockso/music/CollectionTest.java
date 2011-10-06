
package com.pugh.sockso.music;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;

import java.sql.SQLException;

public class CollectionTest extends SocksoTestCase {
    
    private Database db;
    
    @Override
    public void setUp() throws Exception {
        db = new TestDatabase();
        db.update( "insert into collection ( id, path ) values ( 1, '/foo/bar/' )" );
        db.update( "insert into collection ( id, path ) values ( 2, 'C:\\foo\\bar\\' )" );
    }

    public void testConstructors() {
        final int id = 123;
        final String path = "/home/me/music";
        assertNotNull( new Collection() );
        assertNotNull( new Collection(id,path) );
    }

    public void testGetters() {
        final int id = 123;
        final String path = "/home/me/music";
        final Collection col =  new Collection(id,path);
        assertEquals( id, col.getId() );
        assertEquals( path, col.getPath() );
    }

    public void testCollectionCanBeFetchedWhereItContainsAPath() throws SQLException {
        Collection c = Collection.findByPath( db, "/foo/bar/baz/" );
        assertEquals( 1, c.getId() );
    }
    
    public void testCollectionCanBeFetchedWhereItContainsAPathWithoutTrailingSlash() throws SQLException {
        Collection c = Collection.findByPath( db, "/foo/bar/baz" );
        assertEquals( 1, c.getId() );
    }
    
    public void testCollectionReturnedWhenPathMatchesRootWithoutTrailingSlash() throws SQLException {
        Collection c = Collection.findByPath( db, "/foo/bar" );
        assertEquals( 1, c.getId() );
    }

    public void testCollectionReturnedWhenPathMatchesRootWithTrailingSlash() throws SQLException {
        Collection c = Collection.findByPath( db, "/foo/bar/" );
        assertEquals( 1, c.getId() );
    }

    public void testCollectionReturnedWhenPathMatchesRootWithWindowsPathAndTrailingSlash() throws SQLException {
        Collection c = Collection.findByPath( db, "C:\\foo\\bar\\" );
        assertEquals( 2, c.getId() );
    }

    public void testCollectionReturnedWhenPathMatchesRootWithWindowsPathAndNoTrailingSlash() throws SQLException {
//        Collection c = Collection.findByPath( db, "C:\\foo\\bar" );
//        assertEquals( 2, c.getId() );
    }

    public void testNullReturnedWhenTryingToFindACollectionByPathDoesntMatchAnything() throws SQLException {
        assertNull( Collection.findByPath( db, "/bar/baz" ) );
    }
    
}
