package ua.com.foxminded.sql.jdbc.school;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import ua.com.foxminded.sql.jdbc.school.dao.Datasource;
import ua.com.foxminded.sql.jdbc.school.dao.SimpleDatasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PostgresDatabaseContainer extends PostgreSQLContainer<PostgresDatabaseContainer> {
    private static final String DRIVER_CLASS_NAME = "driver-class-name";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "password";
    private static final String IMAGE_VERSION = "postgres:13";

    private static PostgresDatabaseContainer container;

    private PostgresDatabaseContainer() {
        super(IMAGE_VERSION);
    }

    public static PostgresDatabaseContainer getInstance() {
        if (container == null) {
            container = new PostgresDatabaseContainer();
            container.start();
        }
        return container;
    }

    public static SimpleDatasource setupDB() {
        PostgresDatabaseContainer container = PostgresDatabaseContainer.getInstance();

        Properties properties = new Properties();
        properties.setProperty(DRIVER_CLASS_NAME, container.getDriverClassName());
        properties.setProperty(SimpleDatasource.JDBC_URL, container.getJdbcUrl());
        properties.setProperty(USERNAME, container.getUsername());
        properties.setProperty(PASSWORD, container.getPassword());

        return new SimpleDatasource(properties);
    }

    @Test
    void testContainers() throws SQLException {
        Datasource datasource = setupDB();
        test(datasource);
    }
    static void test(Datasource datasource) throws SQLException {
        try (Connection connection = datasource.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    assertTrue(rs.next());
                    int result = rs.getInt(1);
                    assertEquals(1, result);
                }
            }
        }
    }
}