/*
 * ConsoleTest.java
 * 
 * Created on Jul 29, 2007, 12:57:15 PM
 * 
 * Tests the console
 * 
 */

package com.pugh.sockso;

import com.pugh.sockso.tests.TestUtils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.CollectionManager;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.web.User;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestLocale;

import java.io.PrintStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.easymock.EasyMock.*;

public class ConsoleTest extends SocksoTestCase {

    private Database db;
    private Properties p;
    private CollectionManager cm;
    private PrintStream out;
    private OutputStream os;
    private InputStream in;
    private Locale locale;
    
    private class MyConsole extends Console {
        public boolean flag = false;
        public MyConsole( Database db, Properties p, CollectionManager cm, PrintStream out, InputStream is, Locale locale ) {
            super( db, p, cm, out, is, locale );
        }
    }
    
    @Override
    public void setUp() {
        db = createNiceMock( Database.class );
        p = createNiceMock( Properties.class );
        cm = createNiceMock( CollectionManager.class );
        os = TestUtils.getOutputStream();
        out = new PrintStream( os );
        in = TestUtils.getInputStream( "\n" );
        locale = createNiceMock( Locale.class );
    }
    
    @Override
    public void tearDown() {
        db = null;
        p = null;
        cm = null;
        os = null;
        out = null;
        in = null;
    }
    
    public void testConstructor() throws IOException {
        Console c = new Console( db, p, cm, out, in, locale );
        assertNotNull( c );
    }

    public void testDispatchCommandExit() throws SQLException {
        MyConsole c = new MyConsole( db, p, cm, out, in, locale ) {
            @Override
            protected void cmdExit() { flag = true; } 
        };
        c.dispatchCommand( Console.CMD_EXIT );
        assertTrue( c.flag );
    }

    public void testDispatchCommandColAdd() throws SQLException {
        MyConsole c = new MyConsole( db, p, cm, out, in, locale ) {
            @Override
            protected void cmdColAdd( String[] args ) { flag = true; }
        };
        c.dispatchCommand( Console.CMD_COLADD );
        assertTrue( c.flag );
    }

    public void testDispatchCommandPropList() throws SQLException {
        MyConsole c = new MyConsole( db, p, cm, out, in, locale ) {
            @Override
            protected void cmdPropList( final String[] args ) { flag = true; }
        };
        c.dispatchCommand( Console.CMD_PROPLIST );
        assertTrue( c.flag );
    }

    public void testDispatchCommandPropSet() throws SQLException {
        MyConsole c = new MyConsole( db, p, cm, out, in, locale ) {
            @Override
            protected void cmdPropSet( String[] args ) { flag = true; }
        };
        c.dispatchCommand( Console.CMD_PROPSET );
        assertTrue( c.flag );
    }

    public void testCmdPropList() {
        
        String[] props = { "foo", "bar" }; // some properties
        expect( p.getProperties() ).andReturn( props );
        replay( p );
        
        Console c = new Console( db, p, cm, out, in, locale );
        c.cmdPropList( new String[] {} );
        
        verify( p );
        
    }
    
    public void testCmdPropSet() {
        
        p.set( "foo", "bar" );
        p.save();
        replay( p );
        
        String[] args = { "coladd", "foo", "bar" };
        Console c = new Console( db, p, cm, out, in, locale );
        
        c.cmdPropSet( args );
        
        verify( p );
        
    }
    
    public void testCmdColAdd() {
        
        String[] args = { Console.CMD_COLADD, "test/" };
        Console c = new Console( db, p, cm, out, in, locale );
        
        c.cmdColAdd( args );
        
    }
    
    public void testCmdColDel() throws SQLException {

        String path = "/foo/bar";
        
        expect( cm.removeDirectory(path) ).andReturn( true );
        replay( cm );

        String[] args = { Console.CMD_COLDEL, path };
        Console c = new Console( db, p, cm, out, in, locale );
        
        c.cmdColDel( args );
        
        verify( cm );

    }

    public void testCmdUserList() throws Exception {
        
        final ResultSet rs = createMock( ResultSet.class );
        expect( rs.next() ).andReturn( true );
        expect( rs.getString("id") ).andReturn( "id" );
        expect( rs.getString("name") ).andReturn( "name" );
        expect( rs.getString("email") ).andReturn( "email" );
        expect( rs.getBoolean("is_admin") ).andReturn( false );
        expect( rs.next() ).andReturn( false );
        rs.close();
        replay( rs );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        expect( st.executeQuery() ).andReturn( rs );
        st.close();
        replay( st );
        
        final Database mockDb = createMock( Database.class );
        expect( mockDb.prepare((String)anyObject()) ).andReturn( st );
        replay( mockDb );
        
        Console c = new Console( mockDb, p, cm, out, in, locale );
        
        c.cmdUserList( new String[] {} );
        
        verify( rs );
        verify( st );
        verify( mockDb );
        
    }
    
    public void testCmdColList() throws Exception {
        
        final ResultSet rs = createMock( ResultSet.class );
        expect( rs.next() ).andReturn( true );
        expect( rs.getString("id") ).andReturn( "id" );
        expect( rs.getString("path") ).andReturn( "name" );
        expect( rs.next() ).andReturn( false );
        rs.close();
        replay( rs );
        
        final PreparedStatement st = createMock( PreparedStatement.class );
        expect( st.executeQuery() ).andReturn( rs );
        st.close();
        replay( st );
        
        final Database mockDb = createMock( Database.class );
        expect( mockDb.prepare((String)anyObject()) ).andReturn( st );
        replay( mockDb );
        
        Console c = new Console( mockDb, p, cm, out, in, locale );
        
        c.cmdColList();
        
        verify( rs );
        verify( st );
        verify( mockDb );
        
    }
    
    public void testGetArgs() {
        
        Console c = new Console( null, null, null, null, null, null );
        
        assertEquals( "a b c", Utils.joinArray(c.getArgs("a b c")," ",0,2) );
        
    }

    public void testMakingANonAdminUserAdminDoesSo() throws Exception {
        Database db = new TestDatabase();
        Console c = new Console( db, p, cm, out, in, new TestLocale() );
        User u1 = new User( "qwe", "rty", "qwe@rty.com", false );
        u1.save( db );
        ///////////
        c.cmdUserAdmin(new String[]{ "useradmin", String.valueOf(u1.getId()), "1" });
        User u2 = User.find( db, u1.getId() );
        assertTrue( u2.isAdmin() );
    }
    
    public void testMakingAnAdminUserNonAdminDoesSo() throws Exception {
        Database db = new TestDatabase();
        Console c = new Console( db, p, cm, out, in, new TestLocale() );
        User u1 = new User( "qwe", "rty", "qwe@rty.com", true );
        u1.save( db );
        ///////////
        c.cmdUserAdmin(new String[]{ "useradmin", String.valueOf(u1.getId()), "0" });
        User u2 = User.find( db, u1.getId() );
        assertFalse( u2.isAdmin() );
    }
    
    public void testSpecifyingInvalidUserIdReportsThisToTheUser() {
        
    }

}
