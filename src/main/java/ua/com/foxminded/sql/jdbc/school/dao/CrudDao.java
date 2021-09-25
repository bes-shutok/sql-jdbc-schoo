package ua.com.foxminded.sql.jdbc.school.dao;


import ua.com.foxminded.sql.jdbc.school.model.Entity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CrudDao<T extends Entity<K>, K> {

    // absence of result is not always an error
    Optional<T> findById(Connection connection, K id) throws SQLException;

    List<T> findAll(Connection connection) throws SQLException;

    // we treat create and update operation in a single method
    T save(Connection connection, T entity) throws SQLException;

    void deleteById(Connection connection, K id) throws SQLException;
}
