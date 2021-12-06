package ua.com.foxminded.sql.jdbc.school.utils;

import ua.com.foxminded.sql.jdbc.school.dao.Datasource;

import java.sql.*;
import java.util.Optional;
import java.util.function.Function;

import static ua.com.foxminded.sql.jdbc.school.utils.TransactionUtils.transaction;

public class SqlUtils {
    public static void executeSqlScript(Connection con, String sqlScript) throws SQLException {
        try (Statement statement = con.createStatement()) {
            statement.executeUpdate(sqlScript);
        }
    }

    public static void executeSqlScriptFile(Datasource datasource, String fileName) throws SQLException {
        transaction(datasource, (con) ->
                SqlUtils.executeSqlScript(con, ResourceUtils.loadTextFileFromResources(fileName)));
    }

    public static <T> T executeQuery(
            Connection con, String statement, Object[] params, ResultSetMapper<T> mapper
    ) throws SQLException {
        try (PreparedStatement st = con.prepareStatement(statement)) {
            for (int i = 0; i < params.length; i++) {
                st.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = st.executeQuery()) {
                try {
                    return mapper.apply(rs);
                } catch (Exception e) {
                    throw new SQLException(e);
                }
            }
        }
    }
    public static void executeDmlQuery(Connection con, String statement, Object[] params) throws SQLException {
        try (PreparedStatement st = con.prepareStatement(statement)) {
            for (int i = 0; i < params.length; i++) {
                st.setObject(i + 1, params[i]);
            }
            st.executeUpdate();
        }
    }

    public static <T> Optional<T> getOptionalResult(
            ResultSet resultSet,
            SqlUtils.ResultSetMapper<T> mapper
    ) throws SQLException {

        if (resultSet.next()) {
            return Optional.of(mapper.apply(resultSet));
        } else {
            return Optional.empty();
        }
    }

    /**
     * This interface cannot be replaced or extend {@link Function} because its {@link Function#apply(Object)} method
     * is not supposed to throw any exceptions.
      */
    @FunctionalInterface
    public interface ResultSetMapper <T> {
        T apply(ResultSet resultSet) throws SQLException;
    }

}
