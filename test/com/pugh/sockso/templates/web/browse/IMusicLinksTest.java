
package com.pugh.sockso.templates.web.browse;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.tests.TemplateTestCase;
import com.pugh.sockso.web.User;

import org.jamon.Renderer;

import static org.easymock.EasyMock.*;

public class IMusicLinksTest extends TemplateTestCase {

    private Locale locale;
    private String name;

    @Override
    public void setUp() {

        name = "";
        locale = createNiceMock( Locale.class );
        replay( locale );

    }

    public Renderer getTemplate( final Properties p, final User user ) {
        return getTemplate( p, user, false );
    }

    public Renderer getTemplate( final Properties p, final User user, final boolean shareLink ) {
        return getTemplate( p, user, shareLink, false );
    }
    public Renderer getTemplate( final Properties p, final User user, final boolean shareLink, final boolean playRandomLink ) {

        final IMusicLinks tpl = new IMusicLinks();

        tpl.setProperties( p );
        tpl.setLocale( locale );
        tpl.setType( "" );
        tpl.setName( name );
        tpl.setShareLink( shareLink );
        tpl.setPlayRandomLink( playRandomLink );

        return tpl.makeRenderer();

    }

    public void testDisableDownloads() {

        final Properties p = new StringProperties();

        p.set( Constants.WWW_DOWNLOADS_DISABLE, Properties.NO );
        assertTrue( render(p).contains("/download/") );

        p.set( Constants.WWW_DOWNLOADS_DISABLE, Properties.YES );
        assertTrue( !render(p).contains("/download/") );

    }

    public void testShareLink() {
        
        final Properties p = new StringProperties();

        assertTrue( getTemplate(p,null,true).asString().contains("share-music") );
        assertTrue( !getTemplate(p,null,false).asString().contains("share-music") );

    }

    public void testPlayRandomLink() {

        final Properties p = new StringProperties();

        assertTrue( getTemplate(p,null,false,true).asString().contains("orderBy=random") );
        assertTrue( !getTemplate(p,null,false,false).asString().contains("orderBy=random") );

    }

    public void testQuotesInNamesAreEscapedInName() {
        
        name = "f\"o\"o";

        final Properties p = new StringProperties();
        final String html = getTemplate( p, null ).asString();

        assertTrue( html.contains("f&quot;o&quot;o") );

    }

}
