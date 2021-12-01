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

public class StudentAssignmentDaoImpl implements StudentAssignmentDao {
    private static final String CREATE_STUDENT_ASSIGNMENT = "INSERT INTO " + StudentAssignment.TABLE_NAME + " (" +
            STUDENT_ID + ", " + COURSE_ID + ") " + "VALUES (?, ?);";

    private static final String ALL_STUDENT_ASSIGNMENTS = "SELECT * FROM " + StudentAssignment.TABLE_NAME + ";";

    private static final String FIND_STUDENT_ASSIGNMENT = "SELECT * FROM " + StudentAssignment.TABLE_NAME
            + " WHERE " + STUDENT_ID + " = ? AND " + COURSE_ID + " = ?;";

    private static final String DELETE_STUDENT_ASSIGNMENT = "DELETE FROM " + StudentAssignment.TABLE_NAME
            + " WHERE " + STUDENT_ID + " = ? AND " + COURSE_ID + " = ?;";

    @Override
    public StudentAssignment create(Connection connection, StudentAssignment entity) throws SQLException {
        SqlUtils.executeUpdate(connection, CREATE_STUDENT_ASSIGNMENT, entity.getStudentId(), entity.getCourseId());
        return entity;
    }

    @Override
    public void deleteByIds(Connection connection, Long studentId, Long courseId) throws SQLException {
        SqlUtils.executeUpdate(connection, DELETE_STUDENT_ASSIGNMENT, studentId, courseId);
    }

    /**
     * Here we need to parse {@link ResultSet} at place. We still could use {@link SqlUtils} method by providing
     * it with {@link ResultSet} mapper but in this case we would be providing parameters as Objects
     * (to generalize the method) and thus lose type validation. All in all it is more readable as it is here
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
