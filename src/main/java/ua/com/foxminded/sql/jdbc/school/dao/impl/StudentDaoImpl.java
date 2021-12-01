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

public class StudentDaoImpl extends AbstractCrudDao<Student, Long> implements StudentDao {
    private static final String CREATE_STUDENT = "INSERT INTO " + Student.TABLE_NAME + " (" +
            Student.FIRST_NAME + ", " + Student.LAST_NAME + ", " + GROUP_ID + ") " + "VALUES (?, ?, ?);";

    private static final String UPDATE_STUDENT = "UPDATE " + Student.TABLE_NAME + " SET " +
            Student.FIRST_NAME + " = ?, " + Student.LAST_NAME + " = ?, " + GROUP_ID + " = ? " + "WHERE "
            + Student.STUDENT_ID + " = ?;";

    private static final String ALL_STUDENTS = "SELECT * FROM " + Student.TABLE_NAME + ";";

    private static final String FIND_STUDENT_BY_ID = "SELECT * FROM " + Student.TABLE_NAME
            + " WHERE " + Student.STUDENT_ID + " = ?;";

    private static final String DELETE_STUDENT = "DELETE FROM " + Student.TABLE_NAME
            + " WHERE " + Student.STUDENT_ID + " = ?;";

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
     * Here we need to parse {@link ResultSet} at place. We still could use {@link SqlUtils} method by providing
     * it with {@link ResultSet} mapper but in this case we would be providing parameters as Objects
     * (to generalize the method) and thus lose type validation. All in all it is more readable as it is here
     */
    @Override
    public List<Student> findAll(Connection con) throws SQLException {
        List<Student> result = new ArrayList<>();
        try (PreparedStatement st = con.prepareStatement(ALL_STUDENTS)) {
            ResultSet resultSet = st.executeQuery();
            while (resultSet.next()) {
                result.add(
                        new Student(
                                resultSet.getLong(Student.STUDENT_ID),
                                resultSet.getString(Student.FIRST_NAME),
                                resultSet.getString(Student.LAST_NAME),
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
                                resultSet.getString(Student.FIRST_NAME),
                                resultSet.getString(Student.LAST_NAME),
                                resultSet.getLong(GROUP_ID)
                        )
                );
            }
        }
        return result;
    }
}
