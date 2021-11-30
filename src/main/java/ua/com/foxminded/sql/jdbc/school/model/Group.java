package ua.com.foxminded.sql.jdbc.school.model;

import java.util.Objects;

public class Group extends LongEntity {

    public static final String TABLE_NAME = "groups";
    public static final String GROUP_ID = "group_id";
    public static final String GROUP_NAME = "group_name";

    private final String name;

    public Group(Long id, String name) {
        super(id);
        this.name = name;
    }

    public Group(String name) {
        this(null, name);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Group group = (Group) o;
        return Objects.equals(name, group.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }

    @Override
    public String toString() {
        return String.format("%n%s (%s %d)", name, GROUP_ID, getId());
    }
}
