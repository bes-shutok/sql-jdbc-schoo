package ua.com.foxminded.sql.jdbc.school.dao.impl;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import ua.com.foxminded.sql.jdbc.school.PostgresDatabaseContainer;
import ua.com.foxminded.sql.jdbc.school.SchoolApp;
import ua.com.foxminded.sql.jdbc.school.dao.*;
import ua.com.foxminded.sql.jdbc.school.model.Course;
import ua.com.foxminded.sql.jdbc.school.model.Group;
import ua.com.foxminded.sql.jdbc.school.model.Student;
import ua.com.foxminded.sql.jdbc.school.model.StudentAssignment;
import ua.com.foxminded.sql.jdbc.school.utils.SqlUtils;

import java.sql.Connection;
import java.sql.SQLException;

class DaoTest {
    static final CourseDao courseDao = new CourseDaoImpl();
    static final GroupDao groupDao = new GroupDaoImpl();
    static final StudentDao studentDao = new StudentDaoImpl();
    static final StudentAssignmentDao studentAssignmentDao = new StudentAssignmentDaoImpl();

    static final String TEST_COURSE_NAME = "Java";
    static final String TEST_COURSE_DESCRIPTION = "Java course";
    static final String NEW_COURSE_NAME = "Rust";
    static final String NEW_COURSE_DESCRIPTION = "Rust course";

    static final String GROUP_NAME = "Some group";
    static final String NEW_GROUP_NAME = "Another group";
    static final String STUDENT_FIRST_NAME = "John";
    static final String STUDENT_LAST_NAME = "Doe";

    static Datasource datasource;

    @BeforeAll
    static void setUp() throws ClassNotFoundException {
        datasource = PostgresDatabaseContainer.setupDB();
    }

    @AfterAll
    static void tearDown() throws SQLException {
        SqlUtils.executeSqlScriptFile(datasource, SchoolApp.DROP_SCHEMA);
    }

    @BeforeEach
    void setUpEach() throws SQLException {
        SqlUtils.executeSqlScriptFile(datasource, SchoolApp.DROP_SCHEMA);
        SqlUtils.executeSqlScriptFile(datasource, SchoolApp.INIT_SCHEMA);
        generateTestingData();
    }

    void generateTestingData() throws SQLException {
        try (Connection con = datasource.getConnection()){
            groupDao.save(con, new Group(GROUP_NAME));
            courseDao.save(con, new Course(TEST_COURSE_NAME, TEST_COURSE_DESCRIPTION));
            studentDao.save(con, new Student(STUDENT_FIRST_NAME, STUDENT_LAST_NAME));
            studentAssignmentDao.create(
                    con,
                    new StudentAssignment(
                            studentDao.findAll(con).get(0).getId(),
                            courseDao.findAll(con).get(0).getId()
                    )
            );
        }
    }

}