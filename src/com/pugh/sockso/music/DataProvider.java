
package com.pugh.sockso.music;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Nathan Perrier
 */
public interface DataProvider<T> {

    public abstract List<T> findAll( final int limit, final int offset ) throws SQLException;

    public abstract List<T> findAll( final int limit, final int offset, final Date fromDate ) throws SQLException;

    public abstract T find( final int id ) throws SQLException;

    public abstract void update( T t ) throws SQLException;

    public abstract T save( T t ) throws SQLException;

    public abstract T saveOrUpdate( T t ) throws SQLException;

}
