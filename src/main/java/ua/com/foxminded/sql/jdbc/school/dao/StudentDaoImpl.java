package ua.com.foxminded.sql.jdbc.school.dao;

import ua.com.foxminded.sql.jdbc.school.model.Student;
import ua.com.foxminded.sql.jdbc.school.utils.SqlUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ua.com.foxminded.sql.jdbc.school.model.Group.GROUP_ID;
import static ua.com.foxminded.sql.jdbc.school.model.Student.*;

public class StudentDaoImpl extends AbstractCrudDao<Student, Long> implements StudentDao {
    @Override
    protected Student create(Connection connection, Student entity) throws SQLException {
        String script = String.format(
                "INSERT INTO %s(%s,%s,%s) VALUES ('%s','%s',%d);",
                STUDENTS_TABLE_NAME,
                FIRST_NAME, LAST_NAME, GROUP_ID,
                entity.getFirstName(), entity.getLastName(), entity.getGroupId()
        );
        SqlUtils.executeSqlScript(connection, script);
        return entity;
    }

    @Override
    protected Student update(Connection connection, Student entity) throws SQLException {
        String script = String.format(
                "UPDATE %s SET %s = '%s', %s = '%s' WHERE %s = %d;",
                STUDENTS_TABLE_NAME, FIRST_NAME, entity.getFirstName(), LAST_NAME, entity.getLastName(),
                STUDENT_ID, entity.getId()
        );
        SqlUtils.executeSqlScript(connection, script);
        return entity;
    }

    @Override
    public Optional<Student> findById(Connection connection, Long id) throws SQLException {
        String script = String.format( "SELECT * FROM %s WHERE %s = %d;", STUDENTS_TABLE_NAME, STUDENT_ID, id);
        Optional<Student> result = Optional.empty();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(script);
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

    @Override
    public List<Student> findAll(Connection connection) throws SQLException {
        String script = String.format("SELECT * FROM %s;", STUDENTS_TABLE_NAME);
        List<Student> result = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(script);
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
    public void deleteById(Connection connection, Long id) throws SQLException {
        String script = String.format("DELETE FROM %s WHERE %s = %d;", STUDENTS_TABLE_NAME, STUDENT_ID, id);
        SqlUtils.executeSqlScript(connection, script);
    }
}
