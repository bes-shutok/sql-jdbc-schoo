package ua.com.foxminded.sql.jdbc.school.model;

public interface Entity<K> {

    K getId();
    void setId(K id);
}
