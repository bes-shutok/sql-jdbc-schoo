package ua.com.foxminded.sql.jdbc.school.utils;

import ua.com.foxminded.sql.jdbc.school.dao.CourseDao;
import ua.com.foxminded.sql.jdbc.school.dao.Datasource;
import ua.com.foxminded.sql.jdbc.school.dao.GroupDao;
import ua.com.foxminded.sql.jdbc.school.dao.StudentDao;
import ua.com.foxminded.sql.jdbc.school.dao.impl.StudentAssignmentDaoImpl;
import ua.com.foxminded.sql.jdbc.school.model.Course;
import ua.com.foxminded.sql.jdbc.school.model.Group;
import ua.com.foxminded.sql.jdbc.school.model.Student;
import ua.com.foxminded.sql.jdbc.school.model.StudentAssignment;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserHandler {

    private static final String GROUPS_BY_STUDENT_COUNT = "SELECT " + Group.GROUP_ID  + " FROM " + Student.TABLE_NAME
            + " WHERE " + Group.GROUP_ID + " IS NOT NULL GROUP BY " + Group.GROUP_ID + " HAVING COUNT(*) <= ? ;";

    private static final String STUDENTS_FOR_COURSE = "SELECT " + Student.STUDENT_ID + " FROM "
            + StudentAssignment.TABLE_NAME + " sa JOIN " + Course.TABLE_NAME + " c ON sa." + Course.COURSE_ID +
            " = c." + Course.COURSE_ID + " WHERE c." + Course.COURSE_NAME + " = ?;";

    private final GroupDao groupDao;
    private final StudentDao studentDao;
    private final CourseDao courseDao;
    private final StudentAssignmentDaoImpl studentAssignmentDao;

    public UserHandler(
            GroupDao groupDao, StudentDao studentDao, CourseDao courseDao, StudentAssignmentDaoImpl studentAssignmentDao
    ) {
        this.groupDao = groupDao;
        this.studentDao = studentDao;
        this.courseDao = courseDao;
        this.studentAssignmentDao = studentAssignmentDao;
    }

    public List<Group> findAllGroupsByStudentCount(Datasource datasource, int maxNumberOfStudents) throws SQLException {
        try (Connection con = datasource.getConnection()) {
            Set<Long> groupIds =
            SqlUtils.executeQuery(
                    con,
                    GROUPS_BY_STUDENT_COUNT,
                    new Object[] {maxNumberOfStudents},
                    Group::getGroupsById
            )
                    .stream()
                    .map(Group::getId)
                    .collect(Collectors.toSet());

            List<Group> groups = new ArrayList<>(groupIds.size());
            for (Long groupId : groupIds) {
                groups.add(groupDao.findById(con, groupId).orElseThrow());
            }
            return groups;
        }
    }

    public List<Student> findAllStudentsRelatedToCourse(Datasource datasource, String courseName) throws SQLException {
        try (Connection con = datasource.getConnection()) {
            Set<Long> studentIds =
                    SqlUtils.executeQuery(
                            con,
                            STUDENTS_FOR_COURSE,
                            new Object[]{courseName},
                            Student::getStudentsById
                    )
                    .stream()
                    .map(Student::getId)
                    .collect(Collectors.toSet());

            List<Student> students = new ArrayList<>(studentIds.size());
            for (Long studentId : studentIds) {
                students.add(studentDao.findById(con, studentId).orElseThrow());
            }
            return students;
        }
    }

    public Student addNewStudent(Datasource datasource, Student student) throws SQLException {
        return null;
    }

    public List<Student> allStudents() throws SQLException {
        return null;
    }

    public Student deleteStudent(Datasource datasource, Long studentId) throws SQLException {
        return null;
    }

    public List<Course> allCourses() throws SQLException {
        return null;
    }

    public List<Student> addStudentToTheCourse(
            Datasource datasource,
            Long studentId,
            Long courseId
    ) throws SQLException {
        return null;
    }

    public List<Course> courses(Long studentId) throws SQLException {
        return null;
    }

    public List<StudentAssignment> removeStudentFromCourse(
            Datasource datasource,
            Long studentId,
            Long courseId
    ) throws SQLException {
        return null;
    }
}
