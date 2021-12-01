package ua.com.foxminded.sql.jdbc.school.dao.impl;

import ua.com.foxminded.sql.jdbc.school.dao.AbstractCrudDao;
import ua.com.foxminded.sql.jdbc.school.dao.CourseDao;
import ua.com.foxminded.sql.jdbc.school.model.Course;
import ua.com.foxminded.sql.jdbc.school.utils.SqlUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseDaoImpl extends AbstractCrudDao<Course, Long> implements CourseDao {

    private static final String CREATE_COURSE = "INSERT INTO " + Course.TABLE_NAME + " (" +
            Course.COURSE_NAME + ", " + Course.COURSE_DESCRIPTION + ") " + "VALUES (?, ?);";

    private static final String UPDATE_COURSE = "UPDATE " + Course.TABLE_NAME + " SET " + Course.COURSE_NAME
            + " = ?, " + Course.COURSE_DESCRIPTION + " = ? " + "WHERE " + Course.COURSE_ID + " = ?;";

    private static final String ALL_COURSES = "SELECT * FROM " + Course.TABLE_NAME + ";";

    private static final String FIND_COURSE_BY_ID = "SELECT * FROM " + Course.TABLE_NAME
            + " WHERE " + Course.COURSE_ID + " = ?;";

    private static final String DELETE_COURSE = "DELETE FROM " + Course.TABLE_NAME + " WHERE " + Course.COURSE_ID
            + " = ?;";

    @Override
    protected Course create(Connection connection, Course entity) throws SQLException {
        SqlUtils.executeUpdate(connection, CREATE_COURSE, entity.getName(), entity.getDescription());
        return entity;
    }

    @Override
    protected Course update(Connection connection, Course entity) throws SQLException {
        SqlUtils.executeUpdate(connection, UPDATE_COURSE,  entity.getName(), entity.getDescription(),
                entity.getId());
        return entity;
    }

    @Override
    public void deleteById(Connection connection, Long id) throws SQLException {
        SqlUtils.executeUpdate(connection, DELETE_COURSE, id);
    }

    /**
     * Here we need to parse {@link ResultSet} at place. We still could use {@link SqlUtils} method by providing
     * it with {@link ResultSet} mapper but in this case we would be providing parameters as Objects
     * (to generalize the method) and thus lose type validation. All in all it is more readable as it is here
     */
    @Override
    public List<Course> findAll(Connection con) throws SQLException {
        List<Course> result = new ArrayList<>();
        try (PreparedStatement st = con.prepareStatement(ALL_COURSES)) {
            ResultSet resultSet = st.executeQuery();
            while (resultSet.next()) {
                result.add(
                        new Course(
                                resultSet.getLong(Course.COURSE_ID),
                                resultSet.getString(Course.COURSE_NAME),
                                resultSet.getString(Course.COURSE_DESCRIPTION)
                        )
                );
            }
        }
        return result;
    }

    /**
     * Same as {@link #findAll(Connection)}
     */
    @Override
    public Optional<Course> findById(Connection con, Long id) throws SQLException {
        Optional<Course> result = Optional.empty();
        try (PreparedStatement st = con.prepareStatement(FIND_COURSE_BY_ID)) {
            st.setLong(1, id);
            ResultSet resultSet = st.executeQuery();
            if (resultSet.next()) {
                result = Optional.of(
                        new Course(
                                id,
                                resultSet.getString(Course.COURSE_NAME),
                                resultSet.getString(Course.COURSE_DESCRIPTION)
                        )
                );
            }
        }
        return result;
    }
}
