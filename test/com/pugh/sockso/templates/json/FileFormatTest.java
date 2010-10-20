
package com.pugh.sockso.templates.json;

import com.pugh.sockso.tests.SocksoTestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class FileFormatTest extends SocksoTestCase {

    /**
     *  Tests the line endings of the files (needs to be CRLF to work on both
     *  windows and unix)
     * 
     */

    public void testLineEndings() throws Exception {

        final File directory = new File( "templates/com/pugh/sockso/templates/json" );
        final File[] files = directory.listFiles();

        for ( final File file : files ) {

            final InputStreamReader in = new InputStreamReader( new FileInputStream(file) );
            final StringBuffer contents = new StringBuffer( "" );

            char lastChar = '\0';

            while ( in.ready() ) {
                final char thisChar = (char) in.read();
                if ( thisChar == '\n' && lastChar != '\r' ) {
                    fail( "UNIX line ending found in '" +file.getName()+ "' - JSON files need to be CRLF for Windows" );
                }
                lastChar = thisChar;
            }

        }

        assertTrue( true );

    }

}
