
package com.pugh.sockso.web.action.api;

import com.pugh.sockso.music.Genre;
import com.pugh.sockso.templates.api.TGenres;
import com.pugh.sockso.web.Request;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GenresAction extends BaseApiAction {

    @Override
    public boolean canHandle( final Request req ) {

        return req.getParamCount() == 2
            && req.getUrlParam( 1 ).equals( "genres" );

    }

    /**
     *  Shows the requested list of genres
     *
     *  @throws IOException
     *
     */

    public void handleRequest() throws SQLException, IOException {

        final List<Genre> genres = Genre.findAll(
            getDatabase(), getLimit(), getOffset()
        );

        showGenres( genres.toArray( new Genre[] {} ) );

    }

    /**
     * Shows the specified genres
     *
     * @param genres
     *
     * @throws IOException
     *
     */

    protected void showGenres( final Genre[] genres ) throws IOException {

        TGenres tpl = new TGenres();
        tpl.setGenres( genres );

        getResponse().showJson( tpl.makeRenderer() );

    }

}
