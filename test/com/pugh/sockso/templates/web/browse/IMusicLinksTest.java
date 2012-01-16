
package com.pugh.sockso.templates.web.browse;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.tests.TemplateTestCase;
import com.pugh.sockso.tests.TestLocale;
import com.pugh.sockso.web.User;

import org.jamon.Renderer;

import static org.easymock.EasyMock.*;

public class IMusicLinksTest extends TemplateTestCase {

    private String name;
    private Properties p;
    private boolean shareLink = false,
                    playRandomLink = false;

    @Override
    public void setUp() {
        name = "";
        p = new StringProperties();
    }

    public Renderer getTemplate()  {

        final IMusicLinks tpl = new IMusicLinks();

        tpl.setProperties( p );
        tpl.setLocale( new TestLocale() );
        tpl.setType( "" );
        tpl.setName( name );
        tpl.setShareLink( shareLink );
        tpl.setPlayRandomLink( playRandomLink );

        return tpl.makeRenderer();

    }

    public void testDisableDownloads() {

        p.set( Constants.WWW_DOWNLOADS_DISABLE, Properties.NO );
        assertTrue( render().contains("/download/") );

        p.set( Constants.WWW_DOWNLOADS_DISABLE, Properties.YES );
        assertTrue( !render().contains("/download/") );

    }

    public void testShareLink() {
        
        shareLink = true;
        assertTrue( getTemplate().asString().contains("share-music") );

        shareLink = false;
        assertTrue( !getTemplate().asString().contains("share-music") );

    }

    public void testPlayRandomLink() {

        playRandomLink = true;
        assertTrue( getTemplate().asString().contains("orderBy=random") );

        playRandomLink = false;
        assertTrue( !getTemplate().asString().contains("orderBy=random") );

    }

    public void testQuotesInNamesAreEscapedInName() {
        
        name = "f\"o\"o";

        final String html = render();

        assertTrue( html.contains("f&quot;o&quot;o") );

    }

}
