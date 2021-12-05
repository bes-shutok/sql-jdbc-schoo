package ua.com.foxminded.sql.jdbc.school.dao.impl;

import ua.com.foxminded.sql.jdbc.school.dao.AbstractCrudDao;
import ua.com.foxminded.sql.jdbc.school.dao.CourseDao;
import ua.com.foxminded.sql.jdbc.school.model.Course;
import ua.com.foxminded.sql.jdbc.school.utils.SqlUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    protected void create(Connection con, Course entity) throws SQLException {
        SqlUtils.executeDmlQuery(
                con,
                CREATE_COURSE,
                new Object[] {entity.getName(), entity.getDescription()}
        );
    }

    @Override
    protected void update(Connection con, Course entity) throws SQLException {
        SqlUtils.executeDmlQuery(
                con,
                UPDATE_COURSE,
                new Object[] {entity.getName(), entity.getDescription(), entity.getId()}
        );
    }

    @Override
    public void deleteById(Connection con, Long id) throws SQLException {
        SqlUtils.executeDmlQuery(con, DELETE_COURSE, new Object[] {id});
    }

    @Override
    public List<Course> findAll(Connection con) throws SQLException {
        return SqlUtils.executeQuery(con, ALL_COURSES, new Object[0], this::getCourses);
    }

    /**
     * Same as {@link #findAll(Connection)}
     */
    @Override
    public Optional<Course> findById(Connection con, Long id) throws SQLException {
        return SqlUtils.executeQuery(
                con,
                FIND_COURSE_BY_ID,
                new Object[] {id},
                resultSet -> SqlUtils.getOptionalResult(resultSet, this::getCourse)
        );
    }

    private List<Course> getCourses(ResultSet resultSet) throws SQLException {
        List<Course> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(getCourse(resultSet));
        }
        return list;
    }

    private Course getCourse(ResultSet resultSet) throws SQLException {
        return new Course(
                resultSet.getLong(Course.COURSE_ID),
                resultSet.getString(Course.COURSE_NAME),
                resultSet.getString(Course.COURSE_DESCRIPTION)
        );
    }

}
