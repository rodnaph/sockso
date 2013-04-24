
package com.pugh.sockso.web;

import java.io.Writer;
import java.io.IOException;

import org.jamon.Renderer;

public class JsonRenderer implements Renderer {

    private final Renderer renderer;

    public JsonRenderer( final Renderer renderer ) {
        this.renderer = renderer;
    }
    
    @Override
    public String asString() {
        return null;
    }

    @Override
    public void renderTo( final Writer writer ) throws IOException {
        this.renderer.renderTo( new JsonWriter(writer) );
    }

}
