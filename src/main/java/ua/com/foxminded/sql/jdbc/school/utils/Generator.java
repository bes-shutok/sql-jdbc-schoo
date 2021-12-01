package ua.com.foxminded.sql.jdbc.school.utils;

import ua.com.foxminded.sql.jdbc.school.dao.CourseDao;
import ua.com.foxminded.sql.jdbc.school.dao.GroupDao;
import ua.com.foxminded.sql.jdbc.school.dao.impl.StudentAssignmentDaoImpl;
import ua.com.foxminded.sql.jdbc.school.dao.StudentDao;
import ua.com.foxminded.sql.jdbc.school.model.Course;
import ua.com.foxminded.sql.jdbc.school.model.Group;
import ua.com.foxminded.sql.jdbc.school.model.Student;
import ua.com.foxminded.sql.jdbc.school.model.StudentAssignment;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Generator {
    private static final char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final String[] LAST_NAMES = {
            "Farmer",
            "Mccormick",
            "Nelson",
            "Peterson",
            "Jordan",
            "Briggs",
            "Kent",
            "Combs",
            "Vaughn",
            "Dyer",
            "Nguyen",
            "Shea",
            "Benjamin",
            "Case",
            "Zimmerman",
            "Sheppard",
            "Rush",
            "Berry",
            "Bowers",
            "Martinez"
    };
    private static final String[] FIRST_NAMES = {
            "Bailey",
            "Lilian",
            "Angela",
            "Violet",
            "Ruby",
            "Barley",
            "Peyton",
            "Esther",
            "Madison",
            "Neva",
            "Jonny",
            "Seamus",
            "Jabir",
            "Jerome",
            "Lawson",
            "Romeo",
            "Dallas",
            "Eli",
            "Dante",
            "Damion"
    };

    private static final Map<String, String> COURSES = Map.of(
            "Linear Algebra", "Linear Algebra course",
            "Differential Equations", "Differential Equations course",
            "Discrete Mathematics", "Discrete Mathematics course",
            "Calculus", "Calculus course",
            "Theory of Computation", "Theory of Computation course",
            "Biology", "Biology course",
            "Chemistry", "Chemistry course",
            "Physics", "Physics course",
            "Philosophy", "Philosophy course",
            "English","English course"
    );

    private final GroupDao groupDao;
    private final StudentDao studentDao;
    private final CourseDao courseDao;
    private final StudentAssignmentDaoImpl studentAssignmentDao;

    public Generator(
            GroupDao groupDao, StudentDao studentDao, CourseDao courseDao, StudentAssignmentDaoImpl studentAssignmentDao
    ) {
        this.groupDao = groupDao;
        this.studentDao = studentDao;
        this.courseDao = courseDao;
        this.studentAssignmentDao = studentAssignmentDao;
    }

    public void generateData(Connection connection, int groupCount, int studentCount) throws SQLException {
        List<Group> groups = generateGroups(connection, groupCount);
        if (groups.isEmpty()) {
            throw new AssertionError("Generated groups list cannot be empty");
        }
        List<Course> courses = generateCourses(connection);
        if (courses.isEmpty()) {
            throw new AssertionError("Generated courses list cannot be empty");
        }
        List<Student> students = generateStudents(connection, studentCount, groups);
        if (students.isEmpty()) {
            throw new AssertionError("Generated students list cannot be empty");
        }
    }

    public void assignStudents(Connection connection) throws SQLException {
        List<Student> students = assignStudentsToGroups(connection);
        List<Course> courses = courseDao.findAll(connection);
        List<StudentAssignment> assignments = assignStudentsToCourses(connection, students, courses);
        if (assignments.isEmpty()) {
            throw new AssertionError("Generated assignStudents list cannot be empty");
        }
    }

    /**
     * Randomly assign student to groups. Each group could contain from 10 to 30 student.
     * It is possible that some groups will be without student or student without groups
     */
    private List<Student> assignStudentsToGroups(Connection connection) throws SQLException {
        List<Student> students = studentDao.findAll(connection);
        List<Group> groups = groupDao.findAll(connection);
        int maxGroupNo = groups.size() - 1;
        int groupNo = 0;
        int studentsPerGroup = getRandomCount(10, 30);
        Long groupId = groups.get(groupNo).getId();
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            student.setGroupId(groupId);
            if (studentsPerGroup == 0) {
                studentsPerGroup = getRandomCount(10, 30);
                if (groupNo < maxGroupNo && (students.size() - i) > studentsPerGroup) {
                    groupId = groups.get(++groupNo).getId();
                } else {
                    break;
                }
            }
            studentDao.save(connection, student);
            studentsPerGroup--;
        }
        return students;
    }

    private List<StudentAssignment> assignStudentsToCourses(Connection connection, List<Student> students, List<Course> courses) throws SQLException {
        List<StudentAssignment> result = new ArrayList<>();
        for (Student student : students) {
            int coursesCount = getRandomCount(1, 3);
            List<Course> appliedCourses = getRandomCourses(courses, coursesCount);
            for (Course course : appliedCourses) {
                result.add(
                        studentAssignmentDao.create(connection, new StudentAssignment(student.getId(), course.getId()))
                );
            }
        }
        return result;
    }

    private List<Course> getRandomCourses(List<Course> courses, int coursesCount) {
        List<Course> result = new ArrayList<>();
        for (int i = 0; i < coursesCount; i++) {
            Course course;
            do {
                course = courses.get(getRandomCount(0, courses.size() - 1));
            } while (result.contains(course));
            result.add(course);
        }
        return result;
    }

    private List<Course> generateCourses(Connection connection) throws SQLException {
        List<Course> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : COURSES.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            result.add(courseDao.save(connection, new Course(k, v)));
        }
        return result;
    }

    private List<Group> generateGroups(Connection connection, long count) throws SQLException {
        List<Group> result = new ArrayList<>();

        for (long i = 0; i < count; i++) {
            String groupName = generateGroupName();
            result.add(groupDao.save(connection, new Group(groupName)));
        }

        return result;
    }

    private String generateGroupName() {
        return String.valueOf(randomChar(alphabet)) + randomChar(alphabet) + "-" + getRandomCount(10, 99);
    }

    private List<Student> generateStudents(Connection connection, int count, List<Group> groups) throws SQLException {
        List<Student> result = new ArrayList<>();
        int maxGroupNo = groups.size() - 1;

        int groupNo = 0;
        int studentsPerGroup = getRandomCount(10, 30);
        Long groupId = groups.get(groupNo).getId();
        for (int i = 0; i < count; i++) {
            String firstName = generateFirstName();
            String lastName = generateLastName();
            if (studentsPerGroup == 0) {
                studentsPerGroup = getRandomCount(10, 30);
                if (groupNo < maxGroupNo && (count - i) > studentsPerGroup) {
                    groupId = groups.get(++groupNo).getId();
                } else {
                    groupId = null;
                }
            }
            result.add(studentDao.save(connection, new Student(firstName, lastName, groupId)));
            studentsPerGroup--;
        }
        return result;
    }

    private int getRandomCount(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
    private String generateLastName() {
        return randomString(LAST_NAMES);
    }

    private String generateFirstName() {
        return randomString(FIRST_NAMES);
    }

    private char randomChar(char[] array) {
        return array[ThreadLocalRandom.current().nextInt(array.length)];
    }
    private String randomString(String[] array) {
        return array[ThreadLocalRandom.current().nextInt(array.length)];
    }
}
