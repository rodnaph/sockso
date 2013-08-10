
package com.pugh.sockso.music;

import com.google.inject.Inject;
import com.pugh.sockso.db.Database;
import java.sql.SQLException;

/**
 *
 * @author Nathan Perrier
 */
abstract class AbstractDataProvider<T extends MusicItem> implements DataProvider<T> {

    protected Database db;

    static final int NO_ID = -1;

    @Inject
    public AbstractDataProvider( Database db ) {
        this.db = db;
    }

    public T saveOrUpdate( final T item ) throws SQLException {

        int id = exists(item);

        if ( id == NO_ID ) {
            save(item);
        }
        else {
            update(item);
        }

        return item;
    }

    protected abstract int exists( T t ) throws SQLException;

}
