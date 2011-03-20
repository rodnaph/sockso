
package com.pugh.sockso.web.action;

import com.pugh.sockso.tests.SocksoTestCase;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

import com.pugh.sockso.tests.MyHttpURLConnection;

public class AmazonCoverSearchTest extends SocksoTestCase {
    
    public void testGetCoverFromSearchResults() throws IOException {

        final String origCover = "http://ecx.images-amazon.com/images/I/41YZSP49YBL._SL160_AA115_.jpg";
        final String data = "da khsdk jahdjk hkjas dkj" +
                            "           junk <img src=\"" +origCover+ "\" more crap\n" +
                            " blah blah";
        final MyHttpURLConnection cnn = new MyHttpURLConnection( data );
        final AmazonCoverSearch s = new AmazonCoverSearch( null );
        final String fetchedCover = s.getCoverFromSearchResults( cnn );

        assertEquals( origCover, fetchedCover );

    }

    public void testGetCoverFromSearchResultsNoResults() throws IOException {

        final AmazonCoverSearch s = new AmazonCoverSearch( null );
        final String data = "";
        final MyHttpURLConnection cnn = new MyHttpURLConnection( data );
        final String cover = s.getCoverFromSearchResults( cnn );

        assertNull( cover );

    }

    public void testGettingCoverFromAmazonResults() throws IOException {

        final AmazonCoverSearch s = new AmazonCoverSearch( null );
        final BufferedReader in = new BufferedReader( new FileReader("test/data/amazon-result.html") );
        final String expected = "http://ecx.images-amazon.com/images/I/51whwodX57L._SL160_AA115_.jpg";

        String line = "";

        while ( (line = in.readLine()) != null ) {
            final String actual = s.getCoverFromData( line );
            if ( actual != null ) {
                assertEquals( expected, actual );
                return;
            }
        }

        fail( "No cover found" );

    }

}
