package ua.com.foxminded.sql.jdbc.school.utils;

import ua.com.foxminded.sql.jdbc.school.dao.Datasource;

import java.sql.*;

import static ua.com.foxminded.sql.jdbc.school.utils.TransactionUtils.transaction;

public class SqlUtils {
    public static void executeSqlScript(Connection connection, String sqlScript) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sqlScript);
        }
    }

    public static void executeSqlScriptFile(Datasource datasource, String fileName) throws SQLException {
        transaction(datasource, (connection) ->
                SqlUtils.executeSqlScript(connection, ResourceUtils.loadTextFileFromResources(fileName)));
    }

    public static void executeUpdate(Connection con, String statement, Object... params) throws SQLException {
        try (PreparedStatement st = con.prepareStatement(statement)) {
            for(int i = 0; i < params.length; i++) {
                st.setObject(i + 1, params[i]);
            }
            st.executeUpdate();
        }
    }
}
