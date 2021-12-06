# sql-jdbc-school

Create an application sql-jdbc-school that inserts/updates/deletes data in the database using JDBC.
Use PostgreSQL DB.
Tables (given types are Java types, use SQL analogs that fit the most:

```
 groups(
 	group_id int,
 	group_name string
 )

 student(
	student_id int,
	group_id int,
	first_name string,
	last_name string
)

 courses(
	course_id int,
	course_name string,
	course_description string
)
```
1. Create SQL files with data:
```
a. create user and database. Assign all privileges on the database to the user. (DB and user should be created before application runs)
b. create a file with tables creation
```

2. Create a java application
```
a. On startup, it should run SQL script with tables creation from previously created files. If tables already exist - drop them.
b. Generate test data:

* 10 groups with randomly generated names. The name should contain 2 characters, hyphen, 2 numbers
 
* Create 10 courses (math, biology, etc)
 
* 200 student. Take 20 first names and 20 last names and randomly combine them to generate student.

* Randomly assign student to groups. Each group could contain from 10 to 30 student. It is possible that some groups will be without student or student without groups

* Create relation MANY-TO-MANY between tables STUDENTS and COURSES. Randomly assign from 1 to 3 courses for each student
```
3. Write SQL Queries, it should be available from the application menu:
```
   a. Find all groups with less or equals student count
   b. Find all student related to course with given name
   c. Add new student
   d. Delete student by STUDENT_ID
   e. Add a student to the course (from a list)
   f. Remove the student from one of his or her courses
```
#Guidelines

###Datasource
In order to gain control of database access and remove credentials access scattered all over the code, we should implement single source of db connections.

###Interface
```
public interface Datasource extends Closeable {
Connection getConnection() throws SQLException;
}
```
###Simple implementation
```
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SimpleDatasource implements Datasource {
public final static String DRIVER_CLASS = "datasource.driver-class";
public final static String JDBC_URL = "datasource.jdbc-url";
public final static String USERNAME = "datasource.username";
public final static String PASSWORD = "datasource.password";

    private final Driver driver;

    private final String jdbcUrl;
    private final String username;
    private final String password;

    public SimpleDatasource(Properties properties) {
        String className = properties.getProperty(DRIVER_CLASS);
        this.jdbcUrl = properties.getProperty(JDBC_URL);
        this.username = properties.getProperty(USERNAME);
        this.password = properties.getProperty(PASSWORD);

        try {
            @SuppressWarnings("unchecked")
            Class<Driver> clazz = (Class<Driver>) Class.forName(className);
            driver = clazz.newInstance();
            DriverManager.registerDriver(driver);
        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    @Override
    public void close() {
        try {
            DriverManager.deregisterDriver(driver);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }

    }
}
```
###Connection pool
Because connection creation is a very expensive operation, you should consider to use connection pool

###Transactions
To maintain consistency of db operations, we should care about transactions

###Naive implementation
```
void complexOperation() throws SQLException {
try (Connection connection = datasource.getConnection()) {
// disable auto-commit
connection.setAutoCommit(false);
try {
//....
//....
//multiple operations

                // no errors so far? finalize transaction
                connection.commit();
            } catch (Exception e) {
                // one of operations failed - rollback all changes
                connection.rollback();
                throw new SQLException(e);
            }
        }
    } 
```
###Shortcut in a functional way
```
import java.sql.Connection;
import java.sql.SQLException;

public class TransactionUtils {

    public static void transaction(Datasource datasource, ConnectionConsumer consumer) throws SQLException {
        try (Connection connection = datasource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                consumer.consume(connection);
                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw new SQLException("Exception in transaction", e);
            }
        }
    }

    public interface ConnectionConsumer {
        void consume(Connection connection) throws Exception;
    }
}
```
Now we can rewrite code above in a compact way
```
void complexOperation() throws SQLException {
transaction(datasource, connection -> {
//....
//....
//multiple operations
});
}
```
###Running sql scripts
Helper class

```
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
}
```
and usage
```
// setup database
SqlUtils.executeSqlScriptFile(datasource, "sql/init_schema.sql");`
```
###Dao structure

###CRUD dao interface
```
public interface CrudDao<T extends Entity<K>, K> {

    // abscence of result is not always an error
    Optional<T> findById(Connection connection, K id) throws SQLException;

    List<T> findAll(Connection connection) throws SQLException;
    
    // we treat create and update operation in a single method 
    T save(Connection connection, T entity) throws SQLException;

    void deleteById(Connection connection, K id) throws SQLException;
}
```

###AbstractDao
```
public abstract class AbstractCrudDao<T extends Entity<K>, K> implements CrudDao<T, K> {`

    @Override
    public T save(Connection connection, T entity) throws SQLException {

        // if id==null then we need to create new row in db otherwise - update existing one
        return entity.getId() == null ? create(connection, entity) : update(connection, entity);
    }

    protected abstract T create(Connection connection, T entity) throws SQLException;

    protected abstract T update(Connection connection, T entity) throws SQLException;
}
```

###Entity Dao interface
```
public interface GroupDao extends CrudDao<Group, Long> {

    // any specific methods declared here 
    Optional<Group> findByName(Connection connection, String name) throws SQLException;
}
```

###Entity Dao implementation
```
public class GroupDaoImpl extends AbstractCrudDao<Group, Long> implements GroupDao {
//...
}
```

###Project structure
```
public class SchoolApp implements Closeable {

    private final Datasource datasource;
    private final GroupDaoImpl groupDao;

    public SchoolApp(Datasource datasource) throws SQLException {
        this.datasource = datasource;

        // setup database
        SqlUtils.executeSqlScriptFile(datasource, "sql/init_schema.sql");

        // setup dao components
        this.groupDao = new GroupDaoImpl();
    }

    private void run() throws SQLException {
        // fill db with generated data
        transaction(datasource, (connection -> new Generator(groupDao).generateData(connection, 10)));

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
```