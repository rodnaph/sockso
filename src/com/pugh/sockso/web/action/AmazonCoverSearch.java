
package com.pugh.sockso.web.action;

import com.google.inject.Inject;
import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.CoverArt;

import java.awt.image.BufferedImage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.net.URL;
import java.net.HttpURLConnection;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

/**
 *  searches amazon for album covers
 *
 *  @author rod
 *
 */

public class AmazonCoverSearch extends AbstractCoverSearch {

    protected static final Logger log = Logger.getLogger( AbstractCoverSearch.class );

    @Inject
    public AmazonCoverSearch( final Database db ) {

        super( db );

    }

    /**
     *  tries to search amazon for a cover for the specified itemName (eg. ar123)
     *  if nothing is found then null is returned.
     *
     *  @param itemName
     *
     *  @return
     *
     */

    public CoverArt getCover( final String itemName ) {

        try {

            final String keywords = getSearchKeywords( itemName );

            // no keywords, no searching...
            if ( keywords == null ) return null;

            final String urlString = getCoverUrl( keywords );

            if ( urlString != null ) {

                final URL url = new URL( urlString );
                final HttpURLConnection cnn = (HttpURLConnection) url.openConnection();
                final BufferedImage cover = ImageIO.read( cnn.getInputStream() );
                CoverArt coverArtImage = new CoverArt( itemName, cover );
                return coverArtImage;

            }

        }

        catch ( final IOException e ) {
            log.debug( e );
        }

        return null;

    }

    /**
     *  searches amazon for an image matching the speficied keywords
     *
     *  @param keywords the search criteria
     *
     *  @return the image url
     *
     *  @throws IOException
     *
     */

    protected String getCoverUrl( final String keywords  ) throws IOException {

        final String searchUrl = "http://www.amazon.com/s/?url=search-alias%3Dpopular&field-keywords=" + Utils.URLEncode(keywords);

        log.debug( "Amazon query: " +searchUrl );

        final URL url = new URL( searchUrl );
        final HttpURLConnection cnn = (HttpURLConnection) url.openConnection();

        cnn.setRequestMethod( "GET" );

        return getCoverFromSearchResults( cnn );

    }

    /**
     *  read through an amazon search to try and extract the url of a cover image.
     *  if nothing is found then null is returned.
     *
     *  @param cnn
     *
     *  @return
     *
     *  @throws java.io.IOException
     *
     */

    protected String getCoverFromSearchResults( final HttpURLConnection cnn ) throws IOException {

        String s = "";
        BufferedReader in = null;

        try {

            in = new BufferedReader(new InputStreamReader(cnn.getInputStream()) );
            while ( (s = in.readLine()) != null ) {
                final String imageUrl = getCoverFromData( s );
                if ( imageUrl != null ) {
                    return imageUrl;
                }
            }

        }

        finally { Utils.close(in); }

        return null;

    }

    /**
     *  Tries to extract a cover image URL from the given data
     *
     *  @param data
     *
     *  @return
     *
     */

    protected String getCoverFromData( final String data ) {

        final String[] patterns = {
            ".*img src=\"(http://\\w+.images-amazon.com/images/\\w+/.+?_SL.+?_A.+?_\\.(jpg|gif|png))\\\".*",
            ".*img src=\"(http://\\w+.images-amazon.com/images/\\w+/.+?_AA\\d+_\\.(jpg|gif|png))\\\".*"
        };

        for ( final String patternString : patterns ) {

            final Pattern pattern = Pattern.compile( patternString );
            final Matcher m = pattern.matcher( data );

            if ( m.matches() ) {
                return m.group( 1 );
            }

        }

        return null;

    }

}
