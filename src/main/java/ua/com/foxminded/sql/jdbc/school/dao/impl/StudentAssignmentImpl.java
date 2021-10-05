package ua.com.foxminded.sql.jdbc.school.dao.impl;

import ua.com.foxminded.sql.jdbc.school.dao.StudentAssignmentDao;
import ua.com.foxminded.sql.jdbc.school.model.StudentAssignment;
import ua.com.foxminded.sql.jdbc.school.utils.SqlUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ua.com.foxminded.sql.jdbc.school.model.Course.*;
import static ua.com.foxminded.sql.jdbc.school.model.Student.STUDENT_ID;
import static ua.com.foxminded.sql.jdbc.school.model.StudentAssignment.STUDENT_ASSIGNMENT_TABLE_NAME;

public class StudentAssignmentImpl implements StudentAssignmentDao {
    private static final String CREATE_STUDENT_ASSIGNMENT = "INSERT INTO " + STUDENT_ASSIGNMENT_TABLE_NAME + " (" +
            STUDENT_ID + ", " + COURSE_ID + ") " + "VALUES (?, ?);";
    private static final String ALL_STUDENT_ASSIGNMENTS = "SELECT * FROM " + STUDENT_ASSIGNMENT_TABLE_NAME + ";";
    private static final String FIND_STUDENT_ASSIGNMENT = "SELECT * FROM " + STUDENT_ASSIGNMENT_TABLE_NAME
            + " WHERE " + STUDENT_ID + " = ? AND " + COURSE_ID + " = ?;";
    private static final String DELETE_STUDENT_ASSIGNMENT = "DELETE FROM " + STUDENT_ASSIGNMENT_TABLE_NAME
            + " WHERE " + STUDENT_ID + " = ? AND " + COURSE_ID + " = ?;";

    @Override
    public StudentAssignment save(Connection connection, StudentAssignment entity) throws SQLException {
        SqlUtils.executeUpdate(connection, CREATE_STUDENT_ASSIGNMENT, entity.getStudentId(), entity.getCourseId());
        return entity;
    }

    @Override
    public void deleteByIds(Connection connection, Long studentId, Long courseId) throws SQLException {
        SqlUtils.executeUpdate(connection, DELETE_STUDENT_ASSIGNMENT, studentId, courseId);
    }

    /**
     * We cannot use {@link SqlUtils} methods here as {@link ResultSet} have to be parsed at place - it is closed
     * with the statement closing. And we cannot delegate its parsing to {@link SqlUtils} since it would require
     * passing business logic to the util methods, and we don't wont to do it.
     */
    @Override
    public List<StudentAssignment> findAll(Connection con) throws SQLException {
        List<StudentAssignment> result = new ArrayList<>();
        try (PreparedStatement st = con.prepareStatement(ALL_STUDENT_ASSIGNMENTS)) {
            ResultSet resultSet = st.executeQuery();
            while (resultSet.next()) {
                result.add(new StudentAssignment(resultSet.getLong(STUDENT_ID), resultSet.getLong(COURSE_ID)));
            }
        }
        return result;
    }

    @Override
    public Optional<StudentAssignment> findByIds(Connection con, Long studentId, Long courseId) throws SQLException {
        Optional<StudentAssignment> result = Optional.empty();
        try (PreparedStatement st = con.prepareStatement(FIND_STUDENT_ASSIGNMENT)) {
            st.setLong(1, studentId);
            st.setLong(2, courseId);
            ResultSet resultSet = st.executeQuery();
            if (resultSet.next()) {
                result = Optional.of(new StudentAssignment(studentId, courseId));
            }
        }
        return result;
    }

}
