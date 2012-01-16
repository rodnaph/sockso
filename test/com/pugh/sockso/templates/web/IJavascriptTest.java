
package com.pugh.sockso.templates.web;

import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;

import com.pugh.sockso.tests.TemplateTestCase;
import com.pugh.sockso.tests.TestLocale;

import org.jamon.Renderer;

public class IJavascriptTest extends TemplateTestCase {

    private Properties p;

    @Override
    protected void setUp() {
        p = new StringProperties();
    }

    public Renderer getTemplate() {
        IJavascript tpl = new IJavascript();
        tpl.setLocale( new TestLocale() );
        tpl.setProperties( p );
        return tpl.makeRenderer();
    }

    public void testPropertiesCanBeLessThen4Characters() {
        p.set( "foo", "bar" );
        render();
    }

}
