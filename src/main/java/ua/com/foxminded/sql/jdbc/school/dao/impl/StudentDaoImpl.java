package ua.com.foxminded.sql.jdbc.school.dao.impl;

import ua.com.foxminded.sql.jdbc.school.dao.AbstractCrudDao;
import ua.com.foxminded.sql.jdbc.school.dao.StudentDao;
import ua.com.foxminded.sql.jdbc.school.model.Student;
import ua.com.foxminded.sql.jdbc.school.utils.SqlUtils;

import java.sql.Connection;
import java.sql.SQLException;
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
    protected void create(Connection con, Student entity) throws SQLException {
        SqlUtils.executeDmlQuery(
                con,
                CREATE_STUDENT,
                new Object[] {entity.getFirstName(), entity.getLastName(), entity.getGroupId()}
        );
    }

    @Override
    protected void update(Connection con, Student entity) throws SQLException {
        SqlUtils.executeDmlQuery(
                con,
                UPDATE_STUDENT,
                new Object[] {entity.getFirstName(), entity.getLastName(), entity.getGroupId(), entity.getId()}
        );
    }

    @Override
    public void deleteById(Connection con, Long id) throws SQLException {
        SqlUtils.executeDmlQuery(con, DELETE_STUDENT, new Object[] {id});
    }

    @Override
    public List<Student> findAll(Connection con) throws SQLException {
        return SqlUtils.executeQuery(con, ALL_STUDENTS, new Object[0], Student::getStudents);
    }

    @Override
    public Optional<Student> findById(Connection con, Long id) throws SQLException {
        return SqlUtils.executeQuery(
                con,
                FIND_STUDENT_BY_ID,
                new Object[] {id},
                resultSet -> SqlUtils.getOptionalResult(resultSet, Student::getStudent)
        );
    }

}
