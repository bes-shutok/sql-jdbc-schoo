package ua.com.foxminded.sql.jdbc.school.dao;

import ua.com.foxminded.sql.jdbc.school.model.Group;
import ua.com.foxminded.sql.jdbc.school.utils.SqlUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ua.com.foxminded.sql.jdbc.school.model.Group.*;


public class GroupDaoImpl extends AbstractCrudDao<Group, Long> implements GroupDao {

    @Override
    protected Group create(Connection connection, Group entity) throws SQLException {
        String script = String.format(
                "INSERT INTO %s(%s) VALUES ('%s');",
                GROUPS_TABLE_NAME, GROUP_NAME, entity.getName()
        );
        SqlUtils.executeSqlScript(connection, script);
        return entity;
    }

    @Override
    protected Group update(Connection connection, Group entity) throws SQLException {
        String script = String.format(
                "UPDATE %s SET %s = '%s' WHERE %s = %d;",
                GROUPS_TABLE_NAME, GROUP_NAME, entity.getName(), GROUP_ID, entity.getId()
                );
        SqlUtils.executeSqlScript(connection, script);
        return entity;
    }

    @Override
    public Optional<Group> findById(Connection connection, Long id) throws SQLException {
        String script = String.format( "SELECT * FROM %s WHERE %s = %d;", GROUPS_TABLE_NAME, GROUP_ID, id);
        Optional<Group> result = Optional.empty();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(script);
            if (resultSet.next()) {
                result = Optional.of(new Group(id, resultSet.getString(GROUP_NAME)));
            }
        }
        return result;
    }

    @Override
    public List<Group> findAll(Connection connection) throws SQLException {
        String script = String.format("SELECT * FROM %s;", GROUPS_TABLE_NAME);
        List<Group> result = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(script);
            while (resultSet.next()) {
                result.add(new Group(resultSet.getLong(GROUP_ID), resultSet.getString(GROUP_NAME)));
            }
        }
        return result;
    }

    @Override
    public void deleteById(Connection connection, Long id) throws SQLException {
        String script = String.format("DELETE FROM %s WHERE %s = %d;", GROUPS_TABLE_NAME, GROUP_ID, id);
        SqlUtils.executeSqlScript(connection, script);
    }

    @Override
    public Optional<Group> findByName(Connection connection, String name) throws SQLException {
        String script = String.format( "SELECT * FROM %s WHERE %s = '%s';", GROUPS_TABLE_NAME, GROUP_NAME, name);
        Optional<Group> result = Optional.empty();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(script);
            if (resultSet.next()) {
                result = Optional.of(new Group(resultSet.getLong(GROUP_ID), name));
            }
        }
        return result;
    }
}

