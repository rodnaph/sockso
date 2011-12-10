
package com.pugh.sockso.tests;

import org.jamon.Renderer;

public abstract class TemplateTestCase extends SocksoTestCase {

    /**
     *  Render the template and return the result as a string
     * 
     *  @return 
     * 
     */
    
    public String render() {

        return getTemplate().asString();

    }

    /**
     *  Returns the renderer for the template that will be under test
     *
     *  @return
     *
     */

    public abstract Renderer getTemplate();

}
