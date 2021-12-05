package ua.com.foxminded.sql.jdbc.school.dao.impl;

import org.junit.jupiter.api.Test;
import ua.com.foxminded.sql.jdbc.school.model.StudentAssignment;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StudentAssignmentDaoImplTest extends DaoTest {

    @Test
    void shouldFindAll() throws SQLException {
        try (Connection con = datasource.getConnection()) {
            List<StudentAssignment> foundStudentAssignment = studentAssignmentDao.findAll(con);
            assertEquals(1, foundStudentAssignment.size());
            assertEquals(
                    new StudentAssignment(
                            studentDao.findAll(con).get(0).getId(),
                            courseDao.findAll(con).get(0).getId()
                    ),
                    foundStudentAssignment.get(0)
            );
        }
    }

    @Test
    void shouldFindByIds() throws SQLException {
        try (Connection con = datasource.getConnection()) {
            Optional<StudentAssignment> foundStudentAssignment = studentAssignmentDao.findByIds(
                    con,
                    studentDao.findAll(con).get(0).getId(),
                    courseDao.findAll(con).get(0).getId()
            );
            assertTrue(foundStudentAssignment.isPresent());
            assertEquals(
                    new StudentAssignment(
                            studentDao.findAll(con).get(0).getId(),
                            courseDao.findAll(con).get(0).getId()
                    ),
                    foundStudentAssignment.get()
            );
            studentAssignmentDao.deleteByIds(
                    con,
                    studentDao.findAll(con).get(0).getId(),
                    courseDao.findAll(con).get(0).getId()
            );
            foundStudentAssignment = studentAssignmentDao.findByIds(
                    con,
                    studentDao.findAll(con).get(0).getId(),
                    courseDao.findAll(con).get(0).getId()
            );
            assertTrue(foundStudentAssignment.isEmpty());
        }
    }

    @Test
    void shouldDeleteByIdAndCreate() throws SQLException {
        try (Connection con = datasource.getConnection()) {
            List<StudentAssignment> studentAssignments = studentAssignmentDao.findAll(con);
            assertEquals(1, studentAssignments.size());
            StudentAssignment foundStudentAssignment = studentAssignments.get(0);
            long studentId = foundStudentAssignment.getStudentId();
            long courseId = foundStudentAssignment.getCourseId();
            studentAssignmentDao.deleteByIds(con, studentId, courseId);
            assertEquals(0, studentAssignmentDao.findAll(con).size());
            studentAssignmentDao.create(con, new StudentAssignment(studentId, courseId));
            Optional<StudentAssignment> optionalStudentAssignment =
                    studentAssignmentDao.findByIds(con, studentId, courseId);
            assertTrue(optionalStudentAssignment.isPresent());
            assertEquals(foundStudentAssignment, optionalStudentAssignment.get());
        }
    }
}