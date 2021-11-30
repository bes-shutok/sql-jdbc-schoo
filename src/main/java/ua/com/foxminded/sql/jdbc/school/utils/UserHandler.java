package ua.com.foxminded.sql.jdbc.school.utils;

import ua.com.foxminded.sql.jdbc.school.dao.CourseDao;
import ua.com.foxminded.sql.jdbc.school.dao.Datasource;
import ua.com.foxminded.sql.jdbc.school.dao.GroupDao;
import ua.com.foxminded.sql.jdbc.school.dao.StudentDao;
import ua.com.foxminded.sql.jdbc.school.dao.impl.StudentAssignmentImpl;
import ua.com.foxminded.sql.jdbc.school.model.Course;
import ua.com.foxminded.sql.jdbc.school.model.Group;
import ua.com.foxminded.sql.jdbc.school.model.Student;
import ua.com.foxminded.sql.jdbc.school.model.StudentAssignment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserHandler {

    private static final String GROUPS_BY_STUDENT_COUNT = "SELECT " + Group.GROUP_ID + " FROM " + Student.TABLE_NAME
            + " WHERE " + Group.GROUP_ID + " IS NOT NULL GROUP BY " + Group.GROUP_ID + " HAVING COUNT(*) <= ? ;";

    private static final String STUDENTS_FOR_COURSE = "SELECT " + Student.STUDENT_ID + " FROM "
            + StudentAssignment.TABLE_NAME + " sa JOIN " + Course.TABLE_NAME + " c ON sa." + Course.COURSE_ID +
            " = c." + Course.COURSE_ID + " WHERE c." + Course.COURSE_NAME + " = ?;";

    private final GroupDao groupDao;
    private final StudentDao studentDao;
    private final CourseDao courseDao;
    private final StudentAssignmentImpl studentAssignmentDao;

    public UserHandler(
            GroupDao groupDao, StudentDao studentDao, CourseDao courseDao, StudentAssignmentImpl studentAssignmentDao
    ) {
        this.groupDao = groupDao;
        this.studentDao = studentDao;
        this.courseDao = courseDao;
        this.studentAssignmentDao = studentAssignmentDao;
    }

    public List<Group> findAllGroupsByStudentCount(Datasource datasource, int maxNumberOfStudents) throws SQLException {
        List<Group> result = new ArrayList<>();
        try (
                Connection connection = datasource.getConnection();
                PreparedStatement st = connection.prepareStatement(GROUPS_BY_STUDENT_COUNT)
        ) {
            st.setLong(1, maxNumberOfStudents);
            ResultSet resultSet = st.executeQuery();
            while (resultSet.next()) {
                Optional<Group> optionalGroup = groupDao.findById(connection, resultSet.getLong(Group.GROUP_ID));
                if (optionalGroup.isPresent()) {
                    result.add(optionalGroup.get());
                } else {
                    throw new SQLException("Group not found");
                }
            }
        }
        return result;
    }

    public List<Student> findAllStudentsForGroup(Datasource datasource, String courseName) throws SQLException {
        List<Student> result = new ArrayList<>();
        try (
                Connection connection = datasource.getConnection();
                PreparedStatement st = connection.prepareStatement(STUDENTS_FOR_COURSE)
        ) {
            st.setString(1, courseName);
            ResultSet resultSet = st.executeQuery();
            while (resultSet.next()) {
                Optional<Student> optionalStudent = studentDao.findById(connection, resultSet.getLong(Student.STUDENT_ID));
                if (optionalStudent.isPresent()) {
                    result.add(optionalStudent.get());
                } else {
                    throw new SQLException("Group not found");
                }
            }
        }
        return result;
    }
}
