package ua.com.foxminded.sql.jdbc.school.dao;

import ua.com.foxminded.sql.jdbc.school.model.StudentAssignment;
import ua.com.foxminded.sql.jdbc.school.utils.SqlUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ua.com.foxminded.sql.jdbc.school.model.Course.COURSE_ID;
import static ua.com.foxminded.sql.jdbc.school.model.Student.STUDENT_ID;
import static ua.com.foxminded.sql.jdbc.school.model.StudentAssignment.STUDENT_ASSIGNMENT_TABLE_NAME;

public class StudentAssignmentImpl implements StudentAssignmentDao {
    @Override
    public StudentAssignment save(Connection connection, StudentAssignment entity) throws SQLException {
        String script = String.format(
                "INSERT INTO %s(%s,%s) VALUES (%d,%d);",
                STUDENT_ASSIGNMENT_TABLE_NAME,
                STUDENT_ID, COURSE_ID,
                entity.getStudentId(), entity.getCourseId()
        );
        SqlUtils.executeSqlScript(connection, script);
        return entity;
    }

    @Override
    public void deleteByIds(Connection connection, Long studentId, Long courseId) throws SQLException {
        String script = String.format(
                "DELETE FROM %s WHERE %s = %d AND %s = %d;",
                STUDENT_ASSIGNMENT_TABLE_NAME,
                STUDENT_ID, studentId,
                COURSE_ID, courseId
        );
        SqlUtils.executeSqlScript(connection, script);
    }

    @Override
    public Optional<StudentAssignment> findByIds(Connection connection, Long studentId, Long courseId) throws SQLException {
        String script = String.format(
                "SELECT * FROM %s WHERE %s = %d AND %s = %d;",
                STUDENT_ASSIGNMENT_TABLE_NAME,
                STUDENT_ID, studentId,
                COURSE_ID, courseId
        );
        Optional<StudentAssignment> result = Optional.empty();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(script);
            if (resultSet.next()) {
                result = Optional.of(new StudentAssignment(studentId, courseId));
            }
        }
        return result;
    }

    @Override
    public List<StudentAssignment> findAll(Connection connection) throws SQLException {
        String script = String.format("SELECT * FROM %s;", STUDENT_ASSIGNMENT_TABLE_NAME);
        List<StudentAssignment> result = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(script);
            while (resultSet.next()) {
                result.add(new StudentAssignment(resultSet.getLong(STUDENT_ID), resultSet.getLong(COURSE_ID)));
            }
        }
        return result;
    }

}
