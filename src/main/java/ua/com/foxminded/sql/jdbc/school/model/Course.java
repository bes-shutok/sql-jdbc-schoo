package ua.com.foxminded.sql.jdbc.school.model;

import java.util.Objects;

public class Course extends LongEntity {

    public static final String COURSES_TABLE_NAME = "courses";
    public static final String COURSE_ID = "course_id";
    public static final String COURSE_NAME = "course_name";
    public static final String COURSE_DESCRIPTION = "course_description";

    private final String name;
    private final String description;

    public Course(Long id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
    }

    public Course(String name, String description) {
        this(null, name, description);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course course)) return false;
        if (!super.equals(o)) return false;
        return getName().equals(course.getName()) && getDescription().equals(course.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getName(), getDescription());
    }
}
