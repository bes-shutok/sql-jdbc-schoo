package ua.com.foxminded.sql.jdbc.school.dao.impl;

import ua.com.foxminded.sql.jdbc.school.dao.AbstractCrudDao;
import ua.com.foxminded.sql.jdbc.school.dao.StudentDao;
import ua.com.foxminded.sql.jdbc.school.model.Student;
import ua.com.foxminded.sql.jdbc.school.utils.SqlUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ua.com.foxminded.sql.jdbc.school.model.Group.GROUP_ID;
import static ua.com.foxminded.sql.jdbc.school.model.Student.*;

public class StudentDaoImpl extends AbstractCrudDao<Student, Long> implements StudentDao {
    private static final String CREATE_STUDENT = "INSERT INTO " + STUDENTS_TABLE_NAME + " (" +
            FIRST_NAME + ", " + LAST_NAME + ", " + GROUP_ID + ") " + "VALUES (?, ?, ?);";
    private static final String UPDATE_STUDENT = "UPDATE " + STUDENTS_TABLE_NAME + " SET " +
            FIRST_NAME + " = ?, " + LAST_NAME + " = ?, " + GROUP_ID + " = ? " + "WHERE " + STUDENT_ID + " = ?;";
    private static final String ALL_STUDENTS = "SELECT * FROM " + STUDENTS_TABLE_NAME + ";";
    private static final String FIND_STUDENT_BY_ID = "SELECT * FROM " + STUDENTS_TABLE_NAME
            + " WHERE " + STUDENT_ID + " = ?;";
    private static final String DELETE_STUDENT = "DELETE FROM " + STUDENTS_TABLE_NAME
            + " WHERE " + STUDENT_ID + " = ?;";

    @Override
    protected Student create(Connection connection, Student entity) throws SQLException {
        SqlUtils.executeUpdate(
                connection, CREATE_STUDENT, entity.getFirstName(), entity.getLastName(), entity.getGroupId()
        );
        return entity;
    }

    @Override
    protected Student update(Connection con, Student entity) throws SQLException {
        SqlUtils.executeUpdate(
                con, UPDATE_STUDENT, entity.getFirstName(), entity.getLastName(), entity.getGroupId(), entity.getId()
        );
        return entity;
    }

    @Override
    public void deleteById(Connection connection, Long id) throws SQLException {
        SqlUtils.executeUpdate(connection, DELETE_STUDENT, id);
    }

    /**
     * We cannot use {@link SqlUtils} methods here as {@link ResultSet} have to be parsed at place - it is closed
     * with the statement closing. And we cannot delegate its parsing to {@link SqlUtils} since it would require
     * passing business logic to the util methods, and we don't wont to do it.
     */
    @Override
    public List<Student> findAll(Connection con) throws SQLException {
        List<Student> result = new ArrayList<>();
        try (PreparedStatement st = con.prepareStatement(ALL_STUDENTS)) {
            ResultSet resultSet = st.executeQuery();
            while (resultSet.next()) {
                result.add(
                        new Student(
                                resultSet.getLong(STUDENT_ID),
                                resultSet.getString(FIRST_NAME),
                                resultSet.getString(LAST_NAME),
                                resultSet.getLong(GROUP_ID)
                        )
                );
            }
        }
        return result;
    }

    @Override
    public Optional<Student> findById(Connection con, Long id) throws SQLException {
        Optional<Student> result = Optional.empty();
        try (PreparedStatement st = con.prepareStatement(FIND_STUDENT_BY_ID)) {
            st.setLong(1, id);
            ResultSet resultSet = st.executeQuery();
            if (resultSet.next()) {
                result = Optional.of(
                        new Student(
                                id,
                                resultSet.getString(FIRST_NAME),
                                resultSet.getString(LAST_NAME),
                                resultSet.getLong(GROUP_ID)
                        )
                );
            }
        }
        return result;
    }
}
