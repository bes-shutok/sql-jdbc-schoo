package ua.com.foxminded.sql.jdbc.school.dao.impl;

import org.junit.jupiter.api.Test;
import ua.com.foxminded.sql.jdbc.school.model.Group;
import ua.com.foxminded.sql.jdbc.school.model.Student;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class StudentDaoImplTest extends DaoTest {

    @Test
    void shouldFindAll() throws SQLException {
        try (Connection con = datasource.getConnection()) {
            List<Student> students = studentDao.findAll(con);
            assertEquals(1, students.size());
            Student foundStudent = students.get(0);
            assertEquals(
                    new Student(foundStudent.getId(), STUDENT_FIRST_NAME, STUDENT_LAST_NAME, null),
                    foundStudent
            );
        }
    }

    @Test
    void shouldUpdate() throws SQLException {
        try (Connection con = datasource.getConnection()) {
            List<Student> students = studentDao.findAll(con);
            assertEquals(1, students.size());
            Student foundStudent = students.get(0);
            List<Group> groups = groupDao.findAll(con);
            assertEquals(1, groups.size());
            Group foundGroup = groups.get(0);
            Student updatedStudent = new Student(
                    foundStudent.getId(),
                    STUDENT_FIRST_NAME,
                    STUDENT_LAST_NAME,
                    foundGroup.getId()
            );
            foundStudent.setGroupId(foundGroup.getId());
            studentDao.save(con, updatedStudent);
            assertEquals(updatedStudent, studentDao.findAll(con).get(0));
        }
    }

    @Test
    void shouldFindById() throws SQLException {
        try (Connection con = datasource.getConnection()) {
            List<Student> students = studentDao.findAll(con);
            assertEquals(1, students.size());
            Student foundStudent = students.get(0);
            Long id = foundStudent.getId();
            assertNotNull(id);
            Optional<Student> optionalStudent = studentDao.findById(con, id);
            assertTrue(optionalStudent.isPresent());
            assertEquals(foundStudent, optionalStudent.get());
            studentDao.deleteById(con, id);
            assertTrue(studentDao.findById(con, id).isEmpty());
        }
    }

    @Test
    void shouldDeleteByIdAndCreate() throws SQLException {
        try (Connection con = datasource.getConnection()) {
            List<Student> students = studentDao.findAll(con);
            assertEquals(1, students.size());
            Student foundStudent = students.get(0);
            studentDao.deleteById(con, foundStudent.getId());
            students = studentDao.findAll(con);
            assertEquals(0, students.size());
            Student newStudent = new Student(STUDENT_FIRST_NAME, STUDENT_LAST_NAME, null);
            studentDao.save(con, newStudent);
            students = studentDao.findAll(con);
            assertEquals(1, students.size());
            foundStudent = students.get(0);
            assertEquals(
                    new Student(foundStudent.getId(), STUDENT_FIRST_NAME, STUDENT_LAST_NAME, null),
                    foundStudent
            );
        }
    }
}