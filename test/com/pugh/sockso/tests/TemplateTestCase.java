
package com.pugh.sockso.tests;

import com.pugh.sockso.Properties;
import com.pugh.sockso.web.User;

import org.jamon.Renderer;

public abstract class TemplateTestCase extends SocksoTestCase {

    public String render( final Properties p ) {

        return render( p, null );

    }

    public String render( final Properties p, final User user ) {

        return getTemplate( p, user ).asString();

    }

    public Renderer getTemplate( final Properties p ) {

        return getTemplate( p, null );

    }

    /**
     *  Returns the renderer for the template that will be under test
     *
     *  @param p
     *  @param user
     *
     *  @return
     *
     */

    public abstract Renderer getTemplate( final Properties p, final User user );

}
