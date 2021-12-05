package ua.com.foxminded.sql.jdbc.school.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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


    public static List<Group> getGroups(ResultSet resultSet) throws SQLException {
        List<Group> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(getGroup(resultSet));
        }
        return list;
    }
    public static  Group getGroup(ResultSet resultSet) throws SQLException {
        return new Group(
                resultSet.getLong(Group.GROUP_ID),
                resultSet.getString(Group.GROUP_NAME)
        );
    }

    public static List<Group> getGroupsById(ResultSet resultSet) throws SQLException {
        List<Group> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(getGroupById(resultSet));
        }
        return list;
    }
    public static  Group getGroupById(ResultSet resultSet) throws SQLException {
        return new Group(
                resultSet.getLong(Group.GROUP_ID),
                null
        );
    }
}
