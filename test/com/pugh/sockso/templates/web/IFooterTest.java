
package com.pugh.sockso.templates.web;

import com.pugh.sockso.Sockso;
import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.tests.TemplateTestCase;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.web.User;

import java.util.Vector;

import org.jamon.Renderer;

import static org.easymock.EasyMock.*;

public class IFooterTest extends TemplateTestCase {

    private Locale locale;

    @Override
    public void setUp() {

        locale = createNiceMock( Locale.class );
        replay( locale );

    }

    public Renderer getTemplate( final Properties p, final User user ) {

        final IFooter tpl = new IFooter();

        tpl.setUser( user );
        tpl.setProperties( p );
        tpl.setLocale( locale );

        return tpl.makeRenderer();

    }

    public void testPropertiesJavascript() {

        final Properties p = new StringProperties();

        p.set( "www.something", "foobar" );
        p.set( "app.another", "barfoo" );

        assertTrue( render(p).contains("foobar") );
        assertTrue( !render(p).contains("barfoo") );

    }

    public void testDevMode() {

        final Properties p = new StringProperties();

        p.set( "dev.enabled", Properties.YES );
        assertTrue( render(p).contains("jquery.js") );

        p.set( "dev.enabled", Properties.NO );
        assertTrue( !render(p).contains("jquery.js") );
        assertTrue( render(p).contains("packed-" +Sockso.VERSION+ ".js") );

    }
    
    public void testVersionInfo() {
        
        final Properties p = new StringProperties();
        
        assertTrue( render(p).contains("v" +Sockso.VERSION+ "<br />") );
        
    }

    public void testRecentUsers() {

        final Properties p = new StringProperties();
        final Vector<User> users = new Vector<User>();

        assertTrue( !render(p).contains("recentUsers") );

        final Renderer r1 = getTemplate( p, null );
        assertTrue( !r1.asString().contains("recentUsers") );

        users.add( new User(1,"foo useR") );
        users.add( new User(1,"bar User") );
        final IFooter tpl1 = new IFooter();
        tpl1.setProperties( p );
        tpl1.setLocale( locale );
        tpl1.setRecentUsers( users );
        final String html = tpl1.makeRenderer().asString();
        assertTrue( html.contains("recentUsers") );
        assertTrue( html.contains("foo useR") );
        assertTrue( html.contains("bar User") );

    }

}
