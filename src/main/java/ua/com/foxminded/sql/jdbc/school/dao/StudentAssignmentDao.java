package ua.com.foxminded.sql.jdbc.school.dao;

import ua.com.foxminded.sql.jdbc.school.model.StudentAssignment;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface StudentAssignmentDao {


    // absence of result is not always an error
    Optional<StudentAssignment> findByIds(Connection connection, Long studentId, Long courseId) throws SQLException;

    List<StudentAssignment> findAll(Connection connection) throws SQLException;

    // we treat create and update operation in a single method
    StudentAssignment save(Connection connection, StudentAssignment entity) throws SQLException;

    void deleteByIds(Connection connection, Long studentId, Long courseId) throws SQLException;

}
