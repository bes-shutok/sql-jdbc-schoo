package ua.com.foxminded.sql.jdbc.school;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import ua.com.foxminded.sql.jdbc.school.dao.Datasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PostgresDatabaseContainer extends PostgreSQLContainer<PostgresDatabaseContainer> {
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

    public static DatasourceForTests setupDB() throws ClassNotFoundException {
        PostgresDatabaseContainer container = PostgresDatabaseContainer.getInstance();

        Properties properties = new Properties();
        properties.setProperty(DatasourceForTests.DRIVER_CLASS_NAME, container.getDriverClassName());
        properties.setProperty(DatasourceForTests.URL, container.getJdbcUrl());
        properties.setProperty(DatasourceForTests.USERNAME, container.getUsername());
        properties.setProperty(DatasourceForTests.PASSWORD, container.getPassword());

        return new DatasourceForTests(properties);
    }

    @Test
    void testContainers() throws SQLException, ClassNotFoundException {
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