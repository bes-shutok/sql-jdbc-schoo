package ua.com.foxminded.sql.jdbc.school.dao.impl;

import ua.com.foxminded.sql.jdbc.school.dao.StudentAssignmentDao;
import ua.com.foxminded.sql.jdbc.school.model.StudentAssignment;
import ua.com.foxminded.sql.jdbc.school.utils.SqlUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ua.com.foxminded.sql.jdbc.school.model.Course.COURSE_ID;
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
    public void create(Connection con, StudentAssignment entity) throws SQLException {
        SqlUtils.executeDmlQuery(
                con,
                CREATE_STUDENT_ASSIGNMENT,
                new Object[] {entity.getStudentId(), entity.getCourseId()}
        );
    }

    @Override
    public void deleteByIds(Connection con, Long studentId, Long courseId) throws SQLException {
        SqlUtils.executeDmlQuery(con, DELETE_STUDENT_ASSIGNMENT, new Object[] {studentId, courseId});
    }

    /**
     * Here we need to parse {@link ResultSet} at place. We still could use {@link SqlUtils} method by providing
     * it with {@link ResultSet} mapper but in this case we would be providing parameters as Objects
     * (to generalize the method) and thus lose type validation. All in all it is more readable as it is here
     */
    @Override
    public List<StudentAssignment> findAll(Connection con) throws SQLException {
        return SqlUtils.executeQuery(con, ALL_STUDENT_ASSIGNMENTS, new Object[0], this::getStudentAssignments);
    }

    @Override
    public Optional<StudentAssignment> findByIds(Connection con, Long studentId, Long courseId) throws SQLException {
        return SqlUtils.executeQuery(
                con,
                FIND_STUDENT_ASSIGNMENT,
                new Object[] {studentId, courseId},
                resultSet -> SqlUtils.getOptionalResult(resultSet, this::getStudentAssignment)
        );
    }

    private List<StudentAssignment> getStudentAssignments(ResultSet resultSet) throws SQLException {
        List<StudentAssignment> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(getStudentAssignment(resultSet));
        }
        return list;
    }

    private StudentAssignment getStudentAssignment(ResultSet resultSet) throws SQLException {
        return new StudentAssignment(resultSet.getLong(STUDENT_ID), resultSet.getLong(COURSE_ID));
    }
}
