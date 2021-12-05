package ua.com.foxminded.sql.jdbc.school;

import ua.com.foxminded.sql.jdbc.school.dao.*;
import ua.com.foxminded.sql.jdbc.school.dao.impl.CourseDaoImpl;
import ua.com.foxminded.sql.jdbc.school.dao.impl.GroupDaoImpl;
import ua.com.foxminded.sql.jdbc.school.dao.impl.StudentAssignmentDaoImpl;
import ua.com.foxminded.sql.jdbc.school.dao.impl.StudentDaoImpl;
import ua.com.foxminded.sql.jdbc.school.model.Course;
import ua.com.foxminded.sql.jdbc.school.model.Group;
import ua.com.foxminded.sql.jdbc.school.model.Student;
import ua.com.foxminded.sql.jdbc.school.model.StudentAssignment;
import ua.com.foxminded.sql.jdbc.school.utils.Generator;
import ua.com.foxminded.sql.jdbc.school.utils.SqlUtils;
import ua.com.foxminded.sql.jdbc.school.utils.UserHandler;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import static ua.com.foxminded.sql.jdbc.school.utils.ResourceUtils.loadPropertiesFromResources;
import static ua.com.foxminded.sql.jdbc.school.utils.TransactionUtils.transaction;

@SuppressWarnings("RedundantStringFormatCall")
public class SchoolApp implements Closeable {

    public static final String DROP_SCHEMA = "sql/drop_schema.sql";
    public static final String INIT_SCHEMA = "sql/init_schema.sql";

    private final static String MENU = """
            a. Find all groups with less or equals student count
            b. Find all students related to course with given name
            c. Add new student
            d. Delete student by STUDENT_ID
            e. Add a student to the course (from a list)
            f. Remove the student from one of his or her courses""";

    private final Datasource datasource;
    private final GroupDao groupDao;
    private final StudentDao studentDao;
    private final CourseDao courseDao;
    private final StudentAssignmentDaoImpl studentAssignmentDao;

    private final Generator generator;
    private final UserHandler userHandler;

    public SchoolApp(Datasource datasource) throws SQLException {
        this.datasource = datasource;

        // setup database
        SqlUtils.executeSqlScriptFile(datasource, INIT_SCHEMA);

        // setup dao components
        this.groupDao = new GroupDaoImpl();
        this.studentDao = new StudentDaoImpl();
        this.courseDao = new CourseDaoImpl();
        this.studentAssignmentDao = new StudentAssignmentDaoImpl();
        this.generator = new Generator(groupDao, studentDao, courseDao, studentAssignmentDao);
        this.userHandler = new UserHandler(groupDao, studentDao, courseDao, studentAssignmentDao);
    }

    @Override
    public void close() throws IOException {
        try {
            SqlUtils.executeSqlScriptFile(datasource, DROP_SCHEMA);
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    public static void main(String[] args) throws IOException, SQLException {
        Properties databaseProperties = loadPropertiesFromResources("db.properties");
        try (
                Datasource datasource = new SimpleDatasource(databaseProperties);
                SchoolApp schoolApp = new SchoolApp(datasource)
        ) {
            schoolApp.run();
        }
    }

    private void run() throws SQLException {
        // fill db with generated data
        transaction(datasource, (connection -> generator.generateData(connection, 10, 200)));
        transaction(datasource, (generator::assignStudents));
        System.out.println("Test data generated");

        // show menu in a loop
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
/*        String YES = "Yes";
        String NO = "No";*/
        List<String> expectedAnswers = List.of("a", "b", "c", "d", "e", "f");

//todo:
        System.out.println("Chose from available actions menu:\n" + MENU);
        switch (getUserAnswer(reader, expectedAnswers)) {
            // a. Find all groups with less or equals student count
            case "a" -> findAllGroupsWithLessOrEqualStudentCount(datasource, userHandler);

            // b. Find all students related to course with given name
            case "b" ->  findAllStudentsRelatedToCourseWithGivenName(datasource, userHandler);
            // c. Add new student
            case "c" ->  addNewStudent(datasource, userHandler);

            // d. Delete student by STUDENT_ID
            case "d" ->  deleteStudentByStudentId(datasource, userHandler);

            // e. Add a student to the course (from a list)
            case "e" ->  addAStudentToTheCourseFromAList(datasource, userHandler);

            // f. Remove the student from one of his or her courses
            case "f" ->  removeStudentFromCourse(datasource, userHandler);

            default -> throw new IllegalStateException("Unexpected value: " + getUserAnswer(reader, expectedAnswers));
        }
    }

    /**
     * Find all groups with less or equals student count
     */
    private static void findAllGroupsWithLessOrEqualStudentCount(Datasource datasource, UserHandler userHandler)
            throws SQLException {
        Scanner myInput = new Scanner(System.in);
        System.out.print("Find all groups with less or equals student count.\nEnter student count: ");
        int maxNumberOfStudents = myInput.nextInt();
        List<Group> groups = userHandler.findAllGroupsByStudentCount(datasource, maxNumberOfStudents);
        System.out.println("Groups with less or equals student count:\n" + groups);
    }

    /**
     * Find all student related to course with given name
     */
    private void findAllStudentsRelatedToCourseWithGivenName(Datasource datasource, UserHandler userHandler)
            throws SQLException {
        Scanner myInput = new Scanner(System.in);
        System.out.print("Find all student related to course with given name.\nEnter course name: ");
        String courseName = myInput.next();
        List<Student> students = userHandler.findAllStudentsRelatedToCourse(datasource, courseName);
        System.out.println("Groups with less or equals student count:\n" + students);
    }

    /**
     * Add new student
     */
    private void addNewStudent(Datasource datasource, UserHandler userHandler)
            throws SQLException {
        Scanner myInput = new Scanner(System.in);
        System.out.print("Add new student.\nEnter firstName of the student: ");
        String firstName = myInput.next();
        System.out.print("Add new student.\nEnter lastName of the student: ");
        String lastName = myInput.next();
        Student student = userHandler.addNewStudent(datasource, new Student(firstName, lastName));
        System.out.println("The student added: " + student);
    }

    /**
     * Delete student by STUDENT_ID
     */
    private void deleteStudentByStudentId(Datasource datasource, UserHandler userHandler)
            throws SQLException {
        Scanner myInput = new Scanner(System.in);
        List<Student> students = userHandler.allStudents();
        System.out.print(String.format("Delete student by STUDENT_ID%nList of active students:%n%s", students));
        System.out.print(String.format("%nEnter STUDENT_ID of the student to be deleted:%n"));
        Long studentId = myInput.nextLong();
        Student student = userHandler.deleteStudent(datasource, studentId);
        System.out.println(String.format("Student [%s] has been deleted from the list of active students", student));
    }

    /**
     * Add a student to the course (from a list)
     */
    private void addAStudentToTheCourseFromAList(Datasource datasource, UserHandler userHandler)
            throws SQLException {
        Scanner myInput = new Scanner(System.in);
        List<Student> students = userHandler.allStudents();
        List<Course> courses = userHandler.allCourses();
        System.out.print(String.format("Add a student to the course%nHere is the list of active students:%n%s", students));
        System.out.print(String.format("%nEnter STUDENT_ID of the student to be updated:%n"));
        Long studentId = myInput.nextLong();
        System.out.print(String.format("Add a student to the course%nHere is the list of courses:%n%s", courses));
        System.out.print(String.format("%nEnter COURSE_ID of the course which should be assigned " +
                "to the chosen student%n"));
        Long courseId = myInput.nextLong();
        students = userHandler.addStudentToTheCourse(datasource, studentId, courseId);
        System.out.println(String.format("Here is the list students assigned to the course:%n%s", students));
    }

    /**
     * Remove the student from one of his or her courses
     */
    private void removeStudentFromCourse(Datasource datasource, UserHandler userHandler)
            throws SQLException {
        Scanner myInput = new Scanner(System.in);
        List<Student> students = userHandler.allStudents();
        System.out.print(String.format("Remove the student from one of his or her courses%n" +
                "Here is the list of active students:%n%s", students));
        System.out.print(String.format("%nEnter STUDENT_ID of the student to be updated:%n"));
        Long studentId = myInput.nextLong();
        List<Course> courses = userHandler.courses(studentId);
        System.out.print(String.format("Remove the student from one of his or her courses%n" +
                "Here is the list of active courses for the student:%n%s", courses));
        System.out.print(String.format("%nEnter COURSE_ID of the course from which the student should be removed%n"));
        Long courseId = myInput.nextLong();
        List<StudentAssignment> studentAssignments = userHandler.removeStudentFromCourse(datasource, studentId, courseId);
        System.out.println(String.format("Here is the list studentAssignments for the student:%n%s", studentAssignments));
    }

    private static String getUserAnswer(BufferedReader reader, List<String> expectedAnswers) {
        String answer = "";
        while (!expectedAnswers.contains(answer)) {
            System.out.println("Please provide one of the expected answers: " + expectedAnswers);
            answer = readUserInput(reader);
        }
        return answer;
    }

    private static String readUserInput(BufferedReader reader) {
        String input = "";
        // Reading data using readLine
        try {
            input = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return input;
    }
}

