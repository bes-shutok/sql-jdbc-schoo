package ua.com.foxminded.sql.jdbc.school.dao.impl;

import org.junit.jupiter.api.Test;
import ua.com.foxminded.sql.jdbc.school.model.Course;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CourseDaoImplTest extends DaoTest {

    /**
     * {@link CourseDaoImpl#create(Connection, Course)} is invoked in the parent class
     * in {@link DaoTest#generateTestingData()}
     */
    @Test
    void shouldCreateAndFindAll() throws SQLException {
        try (Connection con = datasource.getConnection()) {
            List<Course> courses = courseDao.findAll(con);
            assertEquals(1, courses.size());
            Course foundCourse = courses.get(0);
            Course expectedCourse = new Course(foundCourse.getId(), TEST_COURSE_NAME, TEST_COURSE_DESCRIPTION);
            assertEquals(expectedCourse, foundCourse);
        }
    }

    @Test
    void shouldUpdate() throws SQLException {
        try (Connection con = datasource.getConnection()) {
            Course updatedCourse =
                    new Course(
                            courseDao.findAll(con).get(0).getId(),
                            NEW_COURSE_NAME,
                            NEW_COURSE_DESCRIPTION
                    );
            courseDao.save(con, updatedCourse);
            Optional<Course> foundCourse = courseDao.findById(con, updatedCourse.getId());
            assertTrue(foundCourse.isPresent());
            assertEquals(updatedCourse, foundCourse.get());
        }
    }

    @Test
    void shouldDeleteById() throws SQLException {
        try (Connection con = datasource.getConnection()) {
            Long id = courseDao.findAll(con).get(0).getId();
            assertNotNull(id);
            courseDao.deleteById(con, id);
            assertTrue(courseDao.findById(con, id).isEmpty());
            assertTrue(courseDao.findAll(con).isEmpty());
        }
    }

    @Test
    void shouldFindById() throws SQLException {
        try (Connection con = datasource.getConnection()) {
            Long id = courseDao.findAll(con).get(0).getId();
            assertNotNull(id);
            Optional<Course> foundCourse = courseDao.findById(con, id);
            assertTrue(foundCourse.isPresent());
            Course expectedCourse = new Course(id, TEST_COURSE_NAME, TEST_COURSE_DESCRIPTION);
            assertEquals(expectedCourse, foundCourse.get() );
            courseDao.deleteById(con, id);
            assertTrue(courseDao.findById(con, id).isEmpty());
        }
    }
}