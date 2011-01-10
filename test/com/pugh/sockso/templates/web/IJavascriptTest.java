
package com.pugh.sockso.templates.web;

import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.web.User;

import com.pugh.sockso.tests.TemplateTestCase;
import com.pugh.sockso.tests.TestLocale;

import org.jamon.Renderer;

public class IJavascriptTest extends TemplateTestCase {

    public Renderer getTemplate( final Properties p, final User user ) {
        IJavascript tpl = new IJavascript();
        tpl.setLocale( new TestLocale() );
        tpl.setProperties( p );
        tpl.setUser( user );
        return tpl.makeRenderer();
    }

    public void testPropertiesCanBeLessThen4Characters() {
        Properties p = new StringProperties();
        p.set( "foo", "bar" );
        render( p );
    }

}
