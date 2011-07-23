
package com.pugh.sockso.web.action.browse;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Collection;
import com.pugh.sockso.templates.web.browse.TFolders;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.action.BaseAction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.io.IOException;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 *  shows folder browsing
 * 
 */

public class Folderer extends BaseAction {

    private static final Logger log = Logger.getLogger( Folderer.class  );

    /**
     *  shows the page with the folder browser on it
     * 
     *  @throws java.io.IOException
     *  @throws java.sql.SQLException
     *  @throws com.pugh.sockso.web.BadRequestException
     * 
     */
    
    public void handleRequest() throws IOException, SQLException, BadRequestException {

        // check folder browsing is enabled
        Utils.checkFeatureEnabled( getProperties(), Constants.WWW_BROWSE_FOLDERS_ENABLED );

        showFolders( getCollections() );
        
    }
    
    /**
     *  shows the template for folder browsing
     * 
     *  @param folders
     * 
     *  @throws java.io.IOException
     * 
     */
    
    protected void showFolders( final Vector<Collection> folders ) throws IOException, SQLException {

        final TFolders tpl = new TFolders();
        
        tpl.setFolders( folders );
        
        getResponse().showHtml( tpl );
                    
    }

    /**
     *  returns the collections the user has added
     * 
     *  @return
     * 
     *  @throws java.sql.SQLException
     * 
     */
    
    protected Vector<Collection> getCollections() throws SQLException {

        ResultSet rs = null;
        PreparedStatement st = null;
        
        try {
            
            final Database db = getDatabase();
            final String sql = " select c.id, c.path " +
                               " from collection c ";
            st = db.prepare( sql );

            rs = st.executeQuery();

            final Vector<Collection> folders = new Vector<Collection>();
            while ( rs.next() )
                folders.add( new Collection(
                    rs.getInt( "id" ),
                    rs.getString( "path" )
                ));
            
            return folders;
            
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
    }

}
