package ua.com.foxminded.sql.jdbc.school;

import ua.com.foxminded.sql.jdbc.school.dao.*;
import ua.com.foxminded.sql.jdbc.school.dao.impl.CourseDaoImpl;
import ua.com.foxminded.sql.jdbc.school.dao.impl.GroupDaoImpl;
import ua.com.foxminded.sql.jdbc.school.dao.impl.StudentAssignmentDaoImpl;
import ua.com.foxminded.sql.jdbc.school.dao.impl.StudentDaoImpl;
import ua.com.foxminded.sql.jdbc.school.model.Group;
import ua.com.foxminded.sql.jdbc.school.model.Student;
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

public class SchoolApp implements Closeable {

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
        SqlUtils.executeSqlScriptFile(datasource, "sql/init_schema.sql");

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
            SqlUtils.executeSqlScriptFile(datasource, "sql/drop_schema.sql");
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
/*
            // c. Add new student
            case "c":
                addNewStudent(reader);
                break;

            // d. Delete student by STUDENT_ID
            case "d":
                deleteStudentByStudentId(reader);
                break;

            // e. Add a student to the course (from a list)
            case "e":
                addAStudentToTheCourseFromAList(reader);
                break;

            // f. Remove the student from one of his or her courses
            case "f":
                removeStudentFromCourse(reader);
                break;*/
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
        List<Student> students = userHandler.findAllStudentsForGroup(datasource, courseName);
        System.out.println("Groups with less or equals student count:\n" + students);
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

