
package com.pugh.sockso.templates.web;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.Utils;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.tests.TemplateTestCase;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.tests.TestLocale;
import com.pugh.sockso.web.User;

import org.jamon.Renderer;

import static org.easymock.EasyMock.*;

public class IHeaderTest extends TemplateTestCase {
    
    private Properties p;

    private User user;

    @Override
    protected void setUp() {
        p = new StringProperties();
    }

    public void testDefault() {
        assertTrue( render().length() > 0 );
    }

    public Renderer getTemplate() {

        final IHeader tpl = new IHeader();

        tpl.setUser( user );
        tpl.setProperties( p );
        tpl.setLocale( new TestLocale() );

        return tpl.makeRenderer();

    }

    public void testBrowseLink() {

        p.set( Constants.WWW_BROWSE_FOLDERS_ENABLED, Properties.YES );
        p.set( Constants.WWW_BROWSE_FOLDERS_ONLY, Properties.YES );
        assertTrue( render().contains("/browse/folders") );

        p.set( Constants.WWW_BROWSE_FOLDERS_ENABLED, Properties.NO );
        p.set( Constants.WWW_BROWSE_FOLDERS_ONLY, Properties.YES );
        assertTrue( render().contains("/browse/letter/a") );

        p.set( Constants.WWW_BROWSE_FOLDERS_ENABLED, Properties.YES );
        p.set( Constants.WWW_BROWSE_FOLDERS_ONLY, Properties.NO );
        assertTrue( render().contains("/browse/letter/a") );

    }

    public void testTitle() {

        final String title = Utils.getRandomString( 20 );
        
        p.set( Constants.WWW_TITLE, title );
        assertTrue( render().contains("<title>" +title+ "</title>") );
        assertTrue( render().contains("<h1><a href=\"/\">" +title+ "</a></h1>") );

    }

    public void testSkin() {

        final String skin = Utils.getRandomString( 20 );
        
        p.set( Constants.WWW_SKIN, skin );
        assertTrue( render().contains("/file/skins/" +skin+ "/css/default.css") );
        assertTrue( render().contains("/file/skins/" +skin+ "/images/favicon.ico") );
        
    }

    public void testLoginLinkAppearsWhenUserIsNotLoggedIn() {
        assertTrue( render().contains("/user/login") );
    }

    public void testLoginLinkDoesntAppearWhenUserIsLoggedIn() {
        user = new User( 1, "foo" );
        assertTrue( !render().contains("/user/login") );
    }

    public void testLogoutLinkAppearsWhenUserIsLoggedIn() {
        user = new User( 1, "foo" );
        assertTrue( render().contains("/user/logout") );
    }

    public void testDisableRegistration() {

        p.set( Constants.WWW_USERS_DISABLE_REGISTRATION, Properties.YES );
        assertTrue( !render().contains("/user/register") );
        p.set( Constants.WWW_USERS_DISABLE_REGISTRATION, Properties.NO );
        assertTrue( render().contains("/user/register") );
        
        user = new User( 1, "foo" );

        // when user logged in, should never see register link
        p.set( Constants.WWW_USERS_DISABLE_REGISTRATION, Properties.NO );
        assertTrue( !render().contains("/user/register") );
        p.set( Constants.WWW_USERS_DISABLE_REGISTRATION, Properties.YES );
        assertTrue( !render().contains("/user/register") );

    }

    public void testUploadsEnabled() {

        p.set( Constants.WWW_UPLOADS_ENABLED, Properties.YES );
        p.set( Constants.WWW_UPLOADS_ALLOW_ANONYMOUS, Properties.YES );
        assertTrue( render().contains("/upload") );

        p.set( Constants.WWW_UPLOADS_ENABLED, Properties.YES );
        p.set( Constants.WWW_UPLOADS_ALLOW_ANONYMOUS, Properties.NO );
        assertTrue( !render().contains("/upload") );

        p.set( Constants.WWW_UPLOADS_ENABLED, Properties.NO );
        p.set( Constants.WWW_UPLOADS_ALLOW_ANONYMOUS, Properties.NO );
        assertTrue( !render().contains("/upload") );

        p.set( Constants.WWW_UPLOADS_ENABLED, Properties.NO );
        p.set( Constants.WWW_UPLOADS_ALLOW_ANONYMOUS, Properties.YES );
        assertTrue( !render().contains("/upload") );

        user = new User( 1, "foo" );

        p.set( Constants.WWW_UPLOADS_ENABLED, Properties.YES );
        p.set( Constants.WWW_UPLOADS_ALLOW_ANONYMOUS, Properties.NO );
        assertTrue( render().contains("/upload") );

        p.set( Constants.WWW_UPLOADS_ENABLED, Properties.NO );
        p.set( Constants.WWW_UPLOADS_ALLOW_ANONYMOUS, Properties.NO );
        assertTrue( !render().contains("/upload") );

        p.set( Constants.WWW_UPLOADS_ENABLED, Properties.NO );
        p.set( Constants.WWW_UPLOADS_ALLOW_ANONYMOUS, Properties.YES );
        assertTrue( !render().contains("/upload") );

    }

    public void testMetaTagsAreAddedViaProperties() {
        p.set( Constants.WWW_METATAGS + ".foo", "bar" );        
        p.set( Constants.WWW_METATAGS + ".foo.bazzle", "baar" );        
        assertContains( render(), "name=\"foo\"" );
        assertContains( render(), "content=\"bar\"" );
        assertContains( render(), "name=\"foo.bazzle\"" );
        assertContains( render(), "content=\"baar\"" );
    }

    public void testMetaTagPrefixIsRemovedFromMetaTagNames() {
        p.set( Constants.WWW_METATAGS + ".foo", "bar" );        
        assertNotContains( render(), Constants.WWW_METATAGS );
    }
    
}
