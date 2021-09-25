package ua.com.foxminded.sql.jdbc.school.dao;

import ua.com.foxminded.sql.jdbc.school.model.Course;
import ua.com.foxminded.sql.jdbc.school.utils.SqlUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ua.com.foxminded.sql.jdbc.school.model.Course.*;

public class CourseDaoImpl extends AbstractCrudDao<Course, Long> implements CourseDao {

    @Override
    protected Course create(Connection connection, Course entity) throws SQLException {
        String script = String.format(
                "INSERT INTO %s(%s,%s) VALUES ('%s','%s');",
                COURSES_TABLE_NAME, COURSE_NAME, COURSE_DESCRIPTION, entity.getName(), entity.getDescription()
        );
        SqlUtils.executeSqlScript(connection, script);
        return entity;
    }

    @Override
    protected Course update(Connection connection, Course entity) throws SQLException {
        String script = String.format(
                "UPDATE %s SET %s = '%s', %s = '%s' WHERE %s = %d;",
                COURSES_TABLE_NAME, COURSE_NAME, entity.getName(), COURSE_DESCRIPTION, entity.getDescription(),
                COURSE_ID, entity.getId()
        );
        SqlUtils.executeSqlScript(connection, script);
        return entity;
    }

    @Override
    public Optional<Course> findById(Connection connection, Long id) throws SQLException {
        String script = String.format( "SELECT * FROM %s WHERE %s = %d;", COURSES_TABLE_NAME, COURSE_ID, id);
        Optional<Course> result = Optional.empty();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(script);
            if (resultSet.next()) {
                result = Optional.of(
                        new Course(
                                id,
                                resultSet.getString(COURSE_NAME),
                                resultSet.getString(COURSE_DESCRIPTION)
                        )
                );
            }
        }
        return result;
    }

    @Override
    public List<Course> findAll(Connection connection) throws SQLException {
        String script = String.format("SELECT * FROM %s;", COURSES_TABLE_NAME);
        List<Course> result = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(script);
            while (resultSet.next()) {
                result.add(
                        new Course(
                                resultSet.getLong(COURSE_ID),
                                resultSet.getString(COURSE_NAME),
                                resultSet.getString(COURSE_DESCRIPTION)
                        )
                );
            }
        }
        return result;
    }

    @Override
    public void deleteById(Connection connection, Long id) throws SQLException {
        String script = String.format("DELETE FROM %s WHERE %s = %d;", COURSES_TABLE_NAME, COURSE_ID, id);
        SqlUtils.executeSqlScript(connection, script);
    }
}
