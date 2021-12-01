package ua.com.foxminded.sql.jdbc.school.dao.impl;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ua.com.foxminded.sql.jdbc.school.dao.*;
import ua.com.foxminded.sql.jdbc.school.model.Course;
import ua.com.foxminded.sql.jdbc.school.model.Group;
import ua.com.foxminded.sql.jdbc.school.utils.SqlUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ua.com.foxminded.sql.jdbc.school.utils.ResourceUtils.loadPropertiesFromResources;

@SuppressWarnings("OptionalGetWithoutIsPresent")
class DaoTest {
    private static final CourseDao courseDao = new CourseDaoImpl();
    private static final GroupDao groupDao = new GroupDaoImpl();
    private static final StudentDao studentDao = new StudentDaoImpl();
    private static final StudentAssignmentDao studentAssignmentDao = new StudentAssignmentDaoImpl();

    private static final String TEST_COURSE_NAME = "Java";
    private static final String TEST_COURSE_DESCRIPTION = "Java course";
    private static final String NEW_COURSE_NAME = "Rust";
    private static final String NEW_COURSE_DESCRIPTION = "Rust course";

    private static final String GROUP_NAME = "Some group";
    private static final String NEW_GROUP_NAME = "Another group";

    private static Datasource datasource;
    private static Connection connection;

    @BeforeAll
    static void setUp() throws IOException, SQLException {
        Properties databaseProperties = loadPropertiesFromResources("db.properties");
        datasource = new SimpleDatasource(databaseProperties);
        SqlUtils.executeSqlScriptFile(datasource, "sql/init_schema.sql");
        connection = datasource.getConnection();
    }

    @AfterAll
    static void tearDown() throws IOException, SQLException {
        connection.close();
        datasource.close();
        SqlUtils.executeSqlScriptFile(datasource, "sql/drop_schema.sql");
    }

    @Test
    void shouldCreateUpdateAndDeleteCourse() throws SQLException {
        assertEquals(courseDao.findAll(connection).size(), 0);
        Course course = new Course(TEST_COURSE_NAME, TEST_COURSE_DESCRIPTION);
        courseDao.save(connection, course);
        List<Course> courses = courseDao.findAll(connection);
        assertEquals(courses.size(),1);
        Course courseFromDb = courses.get(0);
        assertEquals(courseFromDb.getName(), TEST_COURSE_NAME);
        assertEquals(courseFromDb.getDescription(), TEST_COURSE_DESCRIPTION);
        Long id = courseFromDb.getId();
        assertNotNull(id);
        assertEquals(courseFromDb, courseDao.findById(connection, id).get());
        Course updatedCourse = new Course(id, NEW_COURSE_NAME, NEW_COURSE_DESCRIPTION);
        courseDao.save(connection, updatedCourse);
        assertEquals(courseDao.findById(connection, id).get(), updatedCourse);
        courseDao.deleteById(connection, id);
        assertEquals(courseDao.findAll(connection).size(), 0);
    }

    @Test
    void shouldCreateUpdateAndDeleteGroup() throws SQLException {
        assertEquals(groupDao.findAll(connection).size(), 0);
        Group group = new Group(GROUP_NAME);
        groupDao.save(connection, group);
        List<Group> groups = groupDao.findAll(connection);
        assertEquals(groups.size(),1);
        Group groupFromDb = groups.get(0);
        assertEquals(groupFromDb.getName(), GROUP_NAME);
        Long id = groupFromDb.getId();
        assertNotNull(id);
        assertEquals(groupFromDb, groupDao.findById(connection, id).get());
        Group updatedGroup = new Group(id, NEW_GROUP_NAME);
        groupDao.save(connection, updatedGroup);
        assertEquals(updatedGroup, groupDao.findByName(connection, NEW_GROUP_NAME).get());
        assertEquals(groupDao.findById(connection, id).get(), updatedGroup);
        groupDao.deleteById(connection, id);
        assertEquals(groupDao.findAll(connection).size(), 0);
    }
}