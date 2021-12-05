package ua.com.foxminded.sql.jdbc.school.dao.impl;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import ua.com.foxminded.sql.jdbc.school.PostgresDatabaseContainer;
import ua.com.foxminded.sql.jdbc.school.SchoolApp;
import ua.com.foxminded.sql.jdbc.school.dao.*;
import ua.com.foxminded.sql.jdbc.school.utils.SqlUtils;

import java.sql.SQLException;

class DaoTest {
    static final CourseDao courseDao = new CourseDaoImpl();
    static final GroupDao groupDao = new GroupDaoImpl();
    static final StudentDao studentDao = new StudentDaoImpl();
    static final StudentAssignmentDao studentAssignmentDao = new StudentAssignmentDaoImpl();

    static final String TEST_COURSE_NAME = "Java";
    static final String TEST_COURSE_DESCRIPTION = "Java course";
    static final String GROUP_NAME = "Some group";
    static final String STUDENT_FIRST_NAME = "John";
    static final String STUDENT_LAST_NAME = "Doe";

    static final String NEW_GROUP_NAME = "Another group";
    static final String NEW_COURSE_NAME = "Rust";
    static final String NEW_COURSE_DESCRIPTION = "Rust course";

    private static final String TESTING_DATA = "sql/generate_testing_data.sql";

    static Datasource datasource;

    @BeforeAll
    static void setUp() {
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
        SqlUtils.executeSqlScriptFile(datasource, TESTING_DATA);
    }

}