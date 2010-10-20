
package com.pugh.sockso.templates.web;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.Utils;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.tests.TemplateTestCase;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.web.User;

import org.jamon.Renderer;

import static org.easymock.EasyMock.*;

public class IHeaderTest extends TemplateTestCase {
    
    private Locale locale;

    @Override
    public void setUp() {

        locale = createNiceMock( Locale.class );
        replay( locale );

    }

    public void testDefault() {

        final Properties p = new StringProperties();

        final IHeader tpl = new IHeader();
        tpl.setProperties( p );
        tpl.setLocale( locale );

        final String data = tpl.makeRenderer().asString();

        assertTrue( data.length() > 0 );

    }

    public Renderer getTemplate( final Properties p, final User user ) {

        final IHeader tpl = new IHeader();

        tpl.setUser( user );
        tpl.setProperties( p );
        tpl.setLocale( locale );

        return tpl.makeRenderer();

    }

    public void testBrowseLink() {

        final Properties p = new StringProperties();

        p.set( Constants.WWW_BROWSE_FOLDERS_ENABLED, Properties.YES );
        p.set( Constants.WWW_BROWSE_FOLDERS_ONLY, Properties.YES );
        assertTrue( render(p).contains("/browse/folders") );

        p.set( Constants.WWW_BROWSE_FOLDERS_ENABLED, Properties.NO );
        p.set( Constants.WWW_BROWSE_FOLDERS_ONLY, Properties.YES );
        assertTrue( render(p).contains("/browse/letter/a") );

        p.set( Constants.WWW_BROWSE_FOLDERS_ENABLED, Properties.YES );
        p.set( Constants.WWW_BROWSE_FOLDERS_ONLY, Properties.NO );
        assertTrue( render(p).contains("/browse/letter/a") );

    }

    public void testTitle() {

        final Properties p = new StringProperties();
        final String title = Utils.getRandomString( 20 );
        
        p.set( Constants.WWW_TITLE, title );
        assertTrue( render(p).contains("<title>" +title+ "</title>") );
        assertTrue( render(p).contains("<h1><a href=\"/\">" +title+ "</a></h1>") );

    }

    public void testSkin() {

        final Properties p = new StringProperties();
        final String skin = Utils.getRandomString( 20 );
        
        p.set( Constants.WWW_SKIN, skin );
        assertTrue( render(p).contains("/file/skins/" +skin+ "/css/default.css") );
        assertTrue( render(p).contains("/file/skins/" +skin+ "/images/favicon.ico") );
        
    }

    public void testUserLoggedIn() {

        final Properties p = new StringProperties();
        final User user = new User( 1, "foo" );

        assertTrue( render(p,null).contains("/user/login") );
        assertTrue( !render(p,user).contains("/user/login") );
        assertTrue( render(p,user).contains("/user/logout") );
        
    }

    public void testDisableRegistration() {

        final Properties p = new StringProperties();
        final User user = new User( 1, "foo" );

        p.set( Constants.WWW_USERS_DISABLE_REGISTRATION, Properties.YES );
        assertTrue( !render(p,null).contains("/user/register") );
        p.set( Constants.WWW_USERS_DISABLE_REGISTRATION, Properties.NO );
        assertTrue( render(p,null).contains("/user/register") );
        
        // when user logged in, should never see register link
        p.set( Constants.WWW_USERS_DISABLE_REGISTRATION, Properties.NO );
        assertTrue( !render(p,user).contains("/user/register") );
        p.set( Constants.WWW_USERS_DISABLE_REGISTRATION, Properties.YES );
        assertTrue( !render(p,user).contains("/user/register") );

    }

    public void testUploadsEnabled() {

        final Properties p = new StringProperties();
        final User user = new User( 1, "foo" );

        p.set( Constants.WWW_UPLOADS_ENABLED, Properties.YES );
        p.set( Constants.WWW_UPLOADS_ALLOW_ANONYMOUS, Properties.YES );
        assertTrue( render(p,null).contains("/upload") );

        p.set( Constants.WWW_UPLOADS_ENABLED, Properties.YES );
        p.set( Constants.WWW_UPLOADS_ALLOW_ANONYMOUS, Properties.NO );
        assertTrue( !render(p,null).contains("/upload") );

        p.set( Constants.WWW_UPLOADS_ENABLED, Properties.YES );
        p.set( Constants.WWW_UPLOADS_ALLOW_ANONYMOUS, Properties.NO );
        assertTrue( render(p,user).contains("/upload") );

        p.set( Constants.WWW_UPLOADS_ENABLED, Properties.NO );
        p.set( Constants.WWW_UPLOADS_ALLOW_ANONYMOUS, Properties.NO );
        assertTrue( !render(p,null).contains("/upload") );

        p.set( Constants.WWW_UPLOADS_ENABLED, Properties.NO );
        p.set( Constants.WWW_UPLOADS_ALLOW_ANONYMOUS, Properties.NO );
        assertTrue( !render(p,user).contains("/upload") );

        p.set( Constants.WWW_UPLOADS_ENABLED, Properties.NO );
        p.set( Constants.WWW_UPLOADS_ALLOW_ANONYMOUS, Properties.YES );
        assertTrue( !render(p,null).contains("/upload") );

        p.set( Constants.WWW_UPLOADS_ENABLED, Properties.NO );
        p.set( Constants.WWW_UPLOADS_ALLOW_ANONYMOUS, Properties.YES );
        assertTrue( !render(p,user).contains("/upload") );

    }
}
