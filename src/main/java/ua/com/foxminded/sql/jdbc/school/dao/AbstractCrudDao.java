package ua.com.foxminded.sql.jdbc.school.dao;

import ua.com.foxminded.sql.jdbc.school.model.Entity;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractCrudDao<T extends Entity<K>, K> implements CrudDao<T, K> {

    @Override
    public T save(Connection connection, T entity) throws SQLException {

        // if id==null then we need to create new row in db otherwise - update existing one
        return entity.getId() == null ? create(connection, entity) : update(connection, entity);
    }

    protected abstract T create(Connection connection, T entity) throws SQLException;

    protected abstract T update(Connection connection, T entity) throws SQLException;
}

