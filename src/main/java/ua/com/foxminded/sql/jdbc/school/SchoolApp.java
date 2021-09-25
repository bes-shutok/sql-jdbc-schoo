package ua.com.foxminded.sql.jdbc.school;

import ua.com.foxminded.sql.jdbc.school.dao.*;
import ua.com.foxminded.sql.jdbc.school.utils.SqlUtils;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import static ua.com.foxminded.sql.jdbc.school.utils.ResourceUtils.loadPropertiesFromResources;
import static ua.com.foxminded.sql.jdbc.school.utils.TransactionUtils.transaction;

public class SchoolApp implements Closeable {

    private final Datasource datasource;
    private final GroupDao groupDao;
    private final StudentDao studentDao;
    private final CourseDao courseDao;
    private final StudentAssignmentImpl studentAssignmentDao;

    private final Generator generator;

    public SchoolApp(Datasource datasource) throws SQLException {
        this.datasource = datasource;

        // setup database
        SqlUtils.executeSqlScriptFile(datasource, "sql/init_schema.sql");

        // setup dao components
        this.groupDao = new GroupDaoImpl();
        this.studentDao = new StudentDaoImpl();
        this.courseDao = new CourseDaoImpl();
        this.studentAssignmentDao = new StudentAssignmentImpl();
        this.generator = new Generator(groupDao, studentDao, courseDao, studentAssignmentDao);
    }

    private void run() throws SQLException {
        // fill db with generated data
        transaction(datasource, (connection -> generator.generateData(connection, 10, 200)));
        System.out.println("Test data generated");

        // show menu in a loop
    }

    @Override
    public void close() throws IOException {
        try {
            SqlUtils.executeSqlScriptFile(datasource, "sql/drop_schema.sql");
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    public static void main(String[] args) throws IOException, SQLException {
        Properties databaseProperties = loadPropertiesFromResources("db.properties");
        try (
                Datasource datasource = new SimpleDatasource(databaseProperties);
                SchoolApp schoolApp = new SchoolApp(datasource)
        ) {
            schoolApp.run();
        }
    }
}

