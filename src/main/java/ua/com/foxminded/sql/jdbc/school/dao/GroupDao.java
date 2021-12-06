package ua.com.foxminded.sql.jdbc.school.dao;

import ua.com.foxminded.sql.jdbc.school.model.Group;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public interface GroupDao extends CrudDao<Group, Long> {

    // any specific methods declared here
    Optional<Group> findByName(Connection connection, String name) throws SQLException;
}

