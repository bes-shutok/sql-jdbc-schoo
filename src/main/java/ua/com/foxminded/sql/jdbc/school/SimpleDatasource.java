package ua.com.foxminded.sql.jdbc.school;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SimpleDatasource implements Datasource {
    public final static String JDBC_URL = "jdbc-url";

    private final String jdbcUrl;
    private final Properties properties;

    public SimpleDatasource(Properties properties) {
        this.properties = properties;
        this.jdbcUrl = properties.getProperty(JDBC_URL);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, properties);
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void close() throws IOException {
    }
}
