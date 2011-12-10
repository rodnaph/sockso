
package com.pugh.sockso.templates.web;

import com.pugh.sockso.Sockso;
import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.tests.TemplateTestCase;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.tests.TestLocale;
import com.pugh.sockso.web.User;

import java.util.Vector;

import org.jamon.Renderer;

import static org.easymock.EasyMock.*;

public class IFooterTest extends TemplateTestCase {

    private Properties p;
    
    @Override
    protected void setUp() {
        p = new StringProperties();
    }

    public Renderer getTemplate() {

        final IFooter tpl = new IFooter();

        tpl.setProperties( p );
        tpl.setLocale( new TestLocale() );

        return tpl.makeRenderer();

    }

    public void testPropertiesJavascript() {

        p.set( "www.something", "foobar" );
        p.set( "app.another", "barfoo" );

        assertTrue( render().contains("foobar") );
        assertTrue( !render().contains("barfoo") );

    }

    public void testDevMode() {

        p.set( "dev.enabled", Properties.YES );
        assertTrue( render().contains("jquery.js") );

        p.set( "dev.enabled", Properties.NO );
        assertTrue( !render().contains("jquery.js") );
        assertTrue( render().contains("packed-" +Sockso.VERSION+ ".js") );

    }
    
    public void testVersionInfo() {
        
        assertTrue( render().contains("v" +Sockso.VERSION+ "<br />") );
        
    }

    public void testRecentUsers() {

        final Properties p = new StringProperties();
        final Vector<User> users = new Vector<User>();

        assertTrue( !render().contains("recentUsers") );

        final Renderer r1 = getTemplate();
        assertTrue( !r1.asString().contains("recentUsers") );

        users.add( new User(1,"foo useR") );
        users.add( new User(1,"bar User") );
        final IFooter tpl1 = new IFooter();
        tpl1.setProperties( p );
        tpl1.setLocale( new TestLocale() );
        tpl1.setRecentUsers( users );
        final String html = tpl1.makeRenderer().asString();
        assertTrue( html.contains("recentUsers") );
        assertTrue( html.contains("foo useR") );
        assertTrue( html.contains("bar User") );

    }

}
