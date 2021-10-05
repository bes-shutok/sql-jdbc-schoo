package ua.com.foxminded.sql.jdbc.school.dao.impl;

import ua.com.foxminded.sql.jdbc.school.dao.AbstractCrudDao;
import ua.com.foxminded.sql.jdbc.school.dao.GroupDao;
import ua.com.foxminded.sql.jdbc.school.model.Group;
import ua.com.foxminded.sql.jdbc.school.utils.SqlUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ua.com.foxminded.sql.jdbc.school.model.Group.*;


public class GroupDaoImpl extends AbstractCrudDao<Group, Long> implements GroupDao {
    private static final String CREATE_GROUP = "INSERT INTO " + GROUPS_TABLE_NAME + " (" +
            GROUP_NAME + ") " + "VALUES (?);";
    private static final String UPDATE_GROUP = "UPDATE " + GROUPS_TABLE_NAME + " SET " +
            GROUP_NAME + " = ?, " + "WHERE " + GROUP_ID + " = ?;";
    private static final String ALL_GROUPS = "SELECT * FROM " + GROUPS_TABLE_NAME + ";";
    private static final String FIND_GROUP_BY_ID = "SELECT * FROM " + GROUPS_TABLE_NAME + " WHERE " + GROUP_ID + " = ?;";
    private static final String FIND_GROUP_BY_NAME = "SELECT * FROM " + GROUPS_TABLE_NAME
            + " WHERE " + GROUP_NAME + " = ?;";
    private static final String DELETE_GROUP = "DELETE FROM " + GROUPS_TABLE_NAME + " WHERE " + GROUP_ID + " = ?;";

    @Override
    protected Group create(Connection connection, Group entity) throws SQLException {
        SqlUtils.executeUpdate(connection, CREATE_GROUP, entity.getName());
        return entity;
    }

    @Override
    protected Group update(Connection connection, Group entity) throws SQLException {
        SqlUtils.executeUpdate(connection, UPDATE_GROUP, entity.getName(), entity.getId());
        return entity;
    }

    @Override
    public void deleteById(Connection connection, Long id) throws SQLException {
        SqlUtils.executeUpdate(connection, DELETE_GROUP, id);
    }

    /**
     * We cannot use {@link SqlUtils} methods here as {@link ResultSet} have to be parsed at place - it is closed
     * with the statement closing. And we cannot delegate its parsing to {@link SqlUtils} since it would require
     * passing business logic to the util methods, and we don't wont to do it.
     */
    @Override
    public List<Group> findAll(Connection con) throws SQLException {
        List<Group> result = new ArrayList<>();
        try (PreparedStatement st = con.prepareStatement(ALL_GROUPS)) {
            ResultSet resultSet = st.executeQuery();
            while (resultSet.next()) {
                result.add(new Group(resultSet.getLong(GROUP_ID), resultSet.getString(GROUP_NAME)));
            }
        }
        return result;
    }

    @Override
    public Optional<Group> findById(Connection con, Long id) throws SQLException {
        Optional<Group> result = Optional.empty();
        try (PreparedStatement st = con.prepareStatement(FIND_GROUP_BY_ID)) {
            st.setLong(1, id);
            ResultSet resultSet = st.executeQuery();
            if (resultSet.next()) {
                result = Optional.of(new Group(id, resultSet.getString(GROUP_NAME)));
            }
        }
        return result;
    }

    @Override
    public Optional<Group> findByName(Connection con, String name) throws SQLException {
        Optional<Group> result = Optional.empty();
        try (PreparedStatement st = con.prepareStatement(FIND_GROUP_BY_NAME)) {
            st.setString(1, name);
            ResultSet resultSet = st.executeQuery();
            if (resultSet.next()) {
                result = Optional.of(new Group(resultSet.getLong(GROUP_ID), name));
            }
        }
        return result;
    }
}

