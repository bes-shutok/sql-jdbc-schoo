package ua.com.foxminded.sql.jdbc.school.dao.impl;

import ua.com.foxminded.sql.jdbc.school.dao.AbstractCrudDao;
import ua.com.foxminded.sql.jdbc.school.dao.GroupDao;
import ua.com.foxminded.sql.jdbc.school.model.Group;
import ua.com.foxminded.sql.jdbc.school.utils.SqlUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


public class GroupDaoImpl extends AbstractCrudDao<Group, Long> implements GroupDao {
    private static final String CREATE_GROUP = "INSERT INTO " + Group.TABLE_NAME + " (" +
            Group.GROUP_NAME + ") " + "VALUES (?);";

    private static final String UPDATE_GROUP = "UPDATE " + Group.TABLE_NAME + " SET " +
            Group.GROUP_NAME + " = ? " + "WHERE " + Group.GROUP_ID + " = ?;";

    private static final String ALL_GROUPS = "SELECT * FROM " + Group.TABLE_NAME + ";";

    private static final String FIND_GROUP_BY_ID = "SELECT * FROM " + Group.TABLE_NAME + " WHERE " + Group.GROUP_ID
            + " = ?;";

    private static final String FIND_GROUP_BY_NAME = "SELECT * FROM " + Group.TABLE_NAME
            + " WHERE " + Group.GROUP_NAME + " = ?;";

    private static final String DELETE_GROUP = "DELETE FROM " + Group.TABLE_NAME + " WHERE " + Group.GROUP_ID + " = ?;";

    @Override
    protected void create(Connection con, Group entity) throws SQLException {
        SqlUtils.executeDmlQuery(con, CREATE_GROUP, new Object[] {entity.getName()});
    }

    @Override
    protected void update(Connection con, Group entity) throws SQLException {
        SqlUtils.executeDmlQuery(con, UPDATE_GROUP, new Object[] {entity.getName(), entity.getId()});
    }

    @Override
    public void deleteById(Connection con, Long id) throws SQLException {
        SqlUtils.executeDmlQuery(con, DELETE_GROUP, new Object[] {id});
    }

    @Override
    public List<Group> findAll(Connection con) throws SQLException {
        return SqlUtils.executeQuery(con, ALL_GROUPS, new Object[0], Group::getGroups);
    }

    @Override
    public Optional<Group> findById(Connection con, Long id) throws SQLException {
        return SqlUtils.executeQuery(
                con,
                FIND_GROUP_BY_ID,
                new Object[] {id},
                resultSet -> SqlUtils.getOptionalResult(resultSet, Group::getGroup)
        );
    }

    @Override
    public Optional<Group> findByName(Connection con, String name) throws SQLException {
        return SqlUtils.executeQuery(
                con,
                FIND_GROUP_BY_NAME,
                new Object[] {name},
                resultSet -> SqlUtils.getOptionalResult(resultSet, Group::getGroup)
        );
    }

}

