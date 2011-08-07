
package com.pugh.sockso.inject;

import com.pugh.sockso.tests.SocksoTestCase;

public class SocksoModuleTest extends SocksoTestCase {

    public void testFoo() {}
    
//    public void testGetDatabase() throws Exception {
//        
//        final OptionParser parser = Options.getParser();
//        final OptionSet defaultOptions = parser.parse( new String[] {} );
//        final OptionSet hsqlOptions = parser.parse( new String[] { "--dbtype=hsql" } );
//        final OptionSet mysqlOptions = parser.parse( new String[] { "--dbtype=mysql" } );
//        final OptionSet sqliteOptions = parser.parse( new String[] { "--dbtype=sqlite" } );
//        
//        assertEquals( HSQLDatabase.class, Main.getDatabase(defaultOptions).getClass() );
//        assertEquals( HSQLDatabase.class, Main.getDatabase(hsqlOptions).getClass() );
//        assertEquals( MySQLDatabase.class, Main.getDatabase(mysqlOptions).getClass() );
//        assertEquals( SQLiteDatabase.class, Main.getDatabase(sqliteOptions).getClass() );
//        
//    }
// 
//    public void testGetResources() throws Exception {
//        
//        final OptionParser parser = Options.getParser();
//        final OptionSet jarOptions = parser.parse( new String[] { "--resourcestype=jar" } );
//        final OptionSet fileOptions = parser.parse( new String[] { "--resourcestype=file" } );
//        final OptionSet noOptions = parser.parse( new String[] {} );
//        
//        assertEquals( JarResources.class, Main.getResources(jarOptions).getClass() );
//        assertEquals( FileResources.class, Main.getResources(fileOptions).getClass() );
//        assertEquals( FileResources.class, Main.getResources(noOptions).getClass() ); // default
//        
//    }
//
//    public void testGetServer() throws Exception {
//        
//        final OptionParser parser = Options.getParser();
//        final OptionSet sslOptions = parser.parse( new String[] { "--ssl" } );
//        final OptionSet stdOptions = parser.parse( new String[] {} );
//        
//        assertEquals( HttpsServer.class, Main.getServer( 4444, sslOptions ).getClass() );
//        assertEquals( HttpServer.class, Main.getServer( 4444, stdOptions ).getClass() );
//        
//    }
//
//    public void testGettingTheGuiAsManager() {
//        assertEquals( AppFrame.class, Main.getManager(true,null).getClass() );
//    }
//
//    public void testGettingConsoleAsManager() {
//        assertEquals( Console.class, Main.getManager(false,null).getClass() );
//    }

}
