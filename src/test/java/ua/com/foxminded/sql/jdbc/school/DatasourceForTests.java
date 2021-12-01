package ua.com.foxminded.sql.jdbc.school;

import ua.com.foxminded.sql.jdbc.school.dao.Datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatasourceForTests implements Datasource {
    public static final String DRIVER_CLASS_NAME = "driver-class-name";
    public static final String URL = "url";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    private final String url;
    private final String username;
    private final String password;

    public DatasourceForTests(Properties properties) throws ClassNotFoundException {
        Class.forName(properties.getProperty(DRIVER_CLASS_NAME));
        url = properties.getProperty(URL);
        username = properties.getProperty(USERNAME);
        password = properties.getProperty(PASSWORD);
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username,
                password);
    }

    @Override
    public void close() {
    }
}
